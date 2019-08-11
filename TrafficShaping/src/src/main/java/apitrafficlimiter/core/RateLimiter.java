package src.main.java.apitrafficlimiter.core;


import src.main.java.apitrafficlimiter.enums.CustomTimeUnit;
import src.main.java.apitrafficlimiter.model.RateLimitRequest;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class RateLimiter {

    RequestDelayQueue queue ;
    LimitRuleDataSet limitRuleDataSet ;

    public RateLimiter(RequestDelayQueue queue, LimitRuleDataSet limitRuleDataSet) {
        this.queue = queue;
        this.limitRuleDataSet = limitRuleDataSet;
    }

    public  Boolean isRequestAccepted(RateLimitRequest request) {
        Boolean isAccepted =false, isRetry;

        do{
           isRetry = pollInBatch();
             if (limitRuleDataSet.ifLimitAvailableDecrement(request)) {
                 queue.offerRequest(request);
                 isAccepted = true;
             break;
            }
        }while(isRetry);

        request.setStatus(isAccepted);
        return isAccepted;
    }

    private Boolean  pollInBatch() {
        int count = Math.abs(queue.size() / 4);
        while (count > -1) {
            RateLimitRequest expireRequest = queue.pollAndOfferWithNextTimeUnit();
            if (expireRequest == null) {
                return false;
            }
            limitRuleDataSet.increaseLimit(expireRequest);
            count--;
        }
        return true;
    }

    public ConcurrentHashMap<String,ConcurrentHashMap<CustomTimeUnit,AtomicInteger>> getAvailableLimit(){
        while(pollInBatch()){}
        return limitRuleDataSet.getRuleMap();

    }

    public LimitRuleDataSet getLimitRuleDataSet() {
        return limitRuleDataSet;
    }
}

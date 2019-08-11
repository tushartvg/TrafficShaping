package src.main.java.apitrafficlimiter.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import src.main.java.apitrafficlimiter.enums.CustomTimeUnit;
import src.main.java.apitrafficlimiter.model.RateLimitRequest;
import src.main.java.apitrafficlimiter.service.TrafficShapingGateway;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantReadWriteLock;


public class LimitRuleDataSet {
    private static final Logger LOG = LoggerFactory.getLogger(LimitRuleDataSet.class);
    private ConcurrentHashMap<RateLimitRequest,List<ConcurrentHashMap<CustomTimeUnit,AtomicInteger>>> requestRuleCache = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String,ConcurrentHashMap<CustomTimeUnit,AtomicInteger>> ruleMap ;
    private ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    public LimitRuleDataSet(ConcurrentHashMap<String,ConcurrentHashMap<CustomTimeUnit,AtomicInteger>> ruleMap){
        this.ruleMap = ruleMap;
    }

    public ConcurrentHashMap<String, ConcurrentHashMap<CustomTimeUnit, AtomicInteger>> getRuleMap() {
        return ruleMap;
    }

    public  Boolean ifLimitAvailableDecrement(RateLimitRequest request){
        readWriteLock.writeLock().lock();
        try {
            if (isLimitAvailable(request)) {
                decrementLimit(request);
                return true;
            }
            return false;
        }finally {
            readWriteLock.writeLock().unlock();
        }
    }

    private boolean isLimitAvailable(RateLimitRequest request){
        ArrayList<AtomicInteger> limits = new ArrayList<>();
        for(ConcurrentHashMap<CustomTimeUnit,AtomicInteger> rule : getRuleForRequest(request)){
            limits.addAll(rule.values());
        }

        for (AtomicInteger limit:limits) {
            if (limit.get() <= 0) {
                LOG.debug("Request failed to execute, Request= {},AvailableLimit= {}",request.toResponseString(),getRuleForRequest(request).toString());
                return false;
            }
        }

        return true;
    }

    private void decrementLimit(RateLimitRequest request){

            ArrayList<AtomicInteger> limits = new ArrayList<>();
            for (ConcurrentHashMap<CustomTimeUnit, AtomicInteger> rule : getRuleForRequest(request)) {
                limits.addAll(rule.values());
            }
            for (AtomicInteger limit : limits) {
                limit.decrementAndGet();
            }

    }

    public void increaseLimit(RateLimitRequest request){
        readWriteLock.readLock().lock();
        try {
          CustomTimeUnit timeUnit = request.getCustomTimeUnits().first();
          for(ConcurrentHashMap<CustomTimeUnit,AtomicInteger> rule :getRuleForRequest(request)){
             if(rule.containsKey(timeUnit)){
                rule.get(timeUnit).incrementAndGet();
            }
         }
        }finally {
            readWriteLock.readLock().unlock();
        }
    }

    public List<ConcurrentHashMap<CustomTimeUnit,AtomicInteger>> getRuleForRequest(RateLimitRequest request){
        List<ConcurrentHashMap<CustomTimeUnit,AtomicInteger>> ruleList;
        ruleList = requestRuleCache.get(request);
        if(ruleList ==null) {
            ruleList = new ArrayList<>();
            if (ruleMap.get(request.getApiName()) != null) {
                ruleList.add(ruleMap.get(request.getApiName()));
            }
            if (ruleMap.get(request.getMethodName()) != null) {
                ruleList.add(ruleMap.get(request.getMethodName()));
            }
            if (ruleMap.get(request.getClientName()) != null) {
                ruleList.add(ruleMap.get(request.getClientName()));
            }
            requestRuleCache.put(request,ruleList);
        }
        return ruleList;
    }

}

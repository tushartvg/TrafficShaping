package src.main.java.apitrafficlimiter.helper;

import com.google.gson.reflect.TypeToken;
import src.main.java.apitrafficlimiter.core.LimitRuleDataSet;
import src.main.java.apitrafficlimiter.core.RateLimiter;
import src.main.java.apitrafficlimiter.core.RequestDelayQueue;
import src.main.java.apitrafficlimiter.enums.CustomTimeUnit;
import src.main.java.apitrafficlimiter.model.RateLimitRequest;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import com.google.gson.Gson;
import src.main.java.apitrafficlimiter.model.RateLimitResponse;


public class RequestBuilder {
   private final  Gson gson = new Gson();

    private   void addTimeUnitsFromRules(RateLimitRequest request, LimitRuleDataSet dataSet){
        for(ConcurrentHashMap<CustomTimeUnit,AtomicInteger>  rule : dataSet.getRuleForRequest(request)){
            for (CustomTimeUnit key : rule.keySet())
                request.getCustomTimeUnits().add(key);
        }
    }

    public  void build(RateLimitRequest request, LimitRuleDataSet dataSet){
      addTimeUnitsFromRules(request,dataSet);
    }

    public Boolean hasAnyRuleToApply(RateLimitRequest request,LimitRuleDataSet dataSet){
        if(dataSet.getRuleForRequest(request).size()==0)
            return false;
        return true;
    }

    public ConcurrentHashMap<String,RateLimiter>  jsonToMap(String jsonStr){
        Type type = new TypeToken<Map<String,Map<String, Map<String,Integer>>>>(){}.getType();

        Map<String,Map<String, Map<String,Integer>>> clientMap = gson.fromJson(jsonStr, type);
        ConcurrentHashMap<String,RateLimiter>  clientRateLimitDataMap = new ConcurrentHashMap<>();
        //
        for(String clientName : clientMap.keySet())
        {
            Map<String,Map<String,Integer>> ruleMap = clientMap.get(clientName);
            ConcurrentHashMap<String,ConcurrentHashMap<CustomTimeUnit,AtomicInteger>> concurrentRuleMap = new ConcurrentHashMap<>();
            for(String rule : ruleMap.keySet())    {
                Map<String,Integer> limitMap = ruleMap.get(rule);
                ConcurrentHashMap<CustomTimeUnit,AtomicInteger> concurrentlimitMap = new ConcurrentHashMap<>();
                 for (String limit : limitMap.keySet()){
                        CustomTimeUnit timeUnit = CustomTimeUnit.getTimeUnitByCode(limit);
                        AtomicInteger atomicInteger = new AtomicInteger(limitMap.get(limit));
                        concurrentlimitMap.put(timeUnit,atomicInteger);
                    }
                 concurrentRuleMap.put(rule,concurrentlimitMap);
            }
            clientRateLimitDataMap.put(clientName,new RateLimiter(new RequestDelayQueue(),new LimitRuleDataSet(concurrentRuleMap)));
        }
        return clientRateLimitDataMap;
    }
}

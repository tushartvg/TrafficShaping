package test;

import src.main.java.apitrafficlimiter.enums.CustomTimeUnit;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class TestHelper {

    public static  ConcurrentHashMap<String,ConcurrentHashMap<CustomTimeUnit,AtomicInteger>>  getConfigForClient(){

       ConcurrentHashMap<CustomTimeUnit,AtomicInteger>  clientRuleMap = new ConcurrentHashMap<>();
        ConcurrentHashMap<CustomTimeUnit,AtomicInteger>  getRuleMap = new ConcurrentHashMap<>();
        ConcurrentHashMap<CustomTimeUnit,AtomicInteger> postRuleMap = new ConcurrentHashMap<>();
        ConcurrentHashMap<CustomTimeUnit,AtomicInteger>  payRuleMap = new ConcurrentHashMap<>();
        ConcurrentHashMap<CustomTimeUnit,AtomicInteger>  statusRuleMap = new ConcurrentHashMap<>();

        clientRuleMap.put(CustomTimeUnit.HOURS,new AtomicInteger(90));
        //  clientRuleMap.put(CustomTimeUnit.WEEKS,new AtomicInteger(900000));
        // clientRuleMap.put(CustomTimeUnit.MONTHS,new AtomicInteger(10000000));


        getRuleMap.put(CustomTimeUnit.SECONDS,new AtomicInteger(10));
        getRuleMap.put(CustomTimeUnit.MINUTES,new AtomicInteger(40));
        //    getRuleMap.put(CustomTimeUnit.WEEKS,new AtomicInteger(700));

        postRuleMap.put(CustomTimeUnit.SECONDS,new AtomicInteger(20));
        postRuleMap.put(CustomTimeUnit.HOURS,new AtomicInteger(40));
        postRuleMap.put(CustomTimeUnit.WEEKS,new AtomicInteger(900));
        postRuleMap.put(CustomTimeUnit.MONTHS,new AtomicInteger(1000));

        statusRuleMap.put(CustomTimeUnit.SECONDS,new AtomicInteger(2));
        statusRuleMap.put(CustomTimeUnit.HOURS,new AtomicInteger(40));
        statusRuleMap.put(CustomTimeUnit.WEEKS,new AtomicInteger(900));
        statusRuleMap.put(CustomTimeUnit.MONTHS,new AtomicInteger(1000));

        payRuleMap.put(CustomTimeUnit.SECONDS,new AtomicInteger(9));
        // payRuleMap.put(CustomTimeUnit.MINUTES,new AtomicInteger(50000));
        //    payRuleMap.put(CustomTimeUnit.WEEKS,new AtomicInteger(700));

        ConcurrentHashMap<String,ConcurrentHashMap<CustomTimeUnit,AtomicInteger>> ruleMap =new ConcurrentHashMap<>();
        ruleMap.put("get",getRuleMap);
        ruleMap.put("post",postRuleMap);
        ruleMap.put("pay",payRuleMap);
        ruleMap.put("Flipkart",clientRuleMap);
        ruleMap.put("status",statusRuleMap);

        return ruleMap;

    }
}

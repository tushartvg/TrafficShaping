package test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import src.main.java.apitrafficlimiter.core.LimitRuleDataSet;
import src.main.java.apitrafficlimiter.enums.RequestStatus;
import src.main.java.apitrafficlimiter.model.RateLimitRequest;
import src.main.java.apitrafficlimiter.model.RateLimitResponse;
import src.main.java.apitrafficlimiter.service.TrafficShapingGateway;


import java.util.concurrent.TimeUnit;

public class TestCase {
    private static final Logger LOG = LoggerFactory.getLogger(LimitRuleDataSet.class);
   static TrafficShapingGateway trafficShapingGateway = TrafficShapingGateway.getInstance();

    public static Boolean testRegisterClient() throws InterruptedException {
        String jsonStr = " {\n" +
                "   \"Flipkart\": {\n" +
                "      \"post\": {\n" +
                "         \"SEC\": 20,\n" +
                "         \"WEEK\": 900\n" +
                "      },\n" +
                "      \"pay\": {\n" +
                "         \"SEC\": 9\n" +
                "      },\n" +
                "      \"Flipkart\": {\n" +
                "         \"HOUR\": 90\n" +
                "      }\n" +
                "   },\n" +
                "   \"Walmart\": {\n" +
                "      \"post\": {\n" +
                "         \"SEC\": 20,\n" +
                "         \"WEEK\": 900\n" +
                "      },\n" +
                "      \"pay\": {\n" +
                "         \"SEC\": 9\n" +
                "      },\n" +
                "      \"Walmart\": {\n" +
                "         \"HOUR\": 90\n" +
                "      }\n" +
                "   }\n" +
                "}";
        trafficShapingGateway.registerClient(jsonStr);
     LOG.info(trafficShapingGateway.getAvailableLimit(new RateLimitRequest("Flipkart")).toString());
     LOG.info(trafficShapingGateway.getAvailableLimit(new RateLimitRequest("Walmart")).toString());
     return true;
    }

      public static Boolean noRequestForMinuteAfterInitialRequest() throws InterruptedException {
        String jsonStr = "{Flipkart = {pay={MONTH=1000, MIN=4, SEC=4, WEEK=900}}}";
        trafficShapingGateway.registerClient(jsonStr);
         RateLimitResponse response = trafficShapingGateway.isRequestAccepted(new RateLimitRequest("Flipkart", "get", "pay"));
          response = trafficShapingGateway.isRequestAccepted(new RateLimitRequest("Flipkart", "get", "pay"));
          response = trafficShapingGateway.isRequestAccepted(new RateLimitRequest("Flipkart", "get", "pay"));
          response = trafficShapingGateway.isRequestAccepted(new RateLimitRequest("Flipkart", "get", "pay"));
         Thread.sleep(62000l);

        response = trafficShapingGateway.isRequestAccepted(new RateLimitRequest("Flipkart", "get", "pay"));

        LOG.info(trafficShapingGateway.getAvailableLimit(new RateLimitRequest("Flipkart")).toString());
        if(response.getStatus() ==RequestStatus.TOMANYREQUEST) {
                LOG.error("Failed");
                return Boolean.FALSE;
            }

        LOG.info("Success");
        return  Boolean.TRUE;
    }

    public static Boolean conncurrentRequesLessThanLimitPerSecond() throws InterruptedException {
        String jsonStr = "{Flipkart = {pay={MONTH=1000, HOUR=40, SEC=20, WEEK=900}}}";
        trafficShapingGateway.registerClient(jsonStr);
        for(int i=0 ;i<19; i++) {
           RateLimitResponse response = trafficShapingGateway.isRequestAccepted(new RateLimitRequest("Flipkart", "get", "pay"));
           if(response.getStatus() ==RequestStatus.TOMANYREQUEST) {
               LOG.error("Failed");
               return Boolean.FALSE;
            }
        }
        LOG.info("Success");
        return  Boolean.TRUE;
    }

    public static Boolean conncurrentRequesMoreThanLimitPerSecond() throws InterruptedException {
        String jsonStr = "{Flipkart = {pay={MONTH=1000, HOUR=40, SEC=20, WEEK=900}}}";
        trafficShapingGateway.registerClient(jsonStr);
        for(int i=0 ;i<22; i++) {
            RateLimitResponse response = trafficShapingGateway.isRequestAccepted(new RateLimitRequest("Flipkart", "get", "pay"));
            if(response.getStatus() ==RequestStatus.TOMANYREQUEST) {
                LOG.error("Success");
                return Boolean.TRUE;
            }
        }
        LOG.info("Error");
        return  Boolean.FALSE;
    }

    public static Boolean checkForRequstWhichDontHaveRule() throws InterruptedException {
        String jsonStr = "{Flipkart = {pay={MONTH=1000, HOUR=40, SEC=20, WEEK=900}}}";
        trafficShapingGateway.registerClient(jsonStr);
          RateLimitResponse response = trafficShapingGateway.isRequestAccepted(new RateLimitRequest("Flipkart", "get", "status"));
        if(response.getStatus() ==RequestStatus.TOMANYREQUEST) {
            LOG.error("Failed");
            return Boolean.FALSE;
        }

        LOG.info("Success");
        return  Boolean.TRUE;
    }


    public static Boolean checkIfPerSecondSatisfyAndPerMinuteBreak() throws InterruptedException {
        String jsonStr = "{Flipkart = {pay={MONTH=1000, MIN=40, SEC=20, WEEK=900}}}";
        trafficShapingGateway.registerClient(jsonStr);
        //adding 20 request per second
        for(int i=0 ;i<=40; i++) {
            RateLimitResponse response = trafficShapingGateway.isRequestAccepted(new RateLimitRequest("Flipkart", "get", "pay"));
            if(i==20) {
                Thread.sleep(1000l);
                LOG.debug(trafficShapingGateway.getAvailableLimit(new RateLimitRequest("Flipkart")).toString());
            }
        }
        LOG.debug(trafficShapingGateway.getAvailableLimit(new RateLimitRequest("Flipkart")).toString());

        //add 41 request on 3rd sec
        RateLimitResponse response = trafficShapingGateway.isRequestAccepted(new RateLimitRequest("Flipkart", "get", "pay"));

        if(response.getStatus() ==RequestStatus.TOMANYREQUEST) {
            LOG.error("Success");
            return Boolean.TRUE;
        }
        LOG.info("Error");
        return  Boolean.FALSE;
    }


        public static void checkMoreRequestofDifftyeoPerSecond() throws InterruptedException {
        String jsonStr ="{FlipKart = {post={MONTH=1000, HOUR=40, SEC=20, WEEK=900}, get={SEC=10, MIN=40}, pay={SEC=9}, Flipkart={HOUR=90}, status={MONTH=1000, HOUR=40, SEC=2, WEEK=900}}}";

        TrafficShapingGateway trafficShapingGateway = TrafficShapingGateway.getInstance();
      trafficShapingGateway.registerClient(jsonStr);
      for(int i=0 ; i <1;i++) {
          new Thread(() ->{
              System.out.println(trafficShapingGateway.isRequestAccepted(new RateLimitRequest("Flipkart", "get", "pay")));
            }).start();

          if(i%8 ==0) {
              System.out.println(trafficShapingGateway.isRequestAccepted(new RateLimitRequest("Flipkart", "get", "status")));
              System.out.println(trafficShapingGateway.isRequestAccepted(new RateLimitRequest("Flipkart", "post", "status")
              ));
          }

          if(i%10 == 0){
              Thread.sleep(10000L);
          }
      }


    }



    public static void main(String[] args) throws InterruptedException {
        conncurrentRequesMoreThanLimitPerSecond();
        /* conncurrentRequesLessThanLimitPerSecond();

        checkIfPerSecondSatisfyAndPerMinuteBreak();
          checkForRequstWhichDontHaveRule();

        noRequestForMinuteAfterInitialRequest();
        testRegisterClient();*/
    }
}

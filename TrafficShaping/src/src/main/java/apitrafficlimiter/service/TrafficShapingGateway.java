package src.main.java.apitrafficlimiter.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import src.main.java.apitrafficlimiter.core.RateLimiter;
import src.main.java.apitrafficlimiter.enums.RequestStatus;
import src.main.java.apitrafficlimiter.helper.RequestBuilder;
import src.main.java.apitrafficlimiter.model.RateLimitRequest;
import src.main.java.apitrafficlimiter.model.RateLimitResponse;

import java.util.concurrent.ConcurrentHashMap;

public class TrafficShapingGateway {

    private static final Logger LOG = LoggerFactory.getLogger(TrafficShapingGateway.class);
    private static final TrafficShapingGateway instance=null;
   private static final ConcurrentHashMap<String,RateLimiter>  clientRateLimitDataMap = new ConcurrentHashMap<>();
   private static final RequestBuilder requestBuilder= new RequestBuilder();
    private TrafficShapingGateway(){

    }

    public  static TrafficShapingGateway getInstance(){
       if(instance!=null)
          return instance;
       synchronized(TrafficShapingGateway.class){
          return new TrafficShapingGateway();
       }

    }

    public RateLimitResponse getAvailableLimit(RateLimitRequest request){
        RateLimitResponse rateLimitResponse = new RateLimitResponse();
        rateLimitResponse.setRequest(request);
        try {
            RateLimiter rateLimiter = clientRateLimitDataMap.get(request.getClientName());
            if (rateLimiter == null) {
                rateLimitResponse.setStatus(RequestStatus.CLIENTNOTREGISTER);
                return rateLimitResponse;
            }
            rateLimitResponse.setAvailableLimit(rateLimiter.getAvailableLimit());
            rateLimitResponse.setStatus(RequestStatus.SUCCESS);
        }catch(Exception ex){
            LOG.warn("Internal Error, Traffic Shaping might not work as expected");
            rateLimitResponse.setStatus(RequestStatus.EXCEPTION);
        }
        return rateLimitResponse;
    }

   public RateLimitResponse isRequestAccepted(RateLimitRequest request){
      RateLimitResponse rateLimitResponse = new RateLimitResponse();
      rateLimitResponse.setRequest(request);
     try {
        RateLimiter rateLimiter = clientRateLimitDataMap.get(request.getClientName());
        if (rateLimiter == null) {
            LOG.warn("There is not traffic restriction for this client");
           rateLimitResponse.setStatus(RequestStatus.CLIENTNOTREGISTER);
           return rateLimitResponse;
        }

        if(!requestBuilder.hasAnyRuleToApply(request,rateLimiter.getLimitRuleDataSet())){
            rateLimitResponse.setStatus(RequestStatus.NORULESTOAPPLY);
            return rateLimitResponse;
        }

        requestBuilder.build(request, rateLimiter.getLimitRuleDataSet());

        if (rateLimiter.isRequestAccepted(request))
           rateLimitResponse.setStatus(RequestStatus.SUCCESS);
        else
           rateLimitResponse.setStatus(RequestStatus.TOMANYREQUEST);

        rateLimitResponse.setRequest(request);

     }catch (Exception exp){
         LOG.warn("Internal Error, Traffic Shaping might not work as expected");
        rateLimitResponse.setStatus(RequestStatus.EXCEPTION);
     }
       return rateLimitResponse;
   }

   public void registerClient(String strJson){
       clientRateLimitDataMap.putAll(requestBuilder.jsonToMap(strJson));
   }

}

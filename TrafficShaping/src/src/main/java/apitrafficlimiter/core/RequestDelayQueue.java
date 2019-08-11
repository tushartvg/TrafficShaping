package src.main.java.apitrafficlimiter.core;

import src.main.java.apitrafficlimiter.model.RateLimitRequest;

import java.util.concurrent.DelayQueue;

public class RequestDelayQueue {
    private DelayQueue<RateLimitRequest> requests = new DelayQueue<>();
    private Object lock = new Object();

  public RateLimitRequest pollAndOfferWithNextTimeUnit(){
        synchronized (lock){
            RateLimitRequest request = requests.poll();
            RateLimitRequest cloneRequest = (request != null) ? request.getCloneRequestWithNextTimeUnit() : null;

            if(cloneRequest!= null) {
                offerRequest(cloneRequest);
            }
          return  request;
        }
    }

    public int size(){
      return this.requests.size();
    }

    public void offerRequest(RateLimitRequest request){
            request.addDelay();
            requests.offer(request);
     }
}

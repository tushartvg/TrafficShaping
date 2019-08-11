package src.main.java.apitrafficlimiter.model;

import src.main.java.apitrafficlimiter.enums.CustomTimeUnit;
import src.main.java.apitrafficlimiter.enums.RequestStatus;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class RateLimitResponse {
    RequestStatus status;
    ConcurrentHashMap<String,ConcurrentHashMap<CustomTimeUnit,AtomicInteger>> availableLimit = new ConcurrentHashMap<>();
    RateLimitRequest request;

    public RequestStatus getStatus() {
        return status;
    }

    public void setStatus(RequestStatus status) {
        this.status = status;
    }

    public ConcurrentHashMap<String, ConcurrentHashMap<CustomTimeUnit, AtomicInteger>> getAvailableLimit() {
        return availableLimit;
    }

    public void setAvailableLimit(ConcurrentHashMap<String, ConcurrentHashMap<CustomTimeUnit, AtomicInteger>> availableLimit) {
        this.availableLimit = availableLimit;
    }

    public RateLimitRequest getRequest() {
        return request;
    }

    public void setRequest(RateLimitRequest request) {
        this.request = request;
    }

    @Override
    public String toString() {
        return "RateLimitResponse{" +
                "status=" + status +
                ", availableLimit=" + availableLimit +
                ", request=" + request.toResponseString() +
                '}';
    }
}

package src.main.java.apitrafficlimiter.model;


import src.main.java.apitrafficlimiter.enums.CustomTimeUnit;

import java.util.Comparator;
import java.util.Objects;
import java.util.TreeSet;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

public class RateLimitRequest implements Delayed,Cloneable{
   private long id;
   private String clientName;
   private String apiName;
   private String methodName;
   private Long startTime;
   private volatile Long executionTime;
   private TreeSet<CustomTimeUnit> customTimeUnits = new TreeSet<CustomTimeUnit>(Comparator.comparing(CustomTimeUnit::getSortedOrder));
   private Boolean status;

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public TreeSet<CustomTimeUnit> getCustomTimeUnits() {
        return customTimeUnits;
    }

    public void setCustomTimeUnits(TreeSet<CustomTimeUnit> customTimeUnits) {
        this.customTimeUnits = customTimeUnits;
    }

    @Override
    protected RateLimitRequest clone() throws CloneNotSupportedException {
        return (RateLimitRequest) super.clone();
    }

    public RateLimitRequest(String clientName) {
        this.clientName = clientName;
    }
    public RateLimitRequest(String clientName,String apiName, String methodName){
       this.clientName = clientName;
        this.apiName = apiName;
       this.methodName = methodName;
       this.startTime = System.currentTimeMillis();
   }

    public RateLimitRequest(String apiName, String methodName, Long id){
        this.apiName = apiName;
        this.methodName = methodName;
        this.startTime = System.currentTimeMillis();
        this.id = id;
    }

   public RateLimitRequest(String apiName, String methodName, Long startTime, Long id, CustomTimeUnit timeUnit){
        this.apiName = apiName;
        this.methodName = methodName;
        this.startTime = startTime;
        this.id = id;
    }

    public void addDelay(){
       this.executionTime = this.getCustomTimeUnits().first().addMilliSeconds(this.startTime);
   }

    public String getApiName() {
        return apiName;
    }

    public void setApiName(String apiName) {
        this.apiName = apiName;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public Long getStartTime() {
        return startTime;
    }

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }


    @Override
    public long getDelay(TimeUnit timeUnit) {
        long diff = executionTime - System.currentTimeMillis();
        return timeUnit.convert(diff, TimeUnit.MILLISECONDS);
    }

    @Override
    public int compareTo(Delayed delayedObj) {
        if (this.executionTime < ((RateLimitRequest) delayedObj).executionTime) {
            return -1;
        }
        if (this.executionTime > ((RateLimitRequest) delayedObj).executionTime) {
            return 1;
        }
        return 0;

    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }



       public RateLimitRequest getCloneRequestWithNextTimeUnit() {
        RateLimitRequest cloneRequest = null;
        if (this.getCustomTimeUnits().size() > 1) {
            try {
                cloneRequest = this.clone();
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }

            TreeSet<CustomTimeUnit> tempTimeUnits = new TreeSet<CustomTimeUnit>(Comparator.comparing(CustomTimeUnit::getSortedOrder));
            tempTimeUnits.addAll(this.getCustomTimeUnits());
            tempTimeUnits.pollFirst();
            cloneRequest.setCustomTimeUnits(tempTimeUnits);
        }
        return cloneRequest;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RateLimitRequest that = (RateLimitRequest) o;
        return Objects.equals(clientName, that.clientName) &&
                Objects.equals(apiName, that.apiName) &&
                Objects.equals(methodName, that.methodName);
    }

    @Override
    public int hashCode() {

        return Objects.hash(clientName, apiName, methodName);
    }

    @Override
    public String toString() {
        return "RateLimitRequest{" +
                "id=" + id +
                ", clientName='" + clientName + '\'' +
                ", apiName='" + apiName + '\'' +
                ", methodName='" + methodName + '\'' +
                ", startTime=" + startTime +
                ", executionTime=" + executionTime +
                ", customTimeUnits=" + customTimeUnits +
                ", status=" + status +
                '}';
    }

    public String toResponseString() {
        return "RateLimitRequest{" +
                "  clientName='" + clientName + '\'' +
                ", apiName='" + apiName + '\'' +
                ", methodName='" + methodName + '\'' +
                ", startTime=" + startTime +
                ", executionTime=" + executionTime +
               '}';
    }
}

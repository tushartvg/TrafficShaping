package src.main.java.apitrafficlimiter.enums;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;


public enum CustomTimeUnit {
    SECONDS("SEC",1){
        public Long addMilliSeconds(Long timestamp) {
            return timestamp + 1000l;
        }
    },MINUTES("MIN",2){
        public Long addMilliSeconds(Long timestamp) {
            return timestamp + 60000l;
        }
    },HOURS("HOUR",3){
        public Long addMilliSeconds(Long timestamp) {
            return timestamp + 3600000L;
        }
    },DAYS("DAY",4){
        public Long addMilliSeconds(Long timestamp) {
            return timestamp + 86400000L;
        }
    },WEEKS("WEEK",5){
        public Long addMilliSeconds(Long timestamp) {
            return timestamp + 604800000L;
        }
    },MONTHS("MONTH",6){
        public Long addMilliSeconds(Long timestamp) {
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(timestamp);
            cal.add(Calendar.MONTH, 1);
            return cal.getTime().getTime();
        }
    };

    String  code;
    Integer sortedOrder;

    private static final Map<String, CustomTimeUnit> lookup = new HashMap<String, CustomTimeUnit>();

    static {
        for (CustomTimeUnit timeUnit : CustomTimeUnit.values()) {
            lookup.put(timeUnit.getCode(), timeUnit);
        }
    }

    CustomTimeUnit(String code, Integer sortedOrder) {
        this.code = code;
        this.sortedOrder = sortedOrder;
    }

    public  static CustomTimeUnit getTimeUnitByCode(String code){
        return lookup.get(code);
    }
    public Integer getSortedOrder() {
        return sortedOrder;
    }

    public String getCode() {
        return code;
    }

    public Long addMilliSeconds(Long timestamp) {
         throw new AbstractMethodError();
    }

}

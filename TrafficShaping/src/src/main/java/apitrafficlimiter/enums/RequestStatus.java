package src.main.java.apitrafficlimiter.enums;

public enum RequestStatus {
    SUCCESS("Requst Succeed",200),TOMANYREQUEST("To Many Request",429),NORULESTOAPPLY("No Rules For This Request",200),CLIENTNOTREGISTER("Client not register",200),EXCEPTION("Internal Exception",200);
    String Desc;
    Integer code;

    RequestStatus(String desc, Integer code) {
        Desc = desc;
        this.code = code;
    }
}

package com.example.gb.forcemultiplier;

public class taskQueue {
    private String customerName;
    private String req_time;
    private String longitude;
    private String latitude;
    private String issue;

    public String getCustomerName() {
        return customerName;
    }

    public String getReq_time() {
        return req_time;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getIssue() {
        return issue;
    }

    public  taskQueue(String cName,String description, String lon,String lat,String t){
        this.customerName =cName;
        this.issue = description;
        this.latitude = lat;
        this.longitude = lon;
        this.req_time = t;
    }
}

package com.example.hey.MODULE;

public class Status {
    private String imgUrl;
    private long timestamp;

    //empty constructor

    public Status() {
    }

    //constructors

    public Status(String imgUrl, long timestamp) {
        this.imgUrl = imgUrl;
        this.timestamp = timestamp;
    }

    //getters and setters

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}

package com.ahmed.homeservices.models;

import com.google.firebase.database.Exclude;

public class Notification {

    private String body;
    private boolean shown;
    private String title;
    private String notificationId;
    @Exclude
    private String orderId = "";


    public Notification() {

    }

    public boolean isShown() {
        return shown;
    }

    public void setShown(boolean shown) {
        this.shown = shown;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getNotificationId() {
        return notificationId;
    }

    @Exclude
    public void setNotificationId(String notificationId) {
        this.notificationId = notificationId;
    }

    @Exclude
    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }
}

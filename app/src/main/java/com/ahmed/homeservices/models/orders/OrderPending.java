package com.ahmed.homeservices.models.orders;

import java.util.UUID;

public class OrderPending {


    private String categoryId;
    private String orderId = UUID.randomUUID().toString();
    private String userId = "";


    public OrderPending() {

    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}


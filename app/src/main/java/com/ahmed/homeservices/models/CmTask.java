package com.ahmed.homeservices.models;

import java.io.Serializable;

public class CmTask implements Serializable{

//    private Category categoryId;
    private String categoryId;
//    private double cost;
    private String cost;

    private String customerComment = ""; //Optional
//    private User customerId;
    private String customerId;
    private String from;
//    private double rate = 4.5f;
    private String rate = "0";
//    private OrderRequest requestId;
    private String requestId;
    private String to;
    private String type;
    private String cmId;

/*
* we learn
* 1-sugar (app)
* 2-
* 3-
* 4-
* 5-
* 6-
* lets make an app using this fucking 6 points
* */

    public String getCmId() {
        return cmId;
    }

    public void setCmId(String cmId) {
        this.cmId = cmId;
    }

    public CmTask() {
    }
//
//    public String getCmId() {
//        return cmId;
//    }
//
//    public void setCmId(String cmId) {
//        this.cmId = cmId;
//    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }


    public String getCustomerComment() {
        return customerComment;
    }

    public void setCustomerComment(String customerComment) {
        this.customerComment = customerComment;
    }

    public String getRate() {
        return rate;
    }

    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

//    public double getRate() {
//        return rate;
//    }
//
//    public void setRate(double rate) {
//        this.rate = rate;
//    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

}
package com.ahmed.homeservices.models.orders;

import com.ahmed.homeservices.constants.Constants;
import com.ahmed.homeservices.models.Location;
import com.google.firebase.database.Exclude;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.UUID;

//import javax.inject.Inject;

public class OrderRequest implements Serializable {


    private ArrayList<String> orderPhotos = new ArrayList<>();
    //to prevent firebase from save this array into it
    @Exclude
    private ArrayList<String> orderPhotosUris = new ArrayList<>();
    private String creationDate;
    private String creationTime;
    //    ArrayList<Category> listCategory = new ArrayList<Category>();
    private String categoryId;
    private String rate = "5";
    private String customerComment = "";
    //String isDateApplicableOrNot = Constants.STATUS_NOT_SURE;
    private String orderId = UUID.randomUUID().toString();
    private String customerId;
    private String locationId;
    private Location location;
    private String orderDescription;
    private String locationAddress;
    private String state = Constants.PENDING;
//    private String state = Constants.ORDER_STATE_CANCELLED;


    //    private double cost = 00.00f;
    private String cost;


    //    @Inject
    public OrderRequest() {

    }


//    @Exclude
//    public ArrayList<Uri> getOrderPhotosUris() {
//        return orderPhotosUris;
//    }
//
//    @Exclude
//    public void setOrderPhotosUris(ArrayList<Uri> orderPhotosUris) {
//        this.orderPhotosUris = orderPhotosUris;
//    }


    public ArrayList<String> getOrderPhotosUris() {
        return orderPhotosUris;
    }

    public void setOrderPhotosUris(ArrayList<String> orderPhotosUris) {
        this.orderPhotosUris = orderPhotosUris;
    }

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }

    public String getCustomerComment() {
        return customerComment;
    }

    public void setCustomerComment(String customerComment) {
        this.customerComment = customerComment;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getLocationId() {
        return locationId;
    }

    public void setLocationId(String locationId) {
        this.locationId = locationId;
    }

    public ArrayList<String> getOrderPhotos() {
        return orderPhotos;
    }

    public void setOrderPhotos(ArrayList<String> orderPhotos) {
        this.orderPhotos = orderPhotos;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public String getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(String creationTime) {
        this.creationTime = creationTime;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getOrderDescription() {
        return orderDescription;
    }

    public void setOrderDescription(String orderDescription) {
        this.orderDescription = orderDescription;
    }

    public String getLocationAddress() {
        return locationAddress;
    }

    public void setLocationAddress(String locationAddress) {
        this.locationAddress = locationAddress;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }
}


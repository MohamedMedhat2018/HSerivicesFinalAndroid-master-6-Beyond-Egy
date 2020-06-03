package com.ahmed.homeservices.models;

public class PhoneRegistered {

    private String phoneNumber;
//    private boolean verified;
//    private String password;
    private String customerId;

    public PhoneRegistered() {
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}

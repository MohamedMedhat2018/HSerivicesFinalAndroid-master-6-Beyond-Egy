package com.ahmed.homeservices.models;

import com.google.firebase.auth.PhoneAuthProvider;

public class PhoneMaterials {

    private String phoneNumber;
    private String verificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;

    public PhoneMaterials() {
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getVerificationId() {
        return verificationId;
    }

    public void setVerificationId(String verificationId) {
        this.verificationId = verificationId;
    }

    public PhoneAuthProvider.ForceResendingToken getmResendToken() {
        return mResendToken;
    }

    public void setmResendToken(PhoneAuthProvider.ForceResendingToken mResendToken) {
        this.mResendToken = mResendToken;
    }
}

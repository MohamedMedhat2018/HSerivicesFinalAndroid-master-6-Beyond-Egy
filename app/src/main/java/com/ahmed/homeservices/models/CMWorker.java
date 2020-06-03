package com.ahmed.homeservices.models;

import com.ahmed.homeservices.constants.Constants;

import java.io.Serializable;

public class CMWorker implements Serializable {


    private String os = "Android";
    private String workerId;
    private String createDate;
    private String workerPhone;
    private String workerNameInArabic;
    private String workerNameInEnglish;
    private String workerPassword;
    private String workerEmail;
    private String workerLocationId;
    private String workerLocationAdress;
    private String workerCategoryid;
    private String workerType = Constants.CM;
    private Location workerLocation;
    private boolean workerStatusActivation = false;
    private boolean login;
//    private double rate = 5f;
    private String rate;
    private String messageToken;
    private String workerPhoto;
    public CMWorker() {

    }

    public String getOs() {
        return os;
    }

    public void setOs(String os) {
        this.os = os;
    }

    public String getMessageToken() {
        return messageToken;
    }

    public void setMessageToken(String messageToken) {
        this.messageToken = messageToken;
    }

    public String getWorkerPhoto() {
        return workerPhoto;
    }

    public void setWorkerPhoto(String workerPhoto) {
        this.workerPhoto = workerPhoto;
    }

    public boolean isLogin() {
        return login;
    }

    public void setLogin(boolean login) {
        this.login = login;
    }

    public Location getWorkerLocation() {
        return workerLocation;
    }

    public void setWorkerLocation(Location workerLocation) {
        this.workerLocation = workerLocation;
    }

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }

    public String getWorkerLocationAdress() {
        return workerLocationAdress;
    }

    public void setWorkerLocationAdress(String workerLocationAdress) {
        this.workerLocationAdress = workerLocationAdress;
    }


    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getWorkerNameInArabic() {
        return workerNameInArabic;
    }

    public void setWorkerNameInArabic(String workerNameInArabic) {
        this.workerNameInArabic = workerNameInArabic;
    }

    public String getWorkerNameInEnglish() {
        return workerNameInEnglish;
    }

    public void setWorkerNameInEnglish(String workerNameInEnglish) {
        this.workerNameInEnglish = workerNameInEnglish;
    }

    public String getWorkerPhone() {
        return workerPhone;
    }

    public void setWorkerPhone(String workerPhone) {
        this.workerPhone = workerPhone;
    }

    public String getWorkerPassword() {
        return workerPassword;
    }

    public void setWorkerPassword(String workerPassword) {
        this.workerPassword = workerPassword;
    }

    public String getWorkerEmail() {
        return workerEmail;
    }

    public void setWorkerEmail(String workerEmail) {
        this.workerEmail = workerEmail;
    }

    public String getWorkerType() {
        return workerType;
    }

    public void setWorkerType(String workerType) {
        this.workerType = workerType;
    }

    public String getWorkerId() {
        return workerId;
    }

    public void setWorkerId(String workerId) {
        this.workerId = workerId;
    }

    public String getWorkerLocationId() {
        return workerLocationId;
    }

    public void setWorkerLocationId(String workerLocationId) {
        this.workerLocationId = workerLocationId;
    }

    public String getWorkerCategoryid() {
        return workerCategoryid;
    }

    public void setWorkerCategoryid(String workerCategoryid) {
        this.workerCategoryid = workerCategoryid;
    }

    public boolean isWorkerStatusActivation() {
        return workerStatusActivation;
    }

    public void setWorkerStatusActivation(boolean workerStatusActivation) {
        this.workerStatusActivation = workerStatusActivation;
    }

}

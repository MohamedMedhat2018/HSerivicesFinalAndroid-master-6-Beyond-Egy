package com.ahmed.homeservices.models;

import com.google.firebase.database.Exclude;

import java.io.Serializable;

//model for a category
public class Category implements Serializable {

    private String categoryName;
    private String categoryNameArabic = "";
    private String categoryIcon;
    //    private String categoryId = UUID.randomUUID().toString();
    @Exclude
    private String categoryId;
    //    private String downloadUrl;
    //for saving state in the adapter (hold user click)
    @Exclude
    private boolean visible = false;

    public Category() {
    }
    //    private String downloadUrl;

    public Category(String categoryName, String downloadUrl) {
        this.categoryName = categoryName;
//        this.downloadUrl = downloadUrl;
    }

    public String getCategoryNameArabic() {
        return categoryNameArabic;
    }

    public void setCategoryNameArabic(String categoryNameArabic) {
        this.categoryNameArabic = categoryNameArabic;
    }

    public String getCategoryIcon() {
        return categoryIcon;
    }

    public void setCategoryIcon(String categoryIcon) {
        this.categoryIcon = categoryIcon;
    }


    @Exclude
    public String getCategoryId() {
        return categoryId;
    }

    @Exclude
    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }


//    public void setDownloadUrl(String downloadUrl) {
//        this.downloadUrl = downloadUrl;
//    }


    @Exclude
    public boolean isVisible() {
        return visible;
    }

    @Exclude
    public void setVisible(boolean visible) {
        this.visible = visible;
    }
}

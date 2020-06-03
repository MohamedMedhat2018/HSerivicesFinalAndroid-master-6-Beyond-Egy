package com.ahmed.homeservices.models;

public class Country {
    private String countryName;
    private String countryNameArabic;
    private String countryId;

    public Country(String countryName, String countryId) {
        this.countryName = countryName;
        this.countryId = countryId;
    }

    public Country() {
    }

    public String getCountryNameArabic() {
        return countryNameArabic;
    }

    public void setCountryNameArabic(String countryNameArabic) {
        this.countryNameArabic = countryNameArabic;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public String getCountryId() {
        return countryId;
    }

    public void setCountryId(String countryId) {
        this.countryId = countryId;
    }
}
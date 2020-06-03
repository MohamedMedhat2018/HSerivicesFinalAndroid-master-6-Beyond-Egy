package com.ahmed.homeservices.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class LocationCompany implements Serializable {


    private Country country = null;
    private List<City> listOfCities = null;
    private String CountryId = "";
    private List<String> cityId = new ArrayList<>();

    public Country getCountry() {
        return country;
    }

    public String getCountryId() {
        return CountryId;
    }

    public void setCountryId(String countryId) {
        CountryId = countryId;
    }

    public List<String> getCityId() {
        return cityId;
    }

    public void setCityId(List<String> cityId) {
        this.cityId = cityId;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    public List<City> getListOfCities() {
        return listOfCities;
    }

    public void setListOfCities(List<City> listOfCities) {
        this.listOfCities = listOfCities;
    }

    public class Country {
        private String countryName = null;
        private String countryId;

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
}

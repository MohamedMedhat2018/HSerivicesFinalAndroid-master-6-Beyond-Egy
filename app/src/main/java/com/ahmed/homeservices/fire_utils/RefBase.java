package com.ahmed.homeservices.fire_utils;

import com.ahmed.homeservices.constants.Constants;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public final class RefBase {


    /**
     * Default database
     */
    static DatabaseReference root() {
        return FirebaseDatabase.getInstance().getReference();
    }


    /**
     * Get a secondary database instance by URL
     * Test Database Connection
     *
     * @return
     */
//    public static DatabaseReference root() {
//
////        FirebaseOptions options = new FirebaseOptions.Builder()
////                .setApplicationId("1:445806630230:android:0b00433d741945f42672f7")
////                .setApiKey("AIzaSyB5_36WgMjAFqhtHUFX1V-kTvNR6JsawSg")
////                .setDatabaseUrl("https://servizi-test-data.firebaseio.com")
////                .build();
////        FirebaseApp.initializeApp(AppConfig.getInstance().getApplicationContext()
////                , options, "Servizi");
//        FirebaseApp secondApp = FirebaseApp.getInstance("Servizi");
//        FirebaseDatabase secondDatabase = FirebaseDatabase.getInstance(secondApp);
//        //secondDatabase.getReference().setValue(ServerValue.TIMESTAMP);
//
//
//        // Get a secondary database instance by URL
////        return FirebaseDatabase.getInstance()
////                .getReferenceFromUrl("https://servizi-test-data.firebaseio.com/");
//        return secondDatabase.getReference();
//    }
    public static DatabaseReference refUsers() {


        return root()
                .child(Constants.USERS);
    }

    public static DatabaseReference refWorkers() {
        return root()
                .child(Constants.WORKERS);
    }

    public static DatabaseReference refWorker(String workerId) {
        return root()
                .child(Constants.WORKERS)
                .child(workerId);
    }

    public static DatabaseReference refUser(String userId) {
        return root()
                .child(Constants.USERS)
                .child(userId);

    }

    public static DatabaseReference refCategories() {
        return root()
                .child(Constants.CATEGORY);
    }

    public static DatabaseReference refCategory(String catId) {
        return root()
                .child(Constants.CATEGORY)
                .child(catId);

    }

    public static DatabaseReference refCategoryQuestions() {
        return root()
                .child(Constants.CATEGORY_QUESTIONS);
    }


    public static DatabaseReference registerPhone() {
        return root()
                .child(Constants.REG_PHONES);

    }

    public static DatabaseReference registerPhone(String key) {
        return root()
                .child(Constants
                        .REG_PHONES)
                .child(key);
    }

    public static Query regPhones(String userId) {
        return root()
                .child(Constants.REG_PHONES)
                .orderByChild(Constants.CUSTOMER_ID)
                .equalTo(userId);
    }

//    public static DatabaseReference requestPending() {
//        return root().child(Constants.RQUEST_PENDING);
//    }


    //for the pending requests
    public static DatabaseReference requestPending(String userId) {
        return root().child(Constants.RQUEST_PENDING).child(userId);
    }

    //for the pending requests
    public static DatabaseReference requestPending() {
        return root().child(Constants.RQUEST_PENDING);
    }

    //for the requests
    public static DatabaseReference refRequests(String reqId) {
        return root()
                .child(Constants.REQUESTS)
                .child(reqId);

    }

    //for the requests
    public static DatabaseReference refRequests() {
        return root()
                .child(Constants.REQUESTS);
//                .child(userId);
    }


    public static DatabaseReference refCompanies() {
        return root()
                .child(Constants.COMPANIES);
//                .child(userId);
    }

    public static DatabaseReference refCompany(String companyId) {
        return root()
                .child(Constants.COMPANY)
                .child(companyId);
    }

    public static DatabaseReference refCMRegister(String catId, String workerId) {
        return root()
                .child(Constants.WORKER)
                .child(catId)
                .child(workerId);
    }

//
//    public static DatabaseReference ref(String catId,String workerId) {
//        return root()
//                .child(Constants.WORKER)
//                .child(catId)
//                .child(workerId);
//    }

    public static DatabaseReference refCmtTasks(String workerId) {
        return root()
                .child(Constants.CM_TASKS)
                .child(workerId);

    }

    public static DatabaseReference refCmtTasks() {
        return root()
                .child(Constants.CM_TASKS);
//                .child(workerId);


    }

    public static DatabaseReference refLocations() {
        return root()
                .child(Constants.LOCATIONS);
        //.child(workerId);

    }

    public static DatabaseReference refDeleteRequests() {
        return root()
                .child(Constants.DELETED_REQUESTS);
//                .child(userId);
    }

    public static class CitySection {

        public static DatabaseReference refLocCities(String countryId) {
            return refLocations()
                    .child(Constants.CITIES)
                    .child(countryId);

        }

        public static DatabaseReference refLocGetCity(String countryId, String cityId) {
            return refLocations()
                    .child(Constants.CITIES)
                    .child(countryId)
                    .child(cityId);


        }


    }

    public static class CountrySection {

        public static DatabaseReference refLocCountries() {
            return refLocations()
                    .child(Constants.COUNTRIES);
        }

        public static DatabaseReference refLocGetCountry(String id) {
            return refLocations()
                    .child(Constants.COUNTRIES)
                    .child(id);
        }

    }


    public static DatabaseReference refComments(String orderId) {
        return root()
                .child(Constants.COMMENTS)
                .child(orderId);

    }

    public static DatabaseReference notifiFreelance(String freelancerId) {
        return root()
                .child(Constants.NOTIFI_FREELANCER)
                .child(freelancerId);

    }

    public static DatabaseReference notifiCm(String cmId) {
        return root()
                .child(Constants.NOTIFI_CM)
                .child(cmId);

    }

    public static DatabaseReference notifiCustomer(String userId) {
        return root()
                .child(Constants.NOTIFI_CUSTOMER)
                .child(userId);

    }

    public static DatabaseReference rate() {
        return root()
                .child(Constants.RATINGS);
//                .child(userId);
    }

    public static DatabaseReference rate(String rateId) {
        return root()
                .child(Constants.RATINGS)
                .child(rateId);
    }

    public static DatabaseReference refFreelancersConnection() {
        return root()
                .child(Constants.FREE_CUSTOMER_CONNECTION);
//                .child(userId);
    }

    public static DatabaseReference CPNotification() {
        return root()
                .child(Constants.CP_NOTIFICATIONS);
//                .child(userId);
    }

    public static DatabaseReference FreeCustomerConnection() {
        return root()
                .child(Constants.FREE_CUSTOMER_CONNECTION);
//                .child(userId);
    }

}
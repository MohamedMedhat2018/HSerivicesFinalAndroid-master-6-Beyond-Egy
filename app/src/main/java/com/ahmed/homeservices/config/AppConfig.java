package com.ahmed.homeservices.config;

import android.app.Application;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.Configuration;

import androidx.annotation.NonNull;

import com.ahmed.homeservices.internet_checker.ConnectivityReceiver;
import com.ahmed.homeservices.localization.LocaleHelper;
import com.crashlytics.android.Crashlytics;
import com.pixplicity.easyprefs.library.Prefs;

import java.util.Locale;

import io.fabric.sdk.android.Fabric;

//import com.orm.SugarContext;

//import com.orm.SugarContext;

public class AppConfig extends Application {


    private Locale locale = null;
    private static AppConfig mInstance;

    public static synchronized AppConfig getInstance() {
        return mInstance;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base, "en"));
    }

    @Override
    public void onCreate() {
        super.onCreate();



//        SugarContext.init(this);
        Fabric.with(this, new Crashlytics());
        // Initialize the Prefs class
        new Prefs.Builder()
                .setContext(this)
                .setMode(ContextWrapper.MODE_PRIVATE)
                .setPrefsName(getPackageName())
                .setUseDefaultSharedPreference(true)
                .build();

        mInstance = this;


//        if (true) {
//            FirebaseOptions options = new FirebaseOptions.Builder()
//                    .setApplicationId("1:445806630230:android:0b00433d741945f42672f7")
//                    .setApiKey("AIzaSyB5_36WgMjAFqhtHUFX1V-kTvNR6JsawSg")
//                    .setDatabaseUrl("https://servizi-test-data.firebaseio.com")
//                    .build();
//            FirebaseApp.initializeApp(AppConfig.getInstance().getApplicationContext()
//                    , options, "Servizi");
//        }


        //method 2
//        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
//        Configuration config = getBaseContext().getResources().getConfiguration();
//        String lang = settings.getString(getString(R.string.pref_locale), "");
//        if (!"".equals(lang) && !config.locale.getLanguage().equals(lang)) {
//            locale = new Locale(lang);
//            Locale.setDefault(locale);
//            config.locale = locale;
//            getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
//        }


    }

    @Override
    public void onTerminate() {
        super.onTerminate();
//        SugarContext.terminate();
    }

    public void setConnectivityListener(ConnectivityReceiver.ConnectivityReceiverListener listener) {
        ConnectivityReceiver.connectivityReceiverListener = listener;
    }


    //contin. method 2
    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
//        if (locale != null) {
//            newConfig.locale = locale;
//            Locale.getDefault().setDefault(locale);
//            getBaseContext().getResources().updateConfiguration(newConfig, getBaseContext().getResources().getDisplayMetrics());
//        }
    }


}
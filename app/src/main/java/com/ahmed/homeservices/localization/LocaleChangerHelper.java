package com.ahmed.homeservices.localization;

import android.content.res.Configuration;
import android.os.Build;
import android.os.LocaleList;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Locale;

public class LocaleChangerHelper {
    
    private LocaleChangerHelper() {
        throw new IllegalStateException("No instances");
    }
    
    @SuppressWarnings("JavaReflectionMemberAccess,PrivateApi")
    public static void changeLanguage() {
        try {
            Class amnClass = Class.forName("android.app.ActivityManagerNative");
            
            // amn = ActivityManagerNative.getDefault();
            Method methodGetDefault = amnClass.getMethod("getDefault");
            methodGetDefault.setAccessible(true);
            Object amn = methodGetDefault.invoke(amnClass);
            
            // config = amn.getConfiguration();
            Method methodGetConfiguration = amnClass.getMethod("getConfiguration");
            methodGetConfiguration.setAccessible(true);
            Configuration config = (Configuration) methodGetConfiguration.invoke(amn);
            
            // config.userSetLocale = true;
            Class configClass = config.getClass();
            Field f = configClass.getField("userSetLocale");
            f.setBoolean(config, true);
            
            // set the locale to the new value
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                config.setLocales(new LocaleList(/* TODO: Your locales */));
            } else {
                config.locale = new Locale("ar") /* TODO: Your locale */;
            }
            
            // amn.updateConfiguration(config);
            Method methodUpdateConfiguration = amnClass.getMethod("updateConfiguration",
                    Configuration.class);
            methodUpdateConfiguration.setAccessible(true);
            methodUpdateConfiguration.invoke(amn, config);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
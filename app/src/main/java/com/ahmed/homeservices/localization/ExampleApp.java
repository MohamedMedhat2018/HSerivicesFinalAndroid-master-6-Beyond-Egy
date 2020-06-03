package com.ahmed.homeservices.localization;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import javinator9889.localemanager.application.BaseApplication;
import javinator9889.localemanager.localemanager.LocaleManager;

public class ExampleApp extends BaseApplication {
    /**
     * You must override this method for using a custom shared preferences with
     * your locale instance. Notice that, at this point, the application
     * <b>is being created</b>, so you must use the provided {@code base} for
     * managing shared preferences and any other resources that depends on
     * {@code Context}.
     * <p>
     * You can safely return {@code null} if you are not using any custom shared
     * preferences file, so {@link LocaleManager} will use the default one.
     *
     * @param base the base context obtained when just creating the
     *             application.
     * @return {@code SharedPreferences} or {@code null} if using the default
     * ones.
     */
    @Nullable
    @Override
    protected SharedPreferences getCustomSharedPreferences(@NonNull Context base) {
        // we will not use custom shared preferences, so null is safely
        // returnable-
        return null;
    }
}
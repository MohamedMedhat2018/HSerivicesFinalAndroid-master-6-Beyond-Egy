package com.ahmed.homeservices.activites.lang;

import android.animation.Animator;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.ahmed.homeservices.R;
import com.ahmed.homeservices.activites.cutomer_or_worker.CustomerOrWorkerActivity;
import com.ahmed.homeservices.activites.cutomer_or_worker.login.CustomerWorkerLoginActivity;
import com.ahmed.homeservices.activites.details.customer.CustomerPostDetails;
import com.ahmed.homeservices.activites.details.freelancer.FreelancerPostDetails;
import com.ahmed.homeservices.config.AppConfig;
import com.ahmed.homeservices.constants.Constants;
import com.ahmed.homeservices.localization.LocaleHelper;
import com.ahmed.homeservices.utils.Utils;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.pixplicity.easyprefs.library.Prefs;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LanguageActivity extends AppCompatActivity {

    private static final String TAG = "LanguageActivity";
    //    private static final String TITLE = "Sample Article";
    @BindView(R.id.ivLogoStart)
    ImageView ivLogoStart;
//    MixpanelAPI mixpanelAPI;
//    JSONObject jsonObject = new JSONObject();
//    private String articleId;

    @Override
    protected void onStop() {
        super.onStop();
//        Objects.requireNonNull(getIntent().getExtras()).clear();
//        if (getIntent().getExtras() != null){
//        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Prefs.remove(Constants.CLEAR);
    }

    @Override
    protected void onResume() {
        super.onResume();
//        Log.e(TAG, "onResume: ");
//        if (getIntent().getExtras() != null) {
//            Log.e(TAG, "onResume: 5 " + getIntent().getExtras()
//                    .getString(Constants.NOTIFI_DETAILS_TYPE, "a7a"));
//        }
        try {
            if (getIntent().getExtras() != null && getIntent().hasExtra(Constants.NOTIFI_DETAILS_TYPE)
//                    && !Prefs.contains(Constants.CLEAR)
            ) {

//                Log.e(TAG, "onResume: 1 " + getIntent().getExtras()
//                        .getString(Constants.NOTIFI_DETAILS_TYPE, ""));
//                Log.e(TAG, "onResume: 2 " + getIntent().getExtras()
//                        .getString(Constants.TYPE, ""));

                Intent intent = null;
                //Log.e(TAG, "getExtras: " );
//                Log.e(TAG, "onResume+++ffff " +
//                        getIntent().getExtras().getString(Constants.NOTIFI_DETAILS_TYPE));


                switch (getIntent().getExtras().getString(Constants.NOTIFI_DETAILS_TYPE)) {
                    case Constants.FREELANCER_POST_DETAILS:
                        //        intent = new Intent(getApplicationContext(), CustomerPostDetails.class);
                        intent = new Intent(AppConfig.getInstance(), FreelancerPostDetails.class);
                        break;
                    case Constants.CUSTOMER_POST_DETAILS:
//                    Log.e(TAG, "sendNotification: " + detailsType);
//                            intent = new Intent(getApplicationContext(), CustomerPostDetails.class);
                        intent = new Intent(AppConfig.getInstance(), CustomerPostDetails.class);
                        break;
                }


                if (intent != null) {
                    intent.putExtra(Constants.TYPE,
                            getIntent().getExtras().getString(Constants.TYPE));
                    intent.putExtra(Constants.ORDER_ID_NOTIFI_BAR,
                            getIntent().getExtras().getString(Constants.ORDER_ID_NOTIFI_BAR));
//            intent.putExtra(Constants.NOTIFI_DETAILS_TYPE, detailsType);
                    startActivity(intent);
                    //getIntent().getExtras().clear();
                    Prefs.remove(Constants.CLEAR);
                }

            } else {
                Log.e(TAG, "onResume:  null ");
            }
        } catch (Exception e) {
            Log.e(TAG, e.getLocalizedMessage());
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase));
//        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));

    }

    @OnClick(R.id.btnSelectArabic)
    public void btnSelectArabic(View v) {
        changeToAr();

//        ExampleApp.localeManager.setNewLocale(this, LanguagesSupport.Language.ARABIC);
//        recreate();
//        LocaleChangerHelper.changeLanguage();
//        startActivity(new Intent(this, CustomerOrWorkerActivity.class));
//        finish();

    }

    private void changeToAr() {
        //        setLocale("ar");

        //        if (TextUtils.equals(Utils.getCurrentDeviceLang(), Constants.LOCALE_ENGLISH)) {
        Prefs.edit().putString(Constants.SELECTED_LANG, Constants.LANG_ARABIC).apply();
        LocaleHelper.setLocale(this, TextUtils.equals(Prefs.getString(Constants.SELECTED_LANG, Constants.LANG_ARABIC), Constants.LANG_ARABIC) ?
                "ar" : "en");
//        }

//        Log.e(TAG, "btnSelectEnglish: Arabic");

//        startActivity(new Intent(this, CustomerWorkerLoginActivity.class));
//        recreate();

        startActivity(new Intent(this, CustomerOrWorkerActivity.class));
        overridePendingTransition(R.anim.enter, R.anim.exit);
        finish();

    }

    private void setLocale(String language) {
        LocaleHelper.setLocale(this, language);
//        startActivity(new Intent(this, CustomerOrWorkerActivity.class));
        startActivity(new Intent(this, CustomerWorkerLoginActivity.class));
//        finish();
    }

    @OnClick(R.id.btnSelectEnglish)
    public void btnSelectEnglish(View v) {


//        Snekers.getInstance().error("bla bla", this);
        changeToEn();


//        ExampleApp.localeManager.setNewLocale(this, LanguagesSupport.Language.ENGLISH);
//        recreate();
    }

    //    private void testingPushNotificationIcon() {
//        MyFirebaseMessagingService service = new MyFirebaseMessagingService();
//        //test payload
//        HashMap<String, String> map = new HashMap<>();
//        map.put(Constants.TYPE, "hohohoho");
//        service.sendNotification(map);
//
//        //test body/title
////        service.sendNotification("my body", "my title");
//
//    }
    private void changeToEn() {
        //        setLocale("en");

//        if (TextUtils.equals(Utils.getCurrentDeviceLang(), Constants.LOCALE_ENGLISH)) {
        Prefs.edit().putString(Constants.SELECTED_LANG, Constants.LANG_ENGLISH).apply();
        LocaleHelper.setLocale(this, TextUtils.equals(Prefs.getString(Constants.SELECTED_LANG, Constants.LANG_ARABIC),
                Constants.LANG_ARABIC) ?
                "ar" : "en");
//        }


//        Log.e(TAG, "btnSelectEnglish: English");


//        startActivity(new Intent(this, CustomerWorkerLoginActivity.class));
//        recreate();


        startActivity(new Intent(this, CustomerOrWorkerActivity.class));
        overridePendingTransition(R.anim.enter, R.anim.exit);
        finish();
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

//        final FirebaseAppIndex firebaseAppIndex = FirebaseAppIndex.getInstance();
//        startService(new Intent(this, AppIndexingService.class));
//        AppIndexingUtil.clearStickers(this, firebaseAppIndex);
//        onNewIntent(getIntent());

//        testingPushNotificationIcon();

        //        if (Prefs.contains(Constants.SELECTED_LANG)) {
//            finish();
//            startActivity(new Intent(this, CustomerOrWorkerActivity.class));
//        }


//        YoYo.with(Techniques.BounceIn)
        YoYo.with(Techniques.FadeIn)
                .delay(1000)
                .duration(1500)
                .onEnd(new YoYo.AnimatorCallback() {
                    @Override
                    public void call(Animator animator) {
                        ivLogoStart.setVisibility(View.VISIBLE);
//                        YoYo.with(Techniques.FadeOut)
//                                .delay(1000)
//                                .onEnd(new YoYo.AnimatorCallback() {
//                                    @Override
//                                    public void call(Animator animator) {
//                                        ivLogoStart.setVisibility(View.GONE);
//                                        YoYo.with(Techniques.FadeIn)
//                                                .delay(1000)
//                                                .onEnd(new YoYo.AnimatorCallback() {
//                                                    @Override
//                                                    public void call(Animator animator) {
//                                                        ivLogoStart.setVisibility(View.VISIBLE);
//                                                    }
//                                                }).playOn(ivLogoStart);
//                                    }
//                                }).playOn(ivLogoStart);
                    }
                }).playOn(ivLogoStart);


        // ATTENTION: This was auto-generated to handle app links.
//        Intent appLinkIntent = getIntent();
//        String appLinkAction = appLinkIntent.getAction();
//        Uri appLinkData = appLinkIntent.getData();
//        if (appLinkData != null) {
//            Log.e(TAG, "appLinkData " + appLinkData.toString());
//        } else {
//            Log.e(TAG, "appLinkData  null");
//        }
//        if (appLinkAction != null) {
//            Log.e(TAG, "appLinkAction " + appLinkAction);
//        } else {
//            Log.e(TAG, "appLinkAction  null");
//        }
//
//        getDynamicLinks();

//        getFirebaseInstanceId();


    }

    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        setIntent(intent);

//        String action = intent.getAction();
//        Uri data = intent.getData();
//        if (Intent.ACTION_VIEW.equals(action) && data != null) {
//            articleId = data.getLastPathSegment();
////            TextView linkText = findViewById(R.id.link);
////            linkText.setText(data.toString());
//        }
    }

    @Override
    protected void onStart() {
        super.onStart();


        //for testing the secondary database
        //RefBase.root();


        EventBus.getDefault().removeAllStickyEvents();
//        if (articleId == null) return;
//        final Uri BASE_URL = Uri.parse("https://seyanah-uae.com/");
//        final String APP_URI = BASE_URL.buildUpon().appendPath(articleId).build().toString();
//
//        Indexable articleToIndex = new Indexable.Builder()
//                .setName(TITLE)
//                .setUrl(APP_URI)
//                .build();
//
//        Task<Void> task = FirebaseAppIndex.getInstance().update(articleToIndex);
//
//        // If the Task is already complete, a call to the listener will be immediately
//        // scheduled
//        task.addOnSuccessListener(this, new OnSuccessListener<Void>() {
//            @Override
//            public void onSuccess(Void aVoid) {
//                Log.d(TAG, "App Indexing API: Successfully added " + TITLE + " to index");
//            }
//        });
//
//        task.addOnFailureListener(this, new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception exception) {
//                Log.e(TAG, "App Indexing API: Failed to add " + TITLE + " to index. " + exception
//                        .getBody());
//            }
//        });
//
//        // log the view action
//        Task<Void> actionTask = FirebaseUserActions.getInstance().start(Actions.newView(TITLE,
//                APP_URI));
//
//        actionTask.addOnSuccessListener(this, new OnSuccessListener<Void>() {
//            @Override
//            public void onSuccess(Void aVoid) {
//                Log.d(TAG, "App Indexing API: Successfully started view action on " + TITLE);
//            }
//        });
//
//        actionTask.addOnFailureListener(this, new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception exception) {
//                Log.e(TAG, "App Indexing API: Failed to start view action on " + TITLE + ". "
//                        + exception.getBody());
//            }
//        });

        remember();

    }

    private void remember() {
        if (TextUtils.equals(Constants.COUNTRY_CODE, "+20")) {
            new AlertDialog.Builder(this)
                    .setCancelable(false)
                    .setMessage("ماتنساش ال Constants اديني بقولك اهه ؟؟؟؟")
                    .setPositiveButton("حاضر مش هنسي", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .show();
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().removeAllStickyEvents();
//        mixpanelAPI();
        createNotificationChannel();
//        Utils.buildCalligraphyFont();
        Utils.fullScreen(this);
        setContentView(R.layout.activity_language);
        ButterKnife.bind(this);

        checkForDynamicLinks();
    }

    private void mixpanelAPI() {
        //        mixpanelAPI = MixpanelAPI.getInstance(this, getString(R.string.MixpanelAPI));
//        try {
//            jsonObject.put("Prop name", "Prop value");
//            jsonObject.put("Prop 2", "Value 2");
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        mixpanelAPI.track("Event name", jsonObject);
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create channel to show notifications.
            String channelId = getString(R.string.default_notification_channel_id);
            String channelName = "Channel human readable title";
            NotificationManager notificationManager =
                    getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(new NotificationChannel(channelId,
                    channelName, NotificationManager.IMPORTANCE_LOW));
        }
    }

    private void getFirebaseInstanceId() {
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "getInstanceId failed", task.getException());
                            return;
                        }

                        // Get new Instance ID token
                        String token = task.getResult().getToken();
                        Log.e(TAG, "Refreshed_token: " + token);
                        ///the token does not changes if the app don't uninstalled
                        //ctH_0lOFV0Q:APA91bHU-uAvcQ8w71M3MbruxAuqg1a0s-U6fz60uk8vIq3-aU15NVjVwRn_JPpmEhMkYlXVnrl1tSc_eYv_7ur2yIzUHxFc2FR02OYwAIf1JsDxOw1R80A6lorhl3V_eEptEAfX5GN0
                        //but if the app in uninstalled
                        //the token is willl be
                        // Log and toast
//                        String msg = getString(R.string.msg_token_fmt, token);
//                        Log.d(TAG, msg);
//                        Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void getDynamicLinks() {

        FirebaseDynamicLinks.getInstance()
                .getDynamicLink(getIntent())
                .addOnSuccessListener(this, new OnSuccessListener<PendingDynamicLinkData>() {
                    @Override
                    public void onSuccess(PendingDynamicLinkData pendingDynamicLinkData) {
                        // Get deep link from result (may be null if no link is found)
                        Uri deepLink = null;
                        if (pendingDynamicLinkData != null) {
                            deepLink = pendingDynamicLinkData.getLink();

                            Log.e(TAG, "onSuccess: " + deepLink.toString());
                        } else {
                            Log.e(TAG, "onSuccess: null");

                        }


                        // Handle the deep link. For example, open the linked
                        // content, or apply promotional credit to the user's
                        // account.


                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "getDynamicLink:onFailure", e);
                    }
                });
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();

//        Log.e(TAG, "onPostResume: " + Utils.getCurrentDeviceLang());

    }

    private void checkForDynamicLinks() {


        //https://barquae.000webhostapp.com/v1/1234
        //http://barquae.000webhostapp.com/v1/1234
        //app://barquae.000webhostapp.com/v1/1234
        //app://barquae.000webhostapp.com/v1/1234
        //app://barquae.000webhostapp.com/v1/id/user/1234  also will get last param=1234
        //https://example.com/applinks/?whichTaskToOpen=1
        //https://example.com/getUserData/?first_name=Ahmed\&last_name=Hegazy\&age=24\&phone=01156749640\&country=EG\&photo="https://firebasestorage.googleapis.com/v0/b/servizi-528e9.appspot.com/o/348322e2-b0ad-45c1-b253-44f0a7298558?alt=media&token=18b52ead-0906-42e4-8bc3-10167f03b7a9
        //https://example.com/applinks/?first_name=Ahmed&last_name=Hegazy&age=24&phone=01156749640&country=EG
        //https://example.com/appLinks/?app_version=15

        FirebaseDynamicLinks.getInstance().getDynamicLink(getIntent())
                .addOnSuccessListener(this, new OnSuccessListener<PendingDynamicLinkData>() {
                    @Override
                    public void onSuccess(PendingDynamicLinkData pendingDynamicLinkData) {
                        // Get deep link from result (may be null if no link is found)
                        Log.e(TAG, "onSuccess: ");
                        Uri deepLink = null;
                        if (pendingDynamicLinkData != null) {
                            deepLink = pendingDynamicLinkData.getLink();
                            if (deepLink != null) {
                                Log.e(TAG, "onSuccess: " + deepLink.toString());
                                String app_version = deepLink.getQueryParameter("app_version");
                                int appVer = Integer.parseInt(app_version);
                                Toast.makeText(getApplicationContext(), "From dynamic links the app version  = " + appVer,
                                        Toast.LENGTH_SHORT).show();
//                                fetchUserProfileData(deepLink);

//                                try {
//                                    PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
//                                    String version = pInfo.versionName;
//                                    //And you can get the version code by using this
//                                    int verCode = pInfo.versionCode;
//
//                                } catch (PackageManager.NameNotFoundException e) {
//                                    e.printStackTrace();
//                                }


                            }
                        }

                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "getDynamicLink:onFailure", e);
                    }
                });

    }

    /**
     * Fetching the user profile data from the intent through the QueryParameters.
     *
     * @param appLinkData
     */
    private void fetchUserProfileData(Uri appLinkData) {
        String firstName = appLinkData.getQueryParameter(Constants.USER_FIRST_NAME);
        String lastName = appLinkData.getQueryParameter(Constants.USER_LAST_NAME);
        String phoneNumber = appLinkData.getQueryParameter(Constants.USER_PHONE_NUMBER);
        String country = appLinkData.getQueryParameter(Constants.USER_COUNTRY);
        String age = appLinkData.getQueryParameter(Constants.USER_AGE);
//        String photo = appLinkData.getQueryParameter("photo");

        Log.e(TAG, "onCreate: firstName   " + firstName);
        Log.e(TAG, "onCreate: lastName    " + lastName);
        Log.e(TAG, "onCreate: phoneNumber " + phoneNumber);
        Log.e(TAG, "onCreate: country     " + country);
        Log.e(TAG, "onCreate: age         " + age);
//        Log.e(TAG, "onCreate: " + photo);


    }


}

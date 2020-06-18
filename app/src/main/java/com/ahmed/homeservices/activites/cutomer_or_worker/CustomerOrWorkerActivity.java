package com.ahmed.homeservices.activites.cutomer_or_worker;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.ahmed.homeservices.R;
import com.ahmed.homeservices.activites.meowbottomnavigaion.MainActivity;
import com.ahmed.homeservices.adapters.grid.CardFragmentPagerAdapter;
import com.ahmed.homeservices.adapters.grid.CardPagerAdapter;
import com.ahmed.homeservices.constants.Constants;
import com.ahmed.homeservices.fire_utils.RefBase;
import com.ahmed.homeservices.localization.LocaleHelper;
import com.ahmed.homeservices.models.CardItem;
import com.ahmed.homeservices.models.Country;
import com.ahmed.homeservices.models.PhoneMaterials;
import com.ahmed.homeservices.models.ShadowTransformer;
import com.ahmed.homeservices.utils.Utils;
import com.airbnb.lottie.LottieAnimationView;
import com.google.gson.Gson;
import com.pixplicity.easyprefs.library.Prefs;

import java.io.Serializable;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CustomerOrWorkerActivity extends AppCompatActivity implements Serializable {

    private static final String TAG = "CustomOrWorkActivity";
    @BindView(R.id.viewPager)
    ViewPager mViewPager;
    @BindView(R.id.ll_lottie_swipe_holder)
    View ll_lottie_swipe_holder;
    @BindView(R.id.lottieAnimViewSwipe)
    LottieAnimationView lottieAnimViewSwipe;
    //    private int delayMillis = 1000;
    private int delayMillis = 800;
    private boolean f = false;
    private PhoneMaterials phoneMaterials = new PhoneMaterials();
    private Gson gson = new Gson();
    private Button mButton;
    private CardPagerAdapter mCardAdapter;
    private ShadowTransformer mCardShadowTransformer;
    private CardFragmentPagerAdapter mFragmentCardAdapter;
    private ShadowTransformer mFragmentCardShadowTransformer;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase));
    }

    @Override
    protected void onStart() {
        super.onStart();

//        addLocations();
//         check if user exists or not
        //for testing only
//        Prefs.edit().remove(Constants.FIREBASE_UID).apply();
//        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
        if (Prefs.contains(Constants.FIREBASE_UID)) {
//        if (Prefs.getPreferences().contains(Constants.FIREBASE_UID)) {
            Log.e(TAG, "onStart: " + "to");
            finish();
            if (Utils.customerOrWorker()) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            } else {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        } else {
            Log.e(TAG, "onStart: null user");
//            startActivity(new Intent(getApplicationContext(), MainActivity.class));
        }
    }

    private void addLocations() {
        for (int i = 0; i < 5; i++) {
            String key = RefBase.refLocations()
                    .push().getKey();
            RefBase.refLocations()
                    .child("Countries")
                    .child(key)
//                    .setValue(new Country("Egypt" + i, UUID.randomUUID().toString()));
                    .setValue(new Country("Egypt" + i, key));
            for (int j = 0; j < 7; j++) {
                RefBase.refLocations()
                        .child("Cities")
                        .child(key)
                        .push()
                        .setValue("City " + j);
            }
        }
    }

    @OnClick(R.id.btnChangeLan)
    public void btnChangeLan(View view) {
        if (f) {

        } else {

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.updateResources(this,
                TextUtils.equals(Prefs.getString(Constants.SELECTED_LANG, Constants.LANG_ARABIC),
                        Constants.LANG_ARABIC) ?
                        "ar" : "en");
        Utils.fullScreen(this);
        setContentView(R.layout.activity_worker_or_customer);
        ButterKnife.bind(this);

    }

    private void setCardPagerAdapter() {
        mCardAdapter = new CardPagerAdapter(CustomerOrWorkerActivity.this);
        mCardAdapter.addCardItem(new CardItem(R.string.title_1, R.string.text_1));
        //freelancer
/*
        mCardAdapter.addCardItem(new CardItem(R.string.title_2, R.string.title_2));
*/
        mCardAdapter.addCardItem(new CardItem(R.string.title_3, R.string.title_3));

        mFragmentCardAdapter = new CardFragmentPagerAdapter(getSupportFragmentManager(),
                Utils.dpToPixels(2, this));

        mCardShadowTransformer = new ShadowTransformer(mViewPager, mCardAdapter);
        mFragmentCardShadowTransformer = new ShadowTransformer(mViewPager, mFragmentCardAdapter);

        mViewPager.setAdapter(mCardAdapter);
        mViewPager.setPageTransformer(false, mCardShadowTransformer);

        mViewPager.setOffscreenPageLimit(2);
        //freelancer
/*
        mViewPager.setOffscreenPageLimit(3);
*/
        mViewPager.setPageMargin(1);

        mCardShadowTransformer.enableScaling(true);
        mFragmentCardShadowTransformer.enableScaling(true);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        //addCatWithQuestions();
        setCardPagerAdapter();

        if (!Prefs.contains(Constants.IS_CARD_SWIPED_BEFORE)) {
            animateCardPagerUsingHandler();
        }
//        handlingSwipeLeftToolTipsPrecedure();

    }

    private void handlingSwipeLeftToolTipsPrecedure() {

        lottieAnimViewSwipe.playAnimation();
        lottieAnimViewSwipe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    private void animateCardPagerUsingHandler() {
        //for confirming the user that there is an options
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mViewPager.setCurrentItem(1, true);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mViewPager.setCurrentItem(2, true);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mViewPager.setCurrentItem(1, true);
                                Prefs.edit().putString(Constants.IS_CARD_SWIPED_BEFORE, "").apply();
                            }
                        }, delayMillis);
                    }
                }, delayMillis);
            }
        }, delayMillis);
    }

}





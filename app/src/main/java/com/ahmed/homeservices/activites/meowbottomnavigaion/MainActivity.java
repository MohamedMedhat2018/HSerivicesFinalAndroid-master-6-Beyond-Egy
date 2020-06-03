package com.ahmed.homeservices.activites.meowbottomnavigaion;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.ahmed.homeservices.R;
import com.ahmed.homeservices.activites.cutomer_or_worker.CustomerOrWorkerActivity;
import com.ahmed.homeservices.activites.cutomer_or_worker.login.CustomerWorkerLoginActivity;
import com.ahmed.homeservices.activites.lang.LanguageActivity;
import com.ahmed.homeservices.common.Common;
import com.ahmed.homeservices.constants.Constants;
import com.ahmed.homeservices.fire_utils.RefBase;
import com.ahmed.homeservices.fragments.FragmentMore;
import com.ahmed.homeservices.fragments.FragmentNotifications;
import com.ahmed.homeservices.fragments.FragmentOrderAndPosts;
import com.ahmed.homeservices.fragments.FragmentProfile;
import com.ahmed.homeservices.fragments.FragmentRequestOrder;
import com.ahmed.homeservices.interfaces.OnLoggedOut;
import com.ahmed.homeservices.interfaces.OnRegPhoneDeleteByCp;
import com.ahmed.homeservices.localization.LocaleHelper;
import com.ahmed.homeservices.messages.MsgEventReloadFragment;
import com.ahmed.homeservices.messages.MsgFromFreeToFrgOrderAndPosts;
import com.ahmed.homeservices.models.Company;
import com.ahmed.homeservices.models.MsgEvtEditOrder;
import com.ahmed.homeservices.utils.Utils;
import com.etebarian.meowbottomnavigation.MeowBottomNavigation;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.kaushikthedeveloper.doublebackpress.DoubleBackPress;
import com.kaushikthedeveloper.doublebackpress.helper.DoubleBackPressAction;
import com.kaushikthedeveloper.doublebackpress.helper.FirstBackPressAction;
import com.pixplicity.easyprefs.library.Prefs;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import es.dmoral.toasty.Toasty;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;

import static com.ahmed.homeservices.constants.Constants.ID_ACCOUNT;
import static com.ahmed.homeservices.constants.Constants.ID_MORE;
import static com.ahmed.homeservices.constants.Constants.ID_MY_ORDERS;
import static com.ahmed.homeservices.constants.Constants.ID_NOTIFICATION;
import static com.ahmed.homeservices.constants.Constants.ID_REQUEST_ORDER;

public class MainActivity extends AppCompatActivity implements OnRegPhoneDeleteByCp {
    private static final String TAG = "MainActivity";
    @BindView(R.id.bottomNavigation)
    MeowBottomNavigation bottomNavigation;
    int notiticationCustomer = 0, notificationCm = 0, notificationFreelance = 0, notificationCompany = 0;
    DoubleBackPress doubleBackPress;
    private List<Fragment> fragments = new ArrayList<>();
    private android.app.AlertDialog spotsDialog;

    private Company company = new Company();

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase));
    }

    @Override
    public void onPostCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onPostCreate(savedInstanceState, persistentState);
    }

    @Override
    public Uri onProvideReferrer() {
        return super.onProvideReferrer();
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
//                checkIfWorkerIsDeactivated();
                checkIfUserIsLoggedInFromAndotherDevice();
//                checkIfUserIsDeactivated();
            }
        }, 2000);

    }

    private void checkIfUserIsDeactivated() {
        if (Utils.customerOrWorker() && !isFinishing()) {
            Common.checkIfCustomerActivatedOrNot(Prefs.getString(Constants.FIREBASE_UID, ""), RefBase.refUsers(),
                    activationStatus -> {
                        Log.e(TAG, "checkIfWorkerIsDeactivated: ");
                        if (!activationStatus) {

                            new AlertDialog.Builder(this)
                                    .setMessage(getString(R.string.you_are_deactivated))
                                    .setTitle(getString(R.string.app_name_root))
                                    .setCancelable(false)
                                    .setPositiveButton(getString(R.string.ok), (dialog, which) -> {
                                                dialog.dismiss();
                                                Prefs.remove(Constants.FIREBASE_UID);
                                                Intent i = new Intent(getApplicationContext(), LanguageActivity.class);
                                                startActivity(i);
                                                finish();
                                            }
                                    )
                                    .show();

                        } else {
                            Log.e(TAG, "checkIfWorkerIsDeactivated: ");
                        }
                    });

        } else {
            //for company Activation state
//            if (Utils.companyOrNot()) {
            if (false) {
                Common.checkIfCustomerActivatedOrNot(Prefs.getString(Constants.FIREBASE_UID, ""), RefBase.refCompanies(),
                        activationStatus -> {
                            Log.e(TAG, "checkIfWorkerIsDeactivated: ");
                            if (!activationStatus) {

                                new AlertDialog.Builder(this)
                                        .setMessage(getString(R.string.you_are_deactivated))
                                        .setTitle(getString(R.string.app_name_root))
                                        .setCancelable(false)
                                        .setPositiveButton(getString(R.string.ok), (dialog, which) -> {
                                                    dialog.dismiss();
                                                    Prefs.remove(Constants.FIREBASE_UID);
                                                    Intent i = new Intent(getApplicationContext(), LanguageActivity.class);
                                                    startActivity(i);
                                                    finish();
                                                }
                                        )
                                        .show();

                            } else {
                                Log.e(TAG, "checkIWorkerIsDeactivated: ");
                            }
                        });
            } else {
                //For Freelancer
            }
        }

    }

    private void checkIfUserIsLoggedInFromAndotherDevice() {
        if (Utils.customerOrWorker() && !isFinishing()) {
            Log.e(TAG, "checkIfUserIs ");
            //user
            Log.e(TAG, "checkIfUserIs: " + Prefs.getString(Constants.FIREBASE_UID, ""));
            Common.checkIfUserIsLoggedInFromAndotherDevice(Prefs.getString(Constants.FIREBASE_UID, ""), (isLoggedFromAnotherDevice, dataSnapshot) -> {
                Log.e(TAG, "checkIfUserIs: ");
                if (!isLoggedFromAnotherDevice && dataSnapshot.hasChild(Constants.MESSAGE_TOKEN)
                        && TextUtils.equals(Prefs.getString(Constants.MESSAGE_TOKEN, ""), dataSnapshot.child(Constants.MESSAGE_TOKEN).getValue(String.class))
                ) {
//                    if (!isFinishing()) {
//                        HashMap<String, Object> hashMap = (HashMap<String, Object>) dataSnapshot.getValue();
//                        if (dataSnapshot.hasChild(Constants.MESSAGE_TOKEN) && hashMap != null && hashMap.get(Constants.LOGIN) != null) {
//                           hashMap.get(Constants.LOGIN);
//                           hashMap.get(Constants.LOGIN);
//                           hashMap.put(Constants.LOGIN, false);
//                           dataSnapshot.getRef().setValue(hashMap);
//                            Log.e(TAG, "1111 " );
//                            boolean b = (boolean) hashMap.get(Constants.LOGIN);
//                            Log.e(TAG, "5555: " + b );
//                            if (!b) {
                    Log.e(TAG, "2222 ");
                    Common.logout(new OnLoggedOut() {
                        @Override
                        public void onLoggedOutSuccess() {
//                                        spotsDialog.dismiss();
                            Log.e(TAG, "3333 ");
                            finish();
                            startActivity(new Intent(getApplicationContext(), CustomerOrWorkerActivity.class));
                            Toasty.warning(getApplicationContext(), R.string.Please_log_in_again, Toasty.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onLoggedOutFailed() {
                        }
                    });
//                            }
                }

//                        new AlertDialog.Builder(MainActivity.this)
//                                .setMessage(getString(R.string.login_from_another_device))
//                                .setTitle(getString(R.string.app_name_root))
//                                .setCancelable(false)
//                                .setPositiveButton(getString(R.string.log_out), (dialog, which) -> {
//                                            dialog.dismiss();
////                                        Prefs.remove(Constants.FIREBASE_UID);
////                                        Intent i = new Intent(getApplicationContext(), LanguageActivity.class);
////                                        startActivity(i);
////                                        finish();
//
//                                            spotsDialog = Utils.getInstance().pleaseWait(this);
//                                            spotsDialog.setMessage(getString(R.string.logging_out));
//                                            spotsDialog.setCanceledOnTouchOutside(false);
//                                            spotsDialog.setCancelable(false);
//                                            spotsDialog.show();
//
//
//                                        }
//                                )
//                                .show();
//                    }


//                } else {
//                    Log.e(TAG, "checkIfWorkerIsDeactivated: ");
//                }
            });
        }

    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        Log.e(TAG, "onPostResume: ");

        doubleBackPress = new DoubleBackPress()
                .withDoublePressDuration(1000)
                .withDoubleBackPressAction(new DoubleBackPressAction() {
                    @Override
                    public void actionCall() {
                        finish();
                        startActivity(new Intent(getApplicationContext(), LanguageActivity.class));
                    }
                })
                .withFirstBackPressAction(new FirstBackPressAction() {
                    @Override
                    public void actionCall() {
                        Toast.makeText(getApplicationContext(), "Press back again to Exit", Toast.LENGTH_SHORT).show();
                    }
                });


    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        doubleBackPress.onBackPressed();
    }

    FirebaseAnalytics firebaseAnalytics;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        MobileAds.initialize(this, new OnInitializationCompleteListener() {
//            @Override
//            public void onInitializationComplete(InitializationStatus initializationStatus) {
//                Log.e(TAG, "onInitializationComplete: ");
//            }
//        });
        //for testing
//        Prefs.edit().putString(Constants.WORKER_OR_CUSTOMER, Constants.CUSTOMERS).apply();
//        Utils.fullScreen(this);
        setContentView(R.layout.activity_main2);
        ButterKnife.bind(this);

        firebaseAnalytics = FirebaseAnalytics.getInstance(this);

        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "my_item_id");

        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);


        addFragments();
        initMeowBottomNavigation();
        openDefault();
//        checkUser();
        Log.e(TAG, "onCreate: Test ");
        EventBus.getDefault().removeAllStickyEvents();
        fetchTheNumberOfNotification();


        // ATTENTION: This was auto-generated to handle app links.
        Intent appLinkIntent = getIntent();
//        String appLinkAction = appLinkIntent.getAction();
//        Uri appLinkData = appLinkIntent.getData();


//        if (Prefs.getPreferences().contains(Constants.FIREBASE_UID)) {
//            String registeredUserId = Prefs.getPreferences().getString(Constants.FIREBASE_UID, "");
//            Log.e(TAG, "registeredUserId: " + registeredUserId);
//            Common.checkIfRegPhoneDeleteByCp(registeredUserId, this);
//        } else {
//            Log.e(TAG, "onCreate: user null");
//        }

        chekcCompanyActiveOrNot();

    }

    private void chekcCompanyActiveOrNot() {
        String CompanyId = Prefs.getString(Constants.FIREBASE_UID, "");
        RefBase.refCompany(CompanyId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                dataSnapshot.getRef().removeEventListener(this);
                if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {
                    company = dataSnapshot.getValue(Company.class);
                    if (company != null && !(company.getCompanyStatusActivation())) {
                        Toast.makeText(getApplicationContext(), R.string.register_not_activited, Toast.LENGTH_LONG)
                                .show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
//        addFragments();
//        initMeowBottomNavigation();

    }

    private void fetchTheNumberOfNotification() {

        if (Prefs.contains(Constants.FIREBASE_UID)) {
            Log.e(TAG, "normal user: ");
            String id = Prefs.getString(Constants.FIREBASE_UID, "");
//            llLogin.setVisibility(View.GONE);
//            rvNotifications.setVisibility(View.VISIBLE);
            if (Utils.customerOrWorker()) {
                Log.e(TAG, "checkUserType: " + id);
                RefBase.notifiCustomer(id)
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                                dataSnapshot.getRef().removeEventListener(this);
                                if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {
//                                    notifications.clear();
//                                    spotsDialog.dismiss();
                                    notiticationCustomer = 0;
                                    Log.e(TAG, "onDataChange: " + dataSnapshot.getChildrenCount());
                                    for (DataSnapshot snap : dataSnapshot.getChildren()) {
                                        HashMap<String, Object> hashMap = (HashMap<String, Object>) snap.getValue();
//                                        Notification notification = snap.getValue(Notification.class);
//                                        Log.e(TAG, "onDataChange: " + notification.getBody());
//                                        Log.e(TAG, "onDataChange: " + hashMap.toString() + " " + hashMap.get("message").toString() +
//                                        hashMap.get("title").toString() + hashMap.get("shown") );
//                                        String message = Objects.requireNonNull(notification.getBody());
//                                        String title = Objects.requireNonNull(notification.getTitle());
//                                        Boolean isShown = Objects.requireNonNull(notification.isShown());
//                                        Log.e(TAG, "onDataChange: " + title + " " + message + isShown);
//                                        Notification notification = new Notification();
//                                        notification.setBody(message);
//                                        notification.setNotificationId(snap.getKey());
//                                        notification.setTitle(title);
//                                        notification.setShown(isShown);
//                                        notifications.add(notification);
                                        if (hashMap != null) {

                                            if (hashMap.get("shown") != null) {
                                                boolean shown = (boolean) hashMap.get("shown");
                                                if (!shown) {
                                                    notiticationCustomer++;
                                                }
                                            }
                                        }
                                    }
                                    if (notiticationCustomer != 0) {
                                        bottomNavigation.setCount(ID_NOTIFICATION, notiticationCustomer <= 9 ? notiticationCustomer + "" : "9" + "+");
                                    } else {
                                        bottomNavigation.setCount(ID_NOTIFICATION, "");
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
            } else {

                if (Utils.companyOrNot()) {
                    //not Finished
                    Log.e(TAG, "Company: ");
//                        showLoadDialog();
                    RefBase.notifiFreelance(id).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                                        dataSnapshot.getRef().removeEventListener(this);
                            if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {
//                                            notifications.clear();
                                notificationCompany = 0;
//                                            spotsDialog.dismiss();
                                for (DataSnapshot snap :
                                        dataSnapshot.getChildren()) {
                                    HashMap<String, Object> hashMap = (HashMap<String, Object>) snap.getValue();
//                                        Log.e(TAG, "onDataChange: " + hashMap.toString());
//                                                String message = null, title = null, isShown = null;
//                                                if(hashMap.get("message") != null){
//                                                    message = hashMap.get("message").toString();
//                                                }
//                                                if(hashMap.get("title") != null){
//                                                    title = hashMap.get("title").toString();
//                                                }
                                    if (hashMap.get("shown") != null) {
                                        boolean isShown = (boolean) hashMap.get("shown");
                                        if (!isShown) {
                                            notificationCompany++;
                                        }
                                    }
////                                        Log.e(TAG, "onDataChange: " + title + " " + message);
//                                                Notification notification = new Notification();
//                                                notification.setBody(message);
////                                                notification.setNotificationId(snap.getKey());
//                                                notification.setTitle(title);
//                                                notification.setShown(TextUtils.equals(isShown, "true"));
//                                        notifications.add(notification);
                                }
                                if (notificationCompany != 0) {
//                                                bottomNavigation.setCount(ID_NOTIFICATION, notificationFreelance + "+");
                                    bottomNavigation.setCount(ID_NOTIFICATION, notificationCompany <= 9 ?
                                            notificationCompany + "" : "9" + "+");
                                } else {
                                    bottomNavigation.setCount(ID_NOTIFICATION, "");
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                } else {
                    switch (Prefs.getString(Constants.WORKER_LOGGED_AS, "")) {
                        case Constants.FREELANCER:
                            Log.e(TAG, "FREELANCER: ");
//                        showLoadDialog();
                            RefBase.notifiFreelance(id).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                                        dataSnapshot.getRef().removeEventListener(this);
                                    if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {
//                                            notifications.clear();
                                        notificationFreelance = 0;
//                                            spotsDialog.dismiss();
                                        for (DataSnapshot snap :
                                                dataSnapshot.getChildren()) {
                                            HashMap<String, Object> hashMap = (HashMap<String, Object>) snap.getValue();
//                                        Log.e(TAG, "onDataChange: " + hashMap.toString());
//                                                String message = null, title = null, isShown = null;
//                                                if(hashMap.get("message") != null){
//                                                    message = hashMap.get("message").toString();
//                                                }
//                                                if(hashMap.get("title") != null){
//                                                    title = hashMap.get("title").toString();
//                                                }
                                            if (hashMap.get("shown") != null) {
                                                boolean isShown = (boolean) hashMap.get("shown");
                                                if (!isShown) {
                                                    notificationFreelance++;
                                                }
                                            }
////                                        Log.e(TAG, "onDataChange: " + title + " " + message);
//                                                Notification notification = new Notification();
//                                                notification.setBody(message);
////                                                notification.setNotificationId(snap.getKey());
//                                                notification.setTitle(title);
//                                                notification.setShown(TextUtils.equals(isShown, "true"));
//                                        notifications.add(notification);
                                        }
                                        if (notificationFreelance != 0) {
//                                                bottomNavigation.setCount(ID_NOTIFICATION, notificationFreelance + "+");
                                            bottomNavigation.setCount(ID_NOTIFICATION, notificationFreelance <= 9 ?
                                                    notificationFreelance + "" : "9" + "+");
                                        } else {
                                            bottomNavigation.setCount(ID_NOTIFICATION, "");
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError
                                                                databaseError) {

                                }
                            });
                            break;

                    }

                }


            }
        }


    }

    private void checkUser() {
        if (!Prefs.contains(Constants.FIREBASE_UID)) {
            startActivity(new Intent(getApplicationContext(), CustomerWorkerLoginActivity.class));
//            if (bottomNavigation != null) {
//                bottomNavigation.show(0, true);
//                goToFragment(fragments.get(1), false);
//            }
        }
    }

    private void initMeowBottomNavigation() {

        //for clearing the bottom navigation before fill it again
//        bottomNavigation.setModels(new ArrayList<>());
        bottomNavigation.getModels().clear();
        bottomNavigation.getCells().clear();

        if (Utils.customerOrWorker()) {
            //        bottomNavigation.add(new MeowBottomNavigation.Model(ID_REQUEST_ORDER, R.drawable.ic_home));
//        bottomNavigation.add(new MeowBottomNavigation.Model(ID_REQUEST_ORDER, R.drawable.home));
            bottomNavigation.add(new MeowBottomNavigation.Model(ID_REQUEST_ORDER, R.drawable.home3));
//        bottomNavigation.add(new MeowBottomNavigation.Model(ID_MY_ORDERS, R.drawable.ic_explore));
//        bottomNavigation.add(new MeowBottomNavigation.Model(ID_MY_ORDERS, R.drawable.ic_explore));
            bottomNavigation.add(new MeowBottomNavigation.Model(ID_MY_ORDERS, R.drawable.explore));
//        bottomNavigation.add(new MeowBottomNavigation.Model(ID_MESSAGE, R.drawable.ic_message));
//        bottomNavigation.add(new MeowBottomNavigation.Model(ID_NOTIFICATION, R.drawable.ic_notification));
            bottomNavigation.add(new MeowBottomNavigation.Model(ID_NOTIFICATION, R.drawable.notification));
//        bottomNavigation.add(new MeowBottomNavigation.Model(ID_ACCOUNT, R.drawable.ic_account));
//        bottomNavigation.add(new MeowBottomNavigation.Model(ID_ACCOUNT, R.drawable.ic_account2));
            bottomNavigation.add(new MeowBottomNavigation.Model(ID_ACCOUNT, R.drawable.ic_account3));
            bottomNavigation.add(new MeowBottomNavigation.Model(ID_MORE, R.drawable.more));
//            bottomNavigation.setCount(ID_NOTIFICATION, "3+");
        } else {
            //        bottomNavigation.add(new MeowBottomNavigation.Model(ID_REQUEST_ORDER, R.drawable.ic_home));
//        bottomNavigation.add(new MeowBottomNavigation.Model(ID_REQUEST_ORDER, R.drawable.home));
//            bottomNavigation.add(new MeowBottomNavigation.Model(ID_REQUEST_ORDER, R.drawable.home3));
//        bottomNavigation.add(new MeowBottomNavigation.Model(ID_MY_ORDERS, R.drawable.ic_explore));
//        bottomNavigation.add(new MeowBottomNavigation.Model(ID_MY_ORDERS, R.drawable.ic_explore));
            bottomNavigation.add(new MeowBottomNavigation.Model(ID_MY_ORDERS, R.drawable.explore));
//        bottomNavigation.add(new MeowBottomNavigation.Model(ID_MESSAGE, R.drawable.ic_message));
//        bottomNavigation.add(new MeowBottomNavigation.Model(ID_NOTIFICATION, R.drawable.ic_notification));
            bottomNavigation.add(new MeowBottomNavigation.Model(ID_NOTIFICATION, R.drawable.notification));
//            bottomNavigation.setCount(ID_NOTIFICATION, "3+");


//        bottomNavigation.add(new MeowBottomNavigation.Model(ID_ACCOUNT, R.drawable.ic_account));
//        bottomNavigation.add(new MeowBottomNavigation.Model(ID_ACCOUNT, R.drawable.ic_account2));
            bottomNavigation.add(new MeowBottomNavigation.Model(ID_ACCOUNT, R.drawable.ic_account3));
            bottomNavigation.add(new MeowBottomNavigation.Model(ID_MORE, R.drawable.more));

        }

        bottomNavigation.setOnShowListener(new Function1<MeowBottomNavigation.Model, Unit>() {
            @Override
            public Unit invoke(MeowBottomNavigation.Model model) {
//                switch (model.getId()) {
//                    case ID_REQUEST_ORDER:
//
//                        break;
//                    case ID_MY_ORDERS:
//
//                        break;
//                    case ID_NOTIFICATION:
//
//                        break;
//                    case ID_MORE:
//
//                        break;
//
//                }
//                try {
//                    if (fragments != null)
//                        goToFragment(fragments.get(model.getId()), false);
//                } catch (Exception e) {
//
//                }

//                Log.e(TAG, "invoke: ");
                return null;
            }

        });

        bottomNavigation.setOnClickMenuListener(new Function1<MeowBottomNavigation.Model, Unit>() {
            @Override
            public Unit invoke(MeowBottomNavigation.Model model) {

//                switch (model.getId()) {
//                    case ID_MY_ORDERS:
//                        break;
//                }
                try {
                    if (fragments != null)
                        //get the id of current clickable icon (button)
//                        if (model.getId() == ID_MY_ORDERS) {
//                            startActivity(new Intent(getApplicationContext(), CustomerWorkerLoginActivity.class));
//                        } else
                        Log.e(TAG, "invoke: " + model.getId());

                    if (Utils.customerOrWorker()) {
                        goToFragment(fragments.get(model.getId()), false);
                    } else {
                        //for company and freelancer
                        goToFragment(fragments.get(model.getId() - 1), false);
                    }
                } catch (Exception e) {

                }
                return null;
            }
        });


    }

    private void addFragments() {
        fragments.clear();
        if (Utils.customerOrWorker()) {
            fragments.add(new FragmentRequestOrder());
        }
        //for company and freelancer
        fragments.add(new FragmentOrderAndPosts());
        fragments.add(new FragmentNotifications());
        fragments.add(new FragmentMore());
        fragments.add(new FragmentProfile());
    }

    private void openDefault() {
        //the first fragment by default -> Request OrderRequest
        goToFragment(fragments.get(0), false);
//        bottomNavigation.getCells().get(0).setSelected(true);
//        bottomNavigation.getCells().get(0).setClickable(true);
//        bottomNavigation.getCells().get(0).setEnabledCell(true);
        bottomNavigation.show(0, true);
    }

    private void goToFragment(Fragment fragment, boolean addToBackStack) {
        if (fragment == null)
            return;
        //use to control the fragment (remove it or attach it)
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (addToBackStack) {
            transaction.addToBackStack(fragment.getTag());
        }

        //following trans and anim making issues
        //please don't anble it until the problem solved in newest version
//        transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left);
//        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);


//        transaction.remove(fragment);
//        transaction.add(fragment, "");
        transaction.replace(R.id.fragmentContainer, fragment);
        transaction.commit();

//        new Handler().post(new Runnable() {
//            @Override
//            public void run() {
//                transaction.replace(R.id.fragmentContainer, fragment);
//                transaction.commit();
//            }
//        });

    }

    @Override
    public void onStop() {
        super.onStop();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }

        if (getIntent().getExtras() != null) {
            Log.e(TAG, "getExtras: ");
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMsgReloadFragment(MsgEventReloadFragment evt) {
        if (evt != null && evt.isReload()) {
//            reloadFragment();
            Prefs.edit().remove(Constants.ORDER).apply();
            goToFragment(new FragmentRequestOrder(), false);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMsgReloadFragment(MsgFromFreeToFrgOrderAndPosts evt) {
        if (evt != null) {
//            reloadFragment();
            Fragment fragment = new FragmentOrderAndPosts();
            Bundle b = new Bundle();
            b.putInt(Constants.POS_TO_TAB, 1);
            fragment.setArguments(b);
            goToFragment(fragment, false);
            bottomNavigation.show(3, true);

        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }

    private void reloadFragment() {
        final FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.detach(fragments.get(0));
        ft.attach(fragments.get(0));
        ft.commit();
    }

    private void reloadFragment2() {
        final FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.detach(fragments.get(0));
        ft.attach(fragments.get(0));
        ft.commit();
    }

    private void checkIfWorkerIsDeactivated() {
        if (!Utils.customerOrWorker() && !isFinishing()) {
            //worker
            switch (Prefs.getString(Constants.WORKER_LOGGED_AS, "")) {
                case Constants.FREELANCER:
                    Common.checkIfWorkerActivatedOrNot(Prefs.getString(Constants.FIREBASE_UID, ""),
                            activationStatus -> {
                                Log.e(TAG, "checkIfWorkerIsDeactivated: ");
                                if (!activationStatus) {
                                    new AlertDialog.Builder(this)
                                            .setMessage(getString(R.string.you_are_deactivated))
                                            .setTitle(getString(R.string.app_name_root))
                                            .setCancelable(false)
                                            .setPositiveButton(getString(R.string.ok), (dialog, which) -> {
                                                        dialog.dismiss();
                                                        Prefs.remove(Constants.FIREBASE_UID);
                                                        Intent i = new Intent(getApplicationContext(), LanguageActivity.class);
                                                        startActivity(i);
                                                        finish();
                                                    }
                                            )
                                            .show();

                                } else {
                                    Log.e(TAG, "checkIfWorkerIsDeactivated: ");
                                }
                            });
                    break;
            }

        }
    }

    @Override
    public void onRegPhoneDeleteByCp(boolean deleted) {
        Log.e(TAG, "onRegPhoneDeleteByCp: ");


        if (deleted) {
            Log.e(TAG, "onRegPhoneDeleteByCp: deleted");
        } else {

            Log.e(TAG, "onRegPhoneDeleteByCp: not deleted");

        }

        if (true) {
            return;
        }

        if (deleted) {
            Log.e(TAG, "onRegPhoneDeleteByCp: deleted");
            Toast.makeText(getApplicationContext(), "Deleted from cpanel", Toast.LENGTH_SHORT)
                    .show();


            spotsDialog = Utils.getInstance().pleaseWait(this);
            spotsDialog.setMessage(getString(R.string.logging_out));
            spotsDialog.setCanceledOnTouchOutside(false);
            spotsDialog.setCancelable(false);
            spotsDialog.show();

            Common.logout(new OnLoggedOut() {
                @Override
                public void onLoggedOutSuccess() {
                    spotsDialog.dismiss();
                    finish();
                    startActivity(new Intent(getApplicationContext(), CustomerOrWorkerActivity.class));
                }

                @Override
                public void onLoggedOutFailed() {

                }
            });

        }
    }

    //if use postSticky, priority:if u have more Subscribe for the same event 0 1 2 3
//  @Subscribe(sticky = true, threadMode = ThreadMode.MAIN, priority = 0
    @Subscribe()
    public void onMsgEvtEditOrder(MsgEvtEditOrder evt) {
        if (evt != null) {
            if (evt.isEdit()) {
                //            reloadFragment();
                //bottomNavigation.show(0, true);
                bottomNavigation.setVisibility(View.GONE);
//                YoYo.with(Techniques.SlideInDown)
//                        .onEnd(new YoYo.AnimatorCallback() {
//                            @Override
//                            public void call(Animator animator) {
//                                bottomNavigation.setVisibility(View.GONE);
//                            }
//                        })
//                        .playOn(bottomNavigation);

                Fragment fragment = new FragmentRequestOrder();
                Bundle b = new Bundle();
                b.putSerializable(Constants.EDIT_ORDER_, evt.getOrderRequest());
                fragment.setArguments(b);
                goToFragment(fragment, false);

                EventBus.getDefault().removeAllStickyEvents();
                Prefs.edit().remove(Constants.ORDER).apply();
            } else {
//                bottomNavigation.setVisibility(View.VISIBLE);


            }
        }
    }

}

package com.ahmed.homeservices.common;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.ahmed.homeservices.R;
import com.ahmed.homeservices.constants.Constants;
import com.ahmed.homeservices.fire_utils.RefBase;
import com.ahmed.homeservices.interfaces.OnLoggedFromAnotherDeviec;
import com.ahmed.homeservices.interfaces.OnLoggedOut;
import com.ahmed.homeservices.interfaces.OnRegPhoneDeleteByCp;
import com.ahmed.homeservices.interfaces.OnUserActivitied;
import com.ahmed.homeservices.interfaces.OnWorkerActivated;
import com.ahmed.homeservices.internet_checker.ConnectivityReceiver;
import com.ahmed.homeservices.models.CMWorker;
import com.ahmed.homeservices.models.Company;
import com.ahmed.homeservices.models.User;
import com.ahmed.homeservices.utils.Utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.pixplicity.easyprefs.library.Prefs;

import java.io.IOException;
import java.util.HashMap;

import es.dmoral.toasty.Toasty;

public final class Common {

    private static final String TAG = "Common";
    private static Activity activity;

    public Common() {
    }

    public Common(Activity activity) {
        Common.activity = activity;
    }

    // Showing the status of network in Snackbar
    public static void showSnackError(Activity activity, boolean isConnected) {
        String message = null;
        int color;
        if (isConnected) {
//            message = "Good! Connected to Internet";
//            color = Color.WHITE;
        } else {

            message = "Sorry! Not connected to internet";
            color = Color.RED;

            Snackbar snackbar = Snackbar
                    .make(activity.findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                snackbar.getView().setBackgroundColor(activity.getColor(R.color.Orange));
            } else {
                snackbar.getView().setBackgroundColor(ContextCompat.getColor(activity, R.color.Orange));
            }

            View sbView = snackbar.getView();
//        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
            TextView textView = sbView.findViewById(R.id.snackbar_text);
//            textView.setTextSize(17, TypedValue.COMPLEX_UNIT_SP);
            textView.setTextSize(17);

            textView.setTypeface(Typeface.DEFAULT);
//            textView.setTypeface(Typeface.DEFAULT_BOLD);


            textView.setTextColor(color);


            snackbar.show();


        }

    }

    /**
     * Shows a {@link Snackbar} using {@code text}.
     *
     * @param text The Snackbar text.
     */
    public static void showSnackbar(Activity activity, final String text) {
        View container = activity.findViewById(android.R.id.content);
        if (container != null) {
            Snackbar.make(container, text, Snackbar.LENGTH_LONG).show();
        }
    }

    // Method to manually check connection status
    public static boolean checkConnection(Activity activity) {
        boolean isConnected = ConnectivityReceiver.isConnected();
        Common.showSnackError(activity, isConnected);
        return isConnected;
    }

    public static void checkIfWorkerActivatedOrNot(String workerId, OnWorkerActivated onWorkerActivated) {
        RefBase.refWorkers()
                .child(workerId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {
//                            dataSnapshot.getRef().removeEventListener(this);
                            CMWorker cmWorker = dataSnapshot.getValue(CMWorker.class);
                            if (cmWorker != null) {
                                onWorkerActivated.onWorkerActivated(cmWorker.isWorkerStatusActivation());
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    public static void checkIfCustomerActivatedOrNot(String customerId, DatabaseReference ref, OnUserActivitied onUserActivitied) {

        ref.child(customerId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {
//                            dataSnapshot.getRef().removeEventListener(this);
                            if (Utils.customerOrWorker()) {
                                User user = dataSnapshot.getValue(User.class);
                                if (user != null) {
//                                if (user.isActiviationState())
                                    Log.e(TAG, "onDataChange: 1234 " + user.isActiviationState());
                                    onUserActivitied.onActivated(user.isActiviationState());
                                }
                            }
//                            else {
//                                if (Utils.companyOrNot()) {
//                                    Company company = dataSnapshot.getValue(Company.class);
//                                    if (company != null) {
////                                if (user.isActiviationState())
//                                        Log.e(TAG, "onDataChange:1234 company " + company.getCompanyStatusActivation());
//                                        onUserActivitied.onActivated(company.getCompanyStatusActivation());
//                                    }
//
//                                } else {
//                                    //freelancer
//                                }
//                            }

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }


//    public static void checkIfUserActivatedOrNotUsingPhone(String phonenNumber) {
//        return RefBase.registerPhone()
//                .orderByChild(Constants.PHONE_FROM_USER_MODEL)
//                .equalTo(phonenNumber.toString().trim())
//                .addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                        dataSnapshot.getRef().removeEventListener(this);
//                        Log.e(TAG, "onDataChange: 4444444444444");
//                        if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {
//                            childEventListener = query.addChildEventListener(new ChildEventListener() {
//                                @Override
//                                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//                                    dataSnapshot.getRef().removeEventListener(this);
//                                    Log.e(TAG, "onDataChange: 7777777777777");
//                                    if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {
//                                        Log.e(TAG, "onChildAdded: 3");
//                                        Toasty.error(getApplicationContext(),
//                                                Constants.PHONE_EXIST).show();
//                                        Utils.clearAllErrors(findViewById(android.R.id.content));
//                                        etWorkerPhone.setError(Constants.PHONE_EXIST);
//                                        Utils.scrollToView(scrollView, etWorkerPhone);
//                                        dlgProgress.dismiss();
//
//
//                                    } else {
//                                        Log.e(TAG, "onChildAdded: 4");
////                                                    Toasty.success(getApplicationContext(),
////                                                            "Phone not exist").show();
//                                    }
//
//
//                                }
//
//                                @Override
//                                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//
//                                }
//
//                                @Override
//                                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
//
//                                }
//
//                                @Override
//                                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//
//                                }
//
//                                @Override
//                                public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                                }
//                            });
//
////                    dataSnapshot.getRef().removeEventListener(this);
//                        } else {
//                            Log.e(TAG, "onDataChange: 8888888888888888");
//                        }
////                dataSnapshot.getRef().removeEventListener(valueEventListener);
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                    }
//                });
//    }


    public static void checkIfUserIsLoggedInFromAndotherDevice(String userId, OnLoggedFromAnotherDeviec onLoggedFromAnotherDeviec) {
        RefBase.refUsers()
                .child(userId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        //                            dataSnapshot.getRef().removeEventListener(this);
                        if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {

//                            User user = dataSnapshot.getValue(User.class);
                            HashMap<String, Object> map = (HashMap<String, Object>) dataSnapshot.getValue();
                            Log.e(TAG, "333424123d: " + map.toString());
                            if (map != null) {
                                if (map.get(Constants.LOGIN) != null) {
                                    try {
                                        boolean loggedBInAnotherDevice = (boolean) map.get(Constants.LOGIN);
                                        onLoggedFromAnotherDeviec.onLoggedFromAnotherDeviec(loggedBInAnotherDevice, dataSnapshot);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    public static void logout(OnLoggedOut onLoggedOut) {
        if (Utils.customerOrWorker()) {
            removeUserMessageToken(onLoggedOut);
        } else {
            if (Utils.companyOrNot()) {
                removeFreelancerOrCompanyMessageToken(onLoggedOut, RefBase.refCompany(Prefs.getString(Constants.FIREBASE_UID, "")));
            } else {
                removeFreelancerOrCompanyMessageToken(onLoggedOut, RefBase.refWorker(Prefs.getString(Constants.FIREBASE_UID, "")));
            }
        }
    }

    private static void removeUserMessageToken(OnLoggedOut onLoggedOut) {
        if (Utils.customerOrWorker()) {
            //Customer
            //Deletating the message token from firebase as FaceBook
            if (Prefs.getPreferences().contains(Constants.FIREBASE_UID)) {
                Log.e(TAG, "onComplete: ttttet 3");
                RefBase.refUser(Prefs.getString(Constants.FIREBASE_UID, ""))
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                Log.e(TAG, "onDataChange: " + dataSnapshot);
                                dataSnapshot.getRef().removeEventListener(this);
                                if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {
                                    Log.e(TAG, "onDataChange: " + dataSnapshot);
                                    User user = dataSnapshot.getValue(User.class);
                                    if (dataSnapshot.hasChild(Constants.MESSAGE_TOKEN)) {
                                        dataSnapshot.getRef().child(Constants.MESSAGE_TOKEN).removeValue(new DatabaseReference.CompletionListener() {
                                            @Override
                                            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                                                Log.e(TAG, "onComplete: ttttet " + user.isLogin());
//                                                user.setLogin(false);
//                                                dataSnapshot.getRef().setValue(user);
                                                dataSnapshot.getRef().child(Constants.LOGIN).setValue(false).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            Log.e(TAG, "onComplete: ttttet2 " + user.isLogin());
                                                            finishAndRestart(onLoggedOut);
                                                        }
                                                    }
                                                });

                                            }
                                        });
                                    } else {
//                                        dataSnapshot.getRef().child(Constants.LOGIN).setValue(false).addOnCompleteListener(new OnCompleteListener<Void>() {
//                                            @Override
//                                            public void onComplete(@NonNull Task<Void> task) {
//                                                if(task.isSuccessful()){
//                                                    Log.e(TAG, "onComplete: ttttet2 " + user.isLogin());
//                                                    finishAndRestart();
//                                                }
//                                            }
//                                        });
                                    }

                                } else {
//                                    finishAndRestart();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
            }
        }
    }

    private static void removeFreelancerOrCompanyMessageToken(OnLoggedOut onLoggedOut, DatabaseReference ref) {
        if (!Utils.customerOrWorker()) {
            //Customer
            //Deletating the message token from firebase as FaceBook
            if (Prefs.getPreferences().contains(Constants.FIREBASE_UID)) {
                ref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        dataSnapshot.getRef().removeEventListener(this);
                        if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {
//                                    User user = dataSnapshot.getValue(User.class);
                            if (dataSnapshot.hasChild(Constants.MESSAGE_TOKEN)) {
                                dataSnapshot.getRef().child(Constants.MESSAGE_TOKEN).removeValue(new DatabaseReference.CompletionListener() {
                                    @Override
                                    public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                                        finishAndRestart(onLoggedOut);
                                    }
                                });
                            } else {
                                finishAndRestart(onLoggedOut);
                            }
                        } else {

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        }
    }

    private static void finishAndRestart(OnLoggedOut onLoggedOut) {
        Prefs.remove(Constants.INSTANCE_ID);//for blocking all notification for logged out users
        FirebaseMessaging.getInstance().setAutoInitEnabled(false);
//        try {

        new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message message) {
                // This is where you do your work in the UI thread.
                // Your worker tells you in the message what to do.
                try {
                    FirebaseInstanceId.getInstance().deleteInstanceId();
                    Toasty.success(activity, "Token success", Toasty.LENGTH_SHORT).show();
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e(TAG, "5656768876: " + e.getMessage());
                }

            }
        };
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    FirebaseInstanceId.getInstance().deleteInstanceId();
//                    Toasty.success(getActivity(), "Token success", Toasty.LENGTH_SHORT).show();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                    Log.e(TAG, "5656768876: " + e.getMessage() );
//                }
//            }
//        }).start();
//            Disable Firebase Notifications: deleteInstanceId() is a blocking call. It cannot be called on the main thread.
//            FirebaseInstanceId.getInstance().deleteInstanceId();
//            Toasty.success(getActivity(), "Token success", Toasty.LENGTH_SHORT).show();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//                        Log.e(TAG, "onComplete:   instance id deleted success ");

        FirebaseAuth.getInstance().signOut();
//        Prefs.getPreferences().edit().remove(Constants.FIREBASE_UID);
        Prefs.remove(Constants.FIREBASE_UID);

        onLoggedOut.onLoggedOutSuccess();


    }

    /**
     * Check if the admin deleted the user by the CPanel
     * For loggin out the user from application
     * And enabling him to create a new account
     * as User or Freelancer
     */
    public static void checkIfRegPhoneDeleteByCp(String userId, OnRegPhoneDeleteByCp onRegPhoneDeleteByCp) {
        RefBase.regPhones(userId)
//                .addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                        if (dataSnapshot.hasChild(Constants.CUSTOMER_ID)) {
//                            Log.e(TAG, "onDataChange: ");
//                            onRegPhoneDeleteByCp.onRegPhoneDeleteByCp(false);
//                        } else {
//                            onRegPhoneDeleteByCp.onRegPhoneDeleteByCp(true);
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                    }
//                });

//                .addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                        if (dataSnapshot.hasChild(Constants.CUSTOMER_ID)) {
//                            Log.e(TAG, "onDataChange: ");
//                            onRegPhoneDeleteByCp.onRegPhoneDeleteByCp(false);
//                        } else {
//                            onRegPhoneDeleteByCp.onRegPhoneDeleteByCp(true);
//                        }
//
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                    }
//                });

                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        Log.e(TAG, "onChildAdded: ");
                        onRegPhoneDeleteByCp.onRegPhoneDeleteByCp(false);
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                        Log.e(TAG, "onChildRemoved: ");
                        onRegPhoneDeleteByCp.onRegPhoneDeleteByCp(true);
                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

    }

}
package com.ahmed.homeservices.phone_utils;

import android.app.Activity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public final class PhoneUtils {


    private static Activity activity;

    public PhoneUtils(Activity activity) {
        PhoneUtils.activity = activity;
    }

    //check phoneNumber is correct with it's country code then send code
    public static void startPhoneNumberVerification(String phoneNumber,
                                                    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks) {
//        this.phoneNumber = phoneNumber;
//        Prefs.edit().putString(Constants.PHONE_NUMBER, phoneNumber);
//        dlgProgress.show();
        //Starts the sms number verification process for the given sms number.
        // Either sends an SMS with a 6 digit code to the sms number specified or triggers the callback with a complete AuthCredential that can be used to log in the user.
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,             // Phone number to verify
                60,                  // Timeout duration
                TimeUnit.SECONDS,     // Unit of timeout
                activity,        // Activity (for callback binding)
                mCallbacks);       // OnVerificationStateChangedCallbacks

//        mVerificationInProgress = true;
//        mStatusText.setVisibility(View.INVISIBLE);
    }

    public static void startPhoneNumberVerificationCompany(Activity activity, String phoneNumber,
                                                           PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks) {
//        this.phoneNumber = phoneNumber;
//        Prefs.edit().putString(Constants.PHONE_NUMBER, phoneNumber);
//        dlgProgress.show();
        //Starts the sms number verification process for the given sms number.
        // Either sends an SMS with a 6 digit code to the sms number specified or triggers the callback with a complete AuthCredential that can be used to log in the user.
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,             // Phone number to verify
                60,                  // Timeout duration
                TimeUnit.SECONDS,     // Unit of timeout
                activity,        // Activity (for callback binding)
                mCallbacks);       // OnVerificationStateChangedCallbacks

//        mVerificationInProgress = true;
//        mStatusText.setVisibility(View.INVISIBLE);
    }

    public static void resendVerificationCode(String phoneNumber,
                                              PhoneAuthProvider.ForceResendingToken token,
                                              PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                activity,               // Activity (for callback binding)
                mCallbacks,         // OnVerificationStateChangedCallbacks
                token);             // ForceResendingToken from callbacks
    }


    public static void verifyPhoneNumberWithCode(String verificationId, String code, OnCompleteListener listener) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        signInWithPhoneAuthCredential(credential, listener);
    }

    //sign in with Auth credential of sms
    public static void signInWithPhoneAuthCredential(PhoneAuthCredential credential, OnCompleteListener listener) {
//        if (spotsDialog != null) {
//            spotsDialog.show();
//        }
        //Auto retriever (if user register before Move to MainActivityJavaJava directly)
        FirebaseAuth.getInstance().signInWithCredential(credential)
                .addOnCompleteListener(activity, listener);
    }


    private static final String TAG = "PhoneUtils";
}

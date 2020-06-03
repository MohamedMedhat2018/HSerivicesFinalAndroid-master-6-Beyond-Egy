package com.ahmed.homeservices.utils;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.ahmed.homeservices.fire_utils.RefBase;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public final class PhoneUtils {


    private static final String TAG = "PhoneUtils";

    public static void checkIfPhoneExistInSpecificRef(String refConstant, String phoneNumber, OnPhoneExist onPhoneExist) {
        Query query = RefBase.registerPhone()
                .orderByChild(refConstant)
                .equalTo(phoneNumber.trim());

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                dataSnapshot.getRef().removeEventListener(this);
                if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {
                    query.addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                            dataSnapshot.getRef().removeEventListener(this);
//                            track("onChildAdded_5555", "fkfkff");
                            if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {
                                Log.e(TAG, "onChildAdded: 3");

//                                Toasty.error(getApplicationContext(),
//                                        Constants.PHONE_EXIST).show();
//                                Utils.clearAllErrors(findViewById(android.R.id.content));
//                                etEnterPhone.setError(Constants.PHONE_EXIST);
//                                dlgProgress.dismiss();
//                                track("onChildAdded_7337", "fkfkff");
                                onPhoneExist.onPhoneAlreadExist(true);
                            } else {
                                Log.e(TAG, "onChildAdded: 4");
                                onPhoneExist.onPhoneAlreadExist(false);
//                                                    Toasty.success(getApplicationContext(),
//                                                           "Phone not exist").show();
//                                track("onChildAdded_793939", "fkfkff");
//                                startVirificationOperation();
                            }
//                            dataSnapshot.getRef().removeEventListener(childEventListener);
                        }

                        @Override
                        public void onChildChanged(@NonNull DataSnapshot dataSnapshot,
                                                   @Nullable String s) {
                        }

                        @Override
                        public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                        }

                        @Override
                        public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
//                            dlgProgress.dismiss();
//                            track("onCancelled12", databaseError.getMessage());

                        }
                    });
//                    dataSnapshot.getRef().removeEventListener(valueEventListener);
                } else {
                    Log.e(TAG, "onChildAdded data not exist: ");
//                    dlgProgress.dismiss();
//                    track("onDataChange_793939", "fkfkff");
                    onPhoneExist.onPhoneAlreadExist(false);

//                    startVirificationOperation();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
//                dlgProgress.dismiss();
//                track("onCancelled134034", databaseError.getMessage());
            }
        });
    }

    public interface OnPhoneExist {
        void onPhoneAlreadExist(boolean existOrNot);
    }

}

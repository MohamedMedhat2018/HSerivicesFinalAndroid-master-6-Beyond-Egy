package com.ahmed.homeservices.delete_account;

import android.util.Log;

import androidx.annotation.NonNull;

import com.ahmed.homeservices.constants.Constants;
import com.ahmed.homeservices.fire_utils.RefBase;
import com.ahmed.homeservices.models.orders.OrderRequest;
import com.ahmed.homeservices.utils.Utils;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public final class DeleteAccount {


    private static final String TAG = "DeleteAccount";

    public static void cloneRefToDeletedRequests(String firebaseUserId, OnDeletionAccountSuccess onDeletionAccountSuccess) {


        RefBase.regPhones(firebaseUserId)
//                                            .orderByChild(Constants.CUSTOMER_ID)
//                                            .equalTo(firebaseUserId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        dataSnapshot.getRef().removeEventListener(this);
                        if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {
                            for (DataSnapshot dataSnapshot1 :
                                    dataSnapshot.getChildren()) {
                                dataSnapshot1.getRef().removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        RefBase.refRequests()
                                                .orderByChild(Constants.CUSTOMER_ID)
                                                .equalTo(firebaseUserId)
                                                .addValueEventListener(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                        dataSnapshot.getRef().removeEventListener(this);
                                                        if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {
                                                            for (DataSnapshot dataSnapshot1 :
                                                                    dataSnapshot.getChildren()) {
                                                                RefBase.refDeleteRequests().push().setValue(dataSnapshot1.getValue(OrderRequest.class)).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                    @Override
                                                                    public void onSuccess(Void aVoid) {
                                                                        Log.e(TAG, "onSuccess: " + dataSnapshot1.toString());
                                                                        dataSnapshot1.getRef().removeValue();
                                                                    }
                                                                });
                                                            }
                                                            DatabaseReference refNoti = null;
                                                            if (Utils.customerOrWorker()) {
                                                                refNoti = RefBase.notifiCustomer(firebaseUserId);
                                                            } else {
                                                                if (Utils.companyOrNot()) {
//                                                                    refNoti = RefBase.notifiFreelance(firebaseUserId) ;

                                                                } else {
                                                                    refNoti = RefBase.notifiFreelance(firebaseUserId);
                                                                }
                                                            }
                                                            if (refNoti == null) {
                                                                onDelSuccess(onDeletionAccountSuccess);
                                                                return;
                                                            }

                                                            refNoti.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void aVoid) {
                                                                    RefBase.refRequests()
                                                                            .orderByChild(Constants.CUSTOMER_ID)
                                                                            .equalTo(firebaseUserId)
                                                                            .addValueEventListener(new ValueEventListener() {
                                                                                @Override
                                                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                                    dataSnapshot.getRef().removeEventListener(this);
                                                                                    if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {
                                                                                        for (DataSnapshot dataSnapshot1 :
                                                                                                dataSnapshot.getChildren()) {
                                                                                            RefBase.refDeleteRequests().push().setValue(dataSnapshot1.getValue(OrderRequest.class)).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                                @Override
                                                                                                public void onSuccess(Void aVoid) {
                                                                                                    Log.e(TAG, "onSuccess: " + dataSnapshot1.toString());
                                                                                                    dataSnapshot1.getRef().removeValue();
                                                                                                }
                                                                                            });
                                                                                        }
                                                                                        RefBase.notifiCustomer(firebaseUserId)
                                                                                                .removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                            @Override
                                                                                            public void onSuccess(Void aVoid) {
                                                                                                onDelSuccess(onDeletionAccountSuccess);
                                                                                            }
                                                                                        });
                                                                                    } else {
                                                                                        onDelSuccess(onDeletionAccountSuccess);
                                                                                    }
                                                                                }

                                                                                @Override
                                                                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                                                                }
                                                                            });

                                                                }
                                                            });
                                                        } else {
                                                            onDelSuccess(onDeletionAccountSuccess);
                                                        }
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                                    }
                                                });
                                    }
                                });

//                                                        RefBase.refUser(firebaseUserId)
//                                                                .removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
//                                                            @Override
//                                                            public void onSuccess(Void aVoid) {
//                                                                onDelSUccess();
//                                                            }
//                                                        });
                            }

                        } else {
                            onDelSuccess(onDeletionAccountSuccess);
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


    }

    private static void onDelSuccess(OnDeletionAccountSuccess onDeletionAccountSuccess) {
        DeletedSuccess deletedSuccess = new DeletedSuccess();
        onDeletionAccountSuccess.onDeletedSuccess(deletedSuccess);
    }

    public interface OnDeletionAccountSuccess {
        void onDeletedSuccess(DeletedSuccess deletedSuccess);

        void onDeletedFailed(Exception e);
    }

    public static class DeletedSuccess {

        boolean updated = true;

        DeletedSuccess() {

        }
    }


}
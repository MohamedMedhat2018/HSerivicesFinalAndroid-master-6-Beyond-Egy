package com.ahmed.homeservices.activites.profile;

import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.ahmed.homeservices.R;
import com.ahmed.homeservices.constants.Constants;
import com.ahmed.homeservices.fire_utils.RefBase;
import com.ahmed.homeservices.models.CMWorker;
import com.ahmed.homeservices.models.User;
import com.ahmed.homeservices.snekers.Snekers;
import com.ahmed.homeservices.utils.Utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.pixplicity.easyprefs.library.Prefs;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import es.dmoral.toasty.Toasty;

public class EditActivity extends AppCompatActivity {

    private static final String TAG = "EditActivity";
    @BindView(R.id.toolbarEditProfile)
    Toolbar toolbar;
    @BindView(R.id.dateTextInputLayout)
    TextInputLayout dateTextInputLayout;
    @BindView(R.id.etEnterBla)
    TextInputEditText etEnterBla;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        ButterKnife.bind(this);
        accessToolbar();
    }

    private void accessToolbar() {
        toolbar.setNavigationOnClickListener(view -> {
            //finish();
            onBackPressed();
        });
        toolbar.setTitle(getString(R.string.edit));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        switch (Objects.requireNonNull(getIntent().getStringExtra(Constants.EDIT_FIELD_TYPE))) {
            case Constants.EDIT_FULLNAME:
                Log.e(TAG, "EDIT_FULL NAME: ");
                dateTextInputLayout.setHint(getString(R.string.enter_ufllname));
                etEnterBla.setText(getIntent().getStringExtra(Constants.EDIT_FULLNAME));
                Log.e(TAG, "onPostCreate: " + etEnterBla.getText().toString());
                break;

            case Constants.EDIT_NAME_IN_AR:
                Log.e(TAG, "EDIT_NAME_IN_AR: ");
                dateTextInputLayout.setHint(getString(R.string.enter_arabic_name));
                etEnterBla.setText(getIntent().getStringExtra(Constants.EDIT_NAME_IN_AR));
                Log.e(TAG, "onPostCreate: " + etEnterBla.getText().toString());

                break;

            case Constants.EDIT_NAME_IN_EN:
                Log.e(TAG, "EDIT_NAME_IN_EN: ");
                dateTextInputLayout.setHint(getString(R.string.enter_english_name));
                etEnterBla.setText(getIntent().getStringExtra(Constants.EDIT_NAME_IN_EN));
                Log.e(TAG, "onPostCreate: " + etEnterBla.getText().toString());

                break;

            case Constants.EDIT_PHONE_NUMBER:
                Log.e(TAG, "EDIT_PHONE_NUMBER: ");
                dateTextInputLayout.setHint(getString(R.string.enter_phone));
                etEnterBla.setText(getIntent().getStringExtra(Constants.EDIT_PHONE_NUMBER));


                break;
            case Constants.EDIT_EMAIL:
                Log.e(TAG, "EDIT_EMAIL: ");
                dateTextInputLayout.setHint(getString(R.string.enter_email));

                etEnterBla.setText(getIntent().getStringExtra(Constants.EDIT_EMAIL));


//                etEnterBla.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                etEnterBla.setInputType(InputType.TYPE_CLASS_TEXT |
                        InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS |
                        InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);


                break;
            case Constants.EDIT_PASSWORD:
                Log.e(TAG, "EDIT_PASSWORD: ");
                dateTextInputLayout.setHint(getString(R.string.enter_pass));
                etEnterBla.setText(getIntent().getStringExtra(Constants.EDIT_PASSWORD));
                break;
            default:
                break;
        }


    }

    @OnClick(R.id.btnSaveChanges)
    public void btnSaveChanges(View v) {
        switch (Objects.requireNonNull(getIntent().getStringExtra(Constants.EDIT_FIELD_TYPE))) {
            case Constants.EDIT_FULLNAME:
                if (etEnterBla.getText().length() == 0) {
                    Log.e(TAG, "EDIT_FULL NAME: ");
//                    etEnterBla.setError(getString(R.string.enter_fullname));
                    Snekers.getInstance().error(this, getString(R.string.enter_fullname));
                    return;
                }
                break;
            case Constants.EDIT_PHONE_NUMBER:
                if (etEnterBla.getText().length() == 0) {
                    Log.e(TAG, "EDIT_PHONE_NUMBER: ");
//                    etEnterBla.setError(getString(R.string.enter_phone));
                    Snekers.getInstance().error(this, getString(R.string.enter_phone));
                    return;
                }
                break;
            case Constants.EDIT_EMAIL:
                Log.e(TAG, "EDIT_EMAIL:12 ");
                if (etEnterBla.getText().length() == 0) {
//                    etEnterBla.setError(getString(R.string.enter_email));
                    Snekers.getInstance().error(this, getString(R.string.enter_email));
                    return;
                } else if (!Utils.isEmailValid(etEnterBla.getText().toString())) {
                    //etEnterBla.setError(getString(R.string.enter_valid_email));
                    Snekers.getInstance().error(this, getString(R.string.enter_valid_email));
                    Log.e(TAG, "EDIT_EMAIL: ERROR ");
                    return;
                }
                break;

            case Constants.EDIT_PASSWORD:
                Log.e(TAG, "EDIT_PASSWORD: ");
                if (etEnterBla.getText().length() == 0) {
//                    etEnterBla.setError(getString(R.string.enter_pass));
                    Snekers.getInstance().error(this, getString(R.string.enter_pass));
                    return;
                } else if (!Utils.isValidPassword(etEnterBla.getText().toString())) {
//                    etEnterBla.setError(getString(R.string.enter_pass));
                    Snekers.getInstance().error(this, getString(R.string.enter_6_chars));
                    Log.e(TAG, "EDIT_PASS: ERROR ");
                    return;
                }
                break;
        }


        DatabaseReference ref = null;
        if (Utils.customerOrWorker()) {
            ref = RefBase.refUser(Prefs.getString(Constants.FIREBASE_UID, ""));
            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    //stop listener
                    dataSnapshot.getRef().removeEventListener(this);
                    if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {

                        user = dataSnapshot.getValue(User.class);
                        switch (Objects.requireNonNull(getIntent().getStringExtra(Constants.EDIT_FIELD_TYPE))) {
                            case Constants.EDIT_FULLNAME:
                                Log.e(TAG, "EDIT_FULLNAME: ");
                                dateTextInputLayout.setHint(getString(R.string.enter_fullname));
//                                    user.setUserName(getIntent().getStringExtra(Constants.EDIT_FULLNAME));
                                user.setUserName(etEnterBla.getText().toString());

                                break;
                            case Constants.EDIT_PHONE_NUMBER:
                                Log.e(TAG, "EDIT_PHONE_NUMBER: ");
                                dateTextInputLayout.setHint(getString(R.string.enter_phone));
//                                    user.setUserPhoneNumber(getIntent().getStringExtra(Constants.EDIT_PHONE_NUMBER));
                                user.setUserPhoneNumber(etEnterBla.getText().toString());
                                break;
                            case Constants.EDIT_EMAIL:
                                Log.e(TAG, "EDIT_EMAIL:1 ");
                                dateTextInputLayout.setHint(getString(R.string.enter_email));
//                                    user.setUserEmail(getIntent().getStringExtra(Constants.EDIT_EMAIL));
                                user.setUserEmail(etEnterBla.getText().toString());
                                break;
                            case Constants.EDIT_PASSWORD:
                                Log.e(TAG, "EDIT_PASSWORD: ");
                                dateTextInputLayout.setHint(getString(R.string.enter_pass));
//                                    user.setUserPassword(getIntent().getStringExtra(Constants.EDIT_PASSWORD));
                                user.setUserPassword(etEnterBla.getText().toString());
                                break;
                            default:
                                break;
                        }

                        //i'm in the same referance
                        dataSnapshot.getRef().setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    switch (Objects.requireNonNull(getIntent().getStringExtra(Constants.EDIT_FIELD_TYPE))) {
                                        case Constants.EDIT_FULLNAME:
                                            Toasty.success(getApplicationContext(), getString(R.string.updated_success)).show();
                                            break;
                                        case Constants.EDIT_PHONE_NUMBER:
                                            Toasty.success(getApplicationContext(), getString(R.string.updated_success)).show();
                                            break;
                                        case Constants.EDIT_EMAIL:
                                            Toasty.success(getApplicationContext(), getString(R.string.updated_success)).show();
                                            break;
                                        case Constants.EDIT_PASSWORD:
                                            Toasty.success(getApplicationContext(), getString(R.string.updated_success)).show();
                                            break;
                                    }
                                    finish();
                                }

                            }
                        });

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        } else {
            ref = RefBase.refWorker(Prefs.getString(Constants.FIREBASE_UID, ""));
            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    //stop listener
                    dataSnapshot.getRef().removeEventListener(this);

                    if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {
                        CMWorker user = dataSnapshot.getValue(CMWorker.class);
                        switch (Objects.requireNonNull(getIntent().getStringExtra(Constants.EDIT_FIELD_TYPE))) {
                            case Constants.EDIT_NAME_IN_AR:
                                Log.e(TAG, "EDIT_NAME_IN_AR: ");
                                dateTextInputLayout.setHint(getString(R.string.enter_arabic_name));
//                                    user.setUserName(getIntent().getStringExtra(Constants.EDIT_FULLNAME));
//                               user.setWorkerNameInArabic(etEnterBla.getText().toString());
                                user.setWorkerNameInArabic(etEnterBla.getText().toString());

                                break;
                            case Constants.EDIT_NAME_IN_EN:
                                Log.e(TAG, "EDIT_NAME_IN_EN: ");
                                dateTextInputLayout.setHint(getString(R.string.enter_english_name));
//                                    user.setUserName(getIntent().getStringExtra(Constants.EDIT_FULLNAME));
//                               user.setWorkerNameInArabic(etEnterBla.getText().toString());
                                user.setWorkerNameInEnglish(etEnterBla.getText().toString());
                                break;
                            case Constants.EDIT_PHONE_NUMBER:
                                Log.e(TAG, "EDIT_PHONE_NUMBER: ");
                                dateTextInputLayout.setHint(getString(R.string.enter_phone));
//                                    user.setUserPhoneNumber(getIntent().getStringExtra(Constants.EDIT_PHONE_NUMBER));
                                user.setWorkerPhone(etEnterBla.getText().toString());
                                break;
                            case Constants.EDIT_EMAIL:
                                Log.e(TAG, "EDIT_EMAIL: ");
                                dateTextInputLayout.setHint(getString(R.string.enter_email));
//                                    user.setUserEmail(getIntent().getStringExtra(Constants.EDIT_EMAIL));
                                user.setWorkerEmail(etEnterBla.getText().toString());
                                break;
                            case Constants.EDIT_PASSWORD:
                                Log.e(TAG, "EDIT_PASSWORD: ");
                                dateTextInputLayout.setHint(getString(R.string.enter_pass));
//                                    user.setUserPassword(getIntent().getStringExtra(Constants.EDIT_PASSWORD));
                                user.setWorkerPassword(etEnterBla.getText().toString());
                                break;
                            default:
                                break;
                        }

                        //i'm in the same referance
                        dataSnapshot.getRef().setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    switch (Objects.requireNonNull(getIntent().getStringExtra(Constants.EDIT_FIELD_TYPE))) {
                                        case Constants.EDIT_FULLNAME:
                                            Toasty.success(getApplicationContext(), getString(R.string.updated_success)).show();
                                            break;
                                        case Constants.EDIT_PHONE_NUMBER:
                                            Toasty.success(getApplicationContext(), getString(R.string.updated_success)).show();
                                            break;
                                        case Constants.EDIT_EMAIL:
                                            Toasty.success(getApplicationContext(), getString(R.string.updated_success)).show();
                                            break;
                                        case Constants.EDIT_PASSWORD:
                                            Toasty.success(getApplicationContext(), getString(R.string.updated_success)).show();
                                            break;
                                    }
                                    finish();
                                }

                            }
                        });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }


    }

}

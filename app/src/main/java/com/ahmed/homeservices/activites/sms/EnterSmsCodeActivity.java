package com.ahmed.homeservices.activites.sms;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.PersistableBundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.ahmed.homeservices.R;
import com.ahmed.homeservices.activites.cutomer_or_worker.login.CustomerWorkerLoginActivity;
import com.ahmed.homeservices.activites.meowbottomnavigaion.MainActivity;
import com.ahmed.homeservices.common.Common;
import com.ahmed.homeservices.config.AppConfig;
import com.ahmed.homeservices.constants.Constants;
import com.ahmed.homeservices.fire_utils.RefBase;
import com.ahmed.homeservices.interfaces.password.OnPasswordEncrypted;
import com.ahmed.homeservices.internet_checker.ConnectivityReceiver;
import com.ahmed.homeservices.messages.MsgFromLoginToFragment;
import com.ahmed.homeservices.models.CMWorker;
import com.ahmed.homeservices.models.CPNotification;
import com.ahmed.homeservices.models.Company;
import com.ahmed.homeservices.models.PhoneMaterials;
import com.ahmed.homeservices.models.PhoneRegistered;
import com.ahmed.homeservices.models.User;
import com.ahmed.homeservices.phone_utils.PhoneUtils;
import com.ahmed.homeservices.utils.FileUtils;
import com.ahmed.homeservices.utils.Utils;
import com.alimuzaffar.lib.pin.PinEntryEditText;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pixplicity.easyprefs.library.Prefs;
import com.vincent.filepicker.filter.entity.NormalFile;

import org.greenrobot.eventbus.EventBus;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import es.dmoral.toasty.Toasty;
import id.zelory.compressor.Compressor;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static com.ahmed.homeservices.constants.Constants.STATE_CODE_SENT;
import static com.ahmed.homeservices.constants.Constants.STATE_INITIALIZED;
import static com.ahmed.homeservices.constants.Constants.STATE_SIGNIN_FAILED;
import static com.ahmed.homeservices.constants.Constants.STATE_SIGNIN_SUCCESS;
import static com.ahmed.homeservices.constants.Constants.STATE_VERIFY_FAILED;
import static com.ahmed.homeservices.constants.Constants.STATE_VERIFY_SUCCESS;

public class EnterSmsCodeActivity extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener, OnPasswordEncrypted {
    private static final String TAG = "EnterSmsCode";
    static Task<InstanceIdResult> task = null;
    public PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    public PhoneAuthProvider.ForceResendingToken mResendToken;
    public String mVerificationId;
    @BindView(R.id.etSmsCode)
    PinEntryEditText etSmsCode;
    @BindView(R.id.tvResendSmsCode)
    TextView tvResendSmsCode;
    AlertDialog spotsDialog;
    FirebaseAuth firebaseAuth;
    CountDownTimer countDownTimer;
    Gson gson = new Gson();
    PhoneMaterials phoneMaterials;
    User user = new User();
    ConnectivityReceiver connectivityReceiver = new ConnectivityReceiver();
    CMWorker cmWorker = new CMWorker();
    StorageReference ref = null;
    FirebaseStorage storage;
    StorageReference storageReference;
    //    ProgressDialog progressDialog;
    Uri uri;
    Uri fileCompanyLoge, fileCompanyPdf;
    PhoneRegistered phoneRegistered = new PhoneRegistered();
    FirebaseUser firebaseUser;
    @BindView(R.id.tvActivate)
    TextView tvActivate;

    Company company = new Company();


    @Override
    protected void onStart() {
        super.onStart();
        showSoftKeyBoard();
        Log.e(TAG, "onStart$$$$$$$$$: ");
    }

    @Override
    protected void onResume() {
        super.onResume();
        AppConfig.getInstance().setConnectivityListener(this);
    }

    private void showSoftKeyBoard() {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
    }

    public byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }

    @SuppressLint("CheckResult")
    private void uploadPhoto(Uri filePath) {

//
//        if (filePath == null) {
////            filePath = Uri.parse("android.resource://" + getPackageName() + "/mipmap/account");
//            filePath = Uri.parse("android.resource://" + getPackageName() + "/drawable/user2");
//        }

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        //ref = storageReference.child("images/" + UUID.randomUUID().toString());
        ref = storageReference.child(UUID.randomUUID().toString());

        if (filePath != null) {
//            progressDialog = new ProgressDialog(getApplicationContext());
//            progressDialog.setBody("Uploading...");
//            progressDialog.setCancelable(false);
//            progressDialog.setCanceledOnTouchOutside(false);
//            progressDialog.show();
            spotsDialog.show();

            File file = null;
            try {
                file = FileUtils.from(getApplicationContext(), filePath);
            } catch (IOException e) {
                e.printStackTrace();
            }
            final InputStream[] iStream = {null};
            final byte[][] inputData = {null};
            // Compress image using RxJava in background thread
            assert file != null;
            new Compressor(getApplicationContext())
                    .compressToFileAsFlowable(file)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<File>() {
                        @Override
                        public void accept(File file) {
                            try {
                                iStream[0] = getApplicationContext().getContentResolver().openInputStream(Uri.fromFile(file));
                                assert iStream[0] != null;
                                inputData[0] = getBytes(iStream[0]);
                                if (inputData[0] != null)
                                    //ref.putFile(filePath)
//                                    ref = storageReference.child("images/" + UUID.randomUUID().toString());
                                    ref.putBytes(inputData[0])
                                            .addOnSuccessListener(taskSnapshot -> {
//                                                progressDialog.dismiss();
                                                ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                    @Override
                                                    public void onSuccess(Uri uri) {
//                                                        progressDialog.dismiss();
                                                        Log.e(TAG, "uploaded:");
                                                        String url = uri.toString();
                                                        Log.e(TAG, "onSuccess: " + url);
                                                        user.setUserPhoto(url);
                                                        saveCustomerInfo();
                                                    }
                                                }).addOnFailureListener(e -> {

                                                });
                                            })
                                            .addOnFailureListener(e -> {
                                                spotsDialog.dismiss();
                                                Toast.makeText(getApplicationContext(), "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();

                                                Toasty.error(Objects.requireNonNull(getApplicationContext()), Constants.NETWORK_ERROR).show();
                                            })
                                            .addOnProgressListener(taskSnapshot -> {
                                                double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot
                                                        .getTotalByteCount());
//                                                progressDialog.setBody("Uploaded " + (int) progress + "%");
                                            });

                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) {
                            throwable.printStackTrace();
//                            showError(throwable.getBody());
                            Toast.makeText(getApplicationContext(), throwable.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void saveCustomerInfo() {

        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
            @Override
            public void onComplete(@NonNull Task<InstanceIdResult> task) {
                if (task.isSuccessful()) {

                    EnterSmsCodeActivity.task = task;
//                    new AsyncPasswordEncrypt(EnterSmsCodeActivity.this)
//                            .execute(user.getUserPassword());
                    onPasswordEncrypted("");

//                    if (Utils.customerOrWorker()) {
//                        user.setMessageToken(task.getResult().getToken());
//
//                        phoneRegistered.setPhoneNumber(user.getUserPhoneNumber());
//                        phoneRegistered.setCustomerId(firebaseUser.getUid());
//
//                        RefBase.registerPhone().push().setValue(phoneRegistered)
//                                .addOnSuccessListener(new OnSuccessListener<Void>() {
//                                    @Override
//                                    public void onSuccess(Void aVoid) {
//
//
//
//
//                                    }
//                                });
//
//                    }
                }
            }
        });


    }

    @SuppressLint("CheckResult")
    private void uploadPhotosAndPdf(Uri filePath, boolean pdfOrImage) {

        Log.e(TAG, "uploadPhotosAndPdf: 1 " + filePath + " & " + pdfOrImage);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        //ref = storageReference.child("images/" + UUID.randomUUID().toString());
        ref = storageReference.child(UUID.randomUUID().toString());

        if (filePath != null) {
//            progressDialog = new ProgressDialog(getApplicationContext());
//            progressDialog.setBody("Uploading...");
//            progressDialog.setCancelable(false);
//            progressDialog.setCanceledOnTouchOutside(false);
//            progressDialog.show();
            spotsDialog.show();

            File file = null;
            try {
                file = FileUtils.from(getApplicationContext(), filePath);
            } catch (IOException e) {
                e.printStackTrace();
            }
            final InputStream[] iStream = {null};
            final byte[][] inputData = {null};
            // Compress image using RxJava in background thread
            if (file == null) {
                Log.e(TAG, "uploadPhotosAndPdf: null");
                return;
            } else {
                Log.e(TAG, "uploadPhotosAndPdf:i  oooooii ");
            }
            //for image
            if (pdfOrImage) {
                new Compressor(this)
                        .compressToFileAsFlowable(file)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<File>() {
                            @Override
                            public void accept(File file) {
                                try {
                                    iStream[0] = getApplicationContext().getContentResolver().openInputStream(Uri.fromFile(file));
                                    inputData[0] = getBytes(iStream[0]);
                                    if (inputData[0] != null) {
                                        ref = storageReference.child("images/" + UUID.randomUUID().toString());
                                        //ref.putFile(filePath)
//                                    ref = storageReference.child("images/" + UUID.randomUUID().toString());
                                        ref.putBytes(inputData[0])
                                                .addOnSuccessListener(taskSnapshot -> {
//                                                progressDialog.dismiss();
                                                    ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                        @Override
                                                        public void onSuccess(Uri uri) {
//                                                        progressDialog.dismiss();
                                                            Log.e(TAG, "uploaded: image");
                                                            String url = uri.toString();
                                                            Log.e(TAG, "onSuccess: jjjjjjjj1 " + url);

                                                            if (Utils.companyOrNot()) {
                                                                Log.e(TAG, "onComplete:  it's company ");
                                                                company.setCompanyPhoto(url);
                                                                FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                                                                        EnterSmsCodeActivity.task = task;
                                                                        if (task.isSuccessful()) {
                                                                            Log.e(TAG, "onComplete:  success upload for Company ");
//                                                                            new AsyncPasswordEncrypt(EnterSmsCodeActivity.this)
//                                                                                    .execute(cmWorker.getWorkerPassword());
                                                                            onPasswordEncrypted(null);
                                                                        }
                                                                    }
                                                                });
                                                            } else {
                                                                cmWorker.setWorkerPhoto(url);
                                                                FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                                                                        EnterSmsCodeActivity.task = task;
                                                                        if (task.isSuccessful()) {
                                                                            Log.e(TAG, "Not company");
                                                                            onPasswordEncrypted(null);
//                                                                        new AsyncPasswordEncrypt(EnterSmsCodeActivity.this)
//                                                                                .execute(cmWorker.getWorkerPassword());
                                                                        }
                                                                    }

                                                                });
                                                            }
                                                        }
                                                    }).addOnFailureListener(e -> {

                                                    });
                                                })
                                                .addOnFailureListener(e -> {
                                                    spotsDialog.dismiss();
                                                    Toast.makeText(getApplicationContext(), "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();

                                                    Toasty.error(Objects.requireNonNull(getApplicationContext()), Constants.NETWORK_ERROR).show();
                                                })
                                                .addOnProgressListener(taskSnapshot -> {
                                                    double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot
                                                            .getTotalByteCount());
//                                                progressDialog.setBody("Uploaded " + (int) progress + "%");
                                                });

                                    }
                                } catch (FileNotFoundException e) {
//                                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                    e.printStackTrace();
                                    Log.e(TAG, "accept: " + e.getMessage());
                                } catch (IOException e) {
                                    e.printStackTrace();
                                    Log.e(TAG, "accept: " + e.getMessage());
//                                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) {
                                throwable.printStackTrace();
//                            showError(throwable.getBody());
                                Toast.makeText(getApplicationContext(), throwable.getMessage(), Toast.LENGTH_SHORT).show();
                                Log.e(TAG, "acceptLLLL " + throwable.getMessage());
                            }
                        });
            } else {
                //for pdf file
                ref = storageReference.child("company_pdf_files/" + UUID.randomUUID().toString());
//                Log.e("TEST", "normalFile.getName()" + normalFile.getName());

//                ref = storageReference.child("company_pdf_files/" + normalFile.getName());
//                Log.e("TEST", "normalFile.getName()" + normalFile.getName());
                ref.putFile(filePath)
                        .addOnSuccessListener(taskSnapshot -> {
//                                                progressDialog.dismiss();
                            ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
//                                                        progressDialog.dismiss();
                                    Log.e(TAG, "uploaded: Pdf");
                                    String url = uri.toString();
                                    Log.e(TAG, "onSuccess: jjjjjjjj " + url);

                                    if (Utils.companyOrNot()) {
                                        Log.e(TAG, "uploaded: It's Company 2 " + fileCompanyLoge);
                                        company.setCompanyAttachment(url);
                                        uploadPhotosAndPdf(fileCompanyLoge, true);
                                    } else {
                                        cmWorker.setWorkerPhoto(url);
                                        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                                            @Override
                                            public void onComplete(@NonNull Task<InstanceIdResult> task) {
                                                EnterSmsCodeActivity.task = task;
                                                if (task.isSuccessful()) {
                                                    Log.e(TAG, "uploaded: It's Not Company 2");
                                                    onPasswordEncrypted(null);
//                                                                        new AsyncPasswordEncrypt(EnterSmsCodeActivity.this)
//                                                                                .execute(cmWorker.getWorkerPassword());
                                                }
                                            }

                                        });
                                    }
                                }
                            }).addOnFailureListener(e -> {
                            });
                        })
                        .addOnFailureListener(e -> {
                            spotsDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();

                            Toasty.error(Objects.requireNonNull(getApplicationContext()), Constants.NETWORK_ERROR).show();
                        })
                        .addOnProgressListener(taskSnapshot -> {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot
                                    .getTotalByteCount());
//                                                progressDialog.setBody("Uploaded " + (int) progress + "%");
                        });
            }
        }
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        tvResendSmsCode.setEnabled(false);
        if (countDownTimer != null)
            countDownTimer.start();
        getPassedUserUri();
        getPassedCompLogoAndPdf();
//        setMessageToken();
        checkAutoSmsRetrieval();
    }

    private NormalFile normalFile = null;

    private void getPassedCompLogoAndPdf() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            Log.e("TEST", "extra: not null ");
            if (getIntent().hasExtra(Constants.COMPANY_PHOTO_URI)) {
                fileCompanyLoge = Uri.parse(extras.getString(Constants.COMPANY_PHOTO_URI));
                Log.e("TEST", "extra: COMPANY_PHOTO_URI " + fileCompanyLoge);

            }

            if (getIntent().hasExtra(Constants.COMPANY_PDF_FILE)) {
                Log.e("TEST", "extra: COMPANY_PDF_FILE ");
                fileCompanyPdf = Uri.parse(extras.getString(Constants.COMPANY_PDF_FILE));
            }

            if (getIntent().hasExtra(Constants.COMPANY_PDF_NORMAL_FILE)) {
//                normalFile = gson.fromJson(getIntent().getStringExtra(Constants.COMPANY_PDF_NORMAL_FILE), NormalFile.class);
//                Log.e("TEST", "extra: COMPANY_PDF_NORMAL_FILE " + normalFile.getName());

/*                Log.e(TAG, "normalFile: " + normalFile.getPath());
                Log.e(TAG, "normalFile: " + normalFile.getSize());
                Log.e(TAG, "normalFile: " + normalFile.getName());
                Log.e(TAG, "normalFile: " + normalFile.getDate());
                Log.e(TAG, "normalFile: " + normalFile.getBucketName());
                Log.e(TAG, "normalFile: " + normalFile.getMimeType());*/
            }

        }
    }


    private void checkAutoSmsRetrieval() {
        if (getIntent().getExtras() != null) {
            if (getIntent().hasExtra(Constants.AUTO_SMS_RETRIEVAL)) {
                spotsDialog.setMessage(getString(R.string.please_wait));
                spotsDialog.show();
                registerUserWorker(null);
            }
        }
    }

    private void setMessageToken() {
        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (Utils.customerOrWorker()) {
                    if (user != null) {
                        user.setMessageToken(task.getResult().getToken());
                    }
                } else {
                    if (cmWorker != null) {
                        cmWorker.setMessageToken(task.getResult().getToken());
                    }
                }
            }
        });

    }

    private void getPassedUserUri() {
        Bundle extras = getIntent().getExtras();
        if (getIntent().hasExtra(Constants.USER_PHOTO_URI)) {
            uri = Uri.parse(extras.getString(Constants.USER_PHOTO_URI));
//        iv_photo.setImageURI(myUri);
        }
    }

    @OnClick(R.id.tvResendSmsCode)
    public void tvResendSmsCodeClicked(View v) {
        tvResendSmsCode.setEnabled(false);
        countDownTimer.start();

        new PhoneUtils(this).resendVerificationCode(
                phoneMaterials.getPhoneNumber()
                , phoneMaterials.getmResendToken(), mCallbacks);

    }

    @OnClick(R.id.tvActivate)
    public void btnActivateClicked(View v) {
        v.setEnabled(false);
        if (TextUtils.isEmpty(etSmsCode.getText().toString())) {
            Toast.makeText(this, "Enter Sms code", Toast.LENGTH_SHORT).show();
            v.setEnabled(true);
        } else {
            if (etSmsCode.getText().toString().length() != 6) {
                Toast.makeText(this, "Enter full Sms code", Toast.LENGTH_SHORT).show();
                v.setEnabled(true);
            } else {
                if (getIntent().hasExtra(Constants.NO_CURRENT_USER)) {
                    spotsDialog.setMessage(getString(R.string.enter) + " .....");
                } else {
                    if (getIntent().hasExtra(Constants.LOGGED_FROM_ANOTHER_DEVICE)) {
                        spotsDialog.setMessage(getString(R.string.please_wait));
                    }
                }
                spotsDialog.show();

                new PhoneUtils(this).verifyPhoneNumberWithCode(
//                        Prefs.getString(Constants.VERIFICATRION_ID,"")
                        phoneMaterials.getVerificationId()
                        , etSmsCode.getText().toString().trim(), new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    registerUserWorker(task);
                                } else {
                                    v.setEnabled(true);
                                    // Sign in failed, display a message and update the UI
                                    Log.e(TAG, "signInWithCredential:failure", task.getException());
                                    if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                        // The verification code entered was invalid
//                                mVerificationField.setError("Invalid code.");
                                        //send error to model then check it in Fragment to display error in fragment
//                                EventBus.getDefault().post(new MsgEvevntErrorSms(true));
                                        Log.e(TAG, "onComplete: 5555555");
                                        Toast.makeText(getApplicationContext(),
                                                getString(R.string.invalid_sms_code), Toast.LENGTH_SHORT).show();
                                    }
                                    spotsDialog.dismiss();
                                    updateUI(STATE_SIGNIN_FAILED);
                                }
                            }

                        });
            }
        }
    }


    private void registerUserWorker(@NonNull Task<AuthResult> task) {
        // Sign in success, update UI with the signed-in user's information
        Log.e(TAG, "signInWithCredential:success");
        FirebaseUser user;
//                                    Prefs.edit().putString(Constants.FIREBASE_UID, user.getUid()).apply();

        if (task == null) {
            user = FirebaseAuth.getInstance().getCurrentUser();
        } else {
            user = task.getResult().getUser();
        }

        if (getIntent() != null) {
            if (getIntent().hasExtra(Constants.FORGOT_PASS)) {
                spotsDialog.dismiss();
                Log.e(TAG, "999999999999999: ");
                Intent intent = new Intent(getApplicationContext(), CustomerWorkerLoginActivity.class);
//                                          intent.putExtra(Constants.PHONE , )
                setResult(Activity.RESULT_OK, intent);
//                if (Prefs.getPreferences().contains(Constants.FIREBASE_UID)) {
//
//                } else {
//
//                }
                Prefs.edit().putString(Constants.FIREBASE_UID_FORGOT_PASS, user.getUid()).apply();
                finish();
            } else {
                if (getIntent().hasExtra(Constants.NO_CURRENT_USER)) {
                    Log.e(TAG, "999999999990000: ");
                    spotsDialog.setMessage(getString(R.string.enter));
                    Prefs.edit().putString(Constants.FIREBASE_UID, user.getUid()).apply();
                    FirebaseInstanceId.getInstance().getInstanceId()
                            .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                                @Override
                                public void onComplete(@NonNull Task<InstanceIdResult> task) {
                                    if (task.isSuccessful()) {
                                        DatabaseReference ref = null;
                                        if (Utils.customerOrWorker()) {
                                            ref = RefBase.refUser(Prefs.getString(Constants.FIREBASE_UID, ""));
                                            ref.addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                    dataSnapshot.getRef().removeEventListener(this);
                                                    if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {
                                                        //stop listener
//                                                                                dataSnapshot.getRef().removeEventListener(this);
                                                        User user = dataSnapshot.getValue(User.class);
                                                        user.setMessageToken(task.getResult().getToken());
                                                        //i'm in the same referance
                                                        dataSnapshot.getRef().setValue(user);
                                                        spotsDialog.dismiss();
                                                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                                        startActivity(intent);
                                                        overridePendingTransition(R.anim.enter, R.anim.exit);
                                                        finish();

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
                                                    dataSnapshot.getRef().removeEventListener(this);
                                                    if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {
                                                        CMWorker user = dataSnapshot.getValue(CMWorker.class);
                                                        user.setMessageToken(task.getResult().getToken());
                                                        //i'm in the same referance
                                                        spotsDialog.dismiss();
                                                        dataSnapshot.getRef().setValue(user);
                                                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                                        startActivity(intent);
                                                        overridePendingTransition(R.anim.enter, R.anim.exit);
                                                        finish();
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                                }
                                            });
                                        }
                                    }
                                }
                            });

                } else {

                    if (getIntent().hasExtra(Constants.LOGGED_FROM_ANOTHER_DEVICE)) {
//                                                    Log.e(TAG, "999999999999999: ");
//                                                    spotsDialog.show();


                        if (getIntent().hasExtra(Constants.FIREBASE_UID)) {

                            String userId = getIntent().getStringExtra(Constants.FIREBASE_UID);
                            if (Utils.customerOrWorker()) {
//                                                        RefBase.refUser(Prefs.getString(Constants.FIREBASE_UID, ""))
                                RefBase.refUser(userId)
                                        .child(Constants.LOGIN).setValue(false).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            //Remove message Token After Login VALUE 
                                            RefBase.refUser(userId).child(Constants.MESSAGE_TOKEN).setValue(null).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    spotsDialog.dismiss();
//                                                                    Prefs.edit().remove(Constants.FIREBASE_UID).apply();
                                                    Intent intent = new Intent(getApplicationContext(), CustomerWorkerLoginActivity.class);
                                                    setResult(Activity.RESULT_OK, intent);
                                                    finish();
                                                }
                                            });
                                        }
                                    }
                                });
                            } else {
                                RefBase.refWorker(userId)
                                        .child(Constants.LOGIN).setValue(false).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            spotsDialog.dismiss();
//                                                                    Prefs.edit().remove(Constants.FIREBASE_UID).apply();
                                            Intent intent = new Intent(getApplicationContext(), CustomerWorkerLoginActivity.class);
                                            setResult(Activity.RESULT_OK, intent);
                                            finish();

                                        }
                                    }
                                });
                            }
                        }
//                                                    if (Utils.customerOrWorker()) {
////                                                        RefBase.refUser(Prefs.getString(Constants.FIREBASE_UID, ""))
//
//                                                        RefBase.refUser(getIn)
//                                                                .child(Constants.LOGIN).setValue(false).addOnCompleteListener(new OnCompleteListener<Void>() {
//                                                            @Override
//                                                            public void onComplete(@NonNull Task<Void> task) {
//                                                                if (task.isSuccessful()) {
//                                                                    spotsDialog.dismiss();
////                                                                    Prefs.edit().remove(Constants.FIREBASE_UID).apply();
//                                                                    Intent intent = new Intent(getApplicationContext(), CustomerWorkerLoginActivity.class);
//                                                                    setResult(Activity.RESULT_OK, intent);
//                                                                    finish();
//
//                                                                }
//                                                            }
//                                                        });
//                                                    } else {
//                                                        RefBase.refWorker(user.getUid())
//                                                                .child(Constants.LOGIN).setValue(false).addOnCompleteListener(new OnCompleteListener<Void>() {
//                                                            @Override
//                                                            public void onComplete(@NonNull Task<Void> task) {
//                                                                if (task.isSuccessful()) {
//                                                                    spotsDialog.dismiss();
////                                                                    Prefs.edit().remove(Constants.FIREBASE_UID).apply();
//                                                                    Intent intent = new Intent(getApplicationContext(), CustomerWorkerLoginActivity.class);
//                                                                    setResult(Activity.RESULT_OK, intent);
//                                                                    finish();
//
//                                                                }
//                                                            }
//                                                        });
//                                                    }
                    } else {
                        updateUI(STATE_SIGNIN_SUCCESS, user);
                    }
                }

            }
        }
    }


    @OnClick(R.id.llRechangePhoneNumber)
    public void llRechangePhoneNumberClicked(View v) {
//        Intent intent = new Intent(this, CustomerRegisterActivity.class);
        Intent intent = new Intent(this, CustomerWorkerLoginActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.enter, R.anim.exit);
        onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.fullScreen(this);
        setContentView(R.layout.activity_enter_sms_code);
        ButterKnife.bind(this);
        initVars();
        initTimerResendSmsCode();
        fetchPassedData();
        registerReceiver(connectivityReceiver, new IntentFilter());
    }

    private void fetchPassedData() {
        user = gson.fromJson(Prefs.getString(Constants.USER, ""),
                new TypeToken<User>() {
                }.getType());

        phoneMaterials = gson.fromJson(Prefs.getString(Constants.PHONE_MATERIALS, ""),
                new TypeToken<PhoneMaterials>() {
                }.getType());

        cmWorker = gson.fromJson(Prefs.getString(Constants.WORKER, ""),
                new TypeToken<CMWorker>() {
                }.getType());

        company = gson.fromJson(Prefs.getString(Constants.COMPANY, ""),
                new TypeToken<Company>() {
                }.getType());

//        setMessageToken();

    }

    @Override
    public void onPostCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onPostCreate(savedInstanceState, persistentState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (connectivityReceiver != null) {
            unregisterReceiver(connectivityReceiver);
        }
    }

    private void initTimerResendSmsCode() {
        countDownTimer = new CountDownTimer(60 * 60 * 1000, 1000) {
            // adjust the milli seconds here
            public void onTick(long millisUntilFinished) {
                Log.e(TAG, "onTick: ");
                int sec = (int) (TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(
                        TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)));
                tvResendSmsCode.setText(getString(R.string.resend_sms_after) + " " + sec);
                tvResendSmsCode.setEnabled(false);
                if (sec == 0) {
                    onFinish();
                    countDownTimer.cancel();
//                    this.cancel();
//                    countDownTimer = null;
                }
            }

            public void onFinish() {
                Log.e(TAG, "onFinish: ");
                tvResendSmsCode.setText(getString(R.string.resend_sms));
                tvResendSmsCode.setEnabled(true);
//                countDownTimer.cancel();
//                countDownTimer = null;
            }
        };

    }

    @Override
    protected void onStop() {
        super.onStop();
        if (countDownTimer != null) {
            countDownTimer.cancel();
            Log.e(TAG, "onStop: 3454534534");
        }
    }

    private void initVars() {
        spotsDialog = Utils.getInstance().pleaseWait(this);


        if (Utils.customerOrWorker()) {
            spotsDialog.setMessage(getString(R.string.registering_customer));
        } else {
            if (Utils.companyOrNot()) {
                spotsDialog.setMessage(getString(R.string.registering_company));
            } else {
                spotsDialog.setMessage(getString(R.string.registering_freelancer));
            }
        }


        firebaseAuth = FirebaseAuth.getInstance();

        //firebaseAuth.setLanguageCode("fr");
        // To apply the default app language instead of explicitly setting it.
        firebaseAuth.useAppLanguage();


        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onCodeAutoRetrievalTimeOut(String s) {
                super.onCodeAutoRetrievalTimeOut(s);
                Log.e(TAG, "onCodeAutoRetrievalTimeOut: TimeOut");
            }

            //Called when verification is done without user interaction , ex- when user is verified without code,
            // it's takes PhoneAuthCredential (info about Auth Credential for sms)
            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) { //listener for if the code is send to the same device,
                // credential phoneNum style and its details
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the sms number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verification without
                //     user action.
                Log.e("onVerificationCompleted", "onVerificationCompleted:" + credential);

                // Update the UI and attempt sign in with the sms credential
//                updateUI(STATE_VERIFY_SUCCESS, credential);
//                signInWithPhoneAuthCredential(credential);
            }

            //Called when some error occurred such as failing of sending SMS or Number format exception
            @Override
            public void onVerificationFailed(FirebaseException e) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the sms number format is not valid.
                Log.w(TAG, "onVerificationFailed", e);
//                btnRegisterPhoneNumber.setText(getString(R.string.lets_go));
                //Number format exception
                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
//                    etUserPhone.setError("Invalid Phone Number !");
//                    Sneaker.with(EnterSmsCodeActivity.this).setCategoryName("Invalid Phone Number !").sneakError();
//                    spotsDialog.dismiss();

                } else if (e instanceof FirebaseTooManyRequestsException) { // Quota exceeded
                    // The SMS quota for the project has been exceeded (u send a lot of codes in short time )
//                    Snackbar.make(findViewById(android.R.id.content), "Quota exceeded.",
//                            Snackbar.LENGTH_SHORT).show();
//                    initCountDownTimerResendCode();

                }

                // Show a message and update the UI
//                updateUI(STATE_VERIFY_FAILED);
            }

            //Called when verification code is successfully sent to the sms number.
            //A 'token' that can be used to force re-sending an SMS verification code
            @Override
            public void onCodeSent(String verificationId,
                                   PhoneAuthProvider.ForceResendingToken token) {
                // The SMS verification code has been sent to the provided sms number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                Log.d(TAG, "onCodeSent:" + verificationId);
                Log.d(TAG, "onCodeSent2:" + token);
                // Save verification ID and resending token so we can use them later
//                RegisterActivity registerActivity = new RegisterActivity();
//                mVerificationId = registerActivity.getmVerificationId();
//                mResendToken = registerActivity.getmResendToken();
                mResendToken = token;
                mVerificationId = verificationId;
                updateUI(STATE_CODE_SENT);
            }
        };


        etSmsCode.setOnPinEnteredListener(str -> {
//            Drawable background = tvActivate.getBackground();
//            if (str.toString().length() == 6) {
////                ((ShapeDrawable) background).getPaint().setColor(ContextCompat.getColor(getApplicationContext(), R.color.orange_light));
//                changeDrawableShapeColor(background, R.color.orange_light);
//                tvActivate.setClickable(true);
//                Log.e(TAG, "ewewewewe   ttttt");
//            }
//
//            if (str.toString().length() < 6){
//                changeDrawableShapeColor(background, R.color.lightgrey);
//                tvActivate.setClickable(false);
//                Log.e(TAG, "ewewewewe   777777");
//
//            }
        });
        etSmsCode.requestFocus();
        etSmsCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable str) {
                Drawable background = tvActivate.getBackground();

                if (str.toString().length() == 6) {
//                ((ShapeDrawable) background).getPaint().setColor(ContextCompat.getColor(getApplicationContext(), R.color.orange_light));
                    changeDrawableShapeColor(background, R.color.orange_light);
                    tvActivate.setClickable(true);
                    Log.e(TAG, "ewewewewe   ttttt");
                }

                if (str.toString().length() < 6) {
                    changeDrawableShapeColor(background, R.color.lightgrey);
                    tvActivate.setClickable(false);
                    Log.e(TAG, "ewewewewe   777777");

                }

            }
        });


    }


//    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
//        firebaseAuth.signInWithCredential(credential)
//                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        if (task.isSuccessful()) {
//
//                            Intent intent = new Intent(VerifyPhoneActivity.this, ProfileActivity.class);
//                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//
//                            startActivity(intent);
//
//                        } else {
//                            Toast.makeText(VerifyPhoneActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
//                            progressBar.setVisibility(View.GONE);
//                        }
//                    }
//                });
//    }

    private void changeDrawableShapeColor(Drawable background, int color) {
        if (background instanceof ShapeDrawable) {
            ((ShapeDrawable) background).getPaint().setColor(ContextCompat.getColor(getApplicationContext(), color));
        } else if (background instanceof GradientDrawable) {
            ((GradientDrawable) background).setColor(ContextCompat.getColor(getApplicationContext(), color));
        } else if (background instanceof ColorDrawable) {
            ((ColorDrawable) background).setColor(ContextCompat.getColor(getApplicationContext(), color));
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
//        Intent intent = new Intent(this, CustomerRegisterActivity.class);
        Intent intent = new Intent(this, CustomerWorkerLoginActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.enter, R.anim.exit);
        finish();
    }

    private void updateUI(int uiState) {
        updateUI(uiState, FirebaseAuth.getInstance().getCurrentUser(), null);
    }

    private void updateUI(int uiState, FirebaseUser user) {
        updateUI(uiState, user, null);
    }

    private void updateUI(int uiState, FirebaseUser user, PhoneAuthCredential cred) {
        if (spotsDialog != null) {
            spotsDialog.dismiss();
        }
        switch (uiState) {
            case STATE_INITIALIZED:
                Log.e(TAG, "STATE_INITIALIZED");
                // Initialized state, show only the sms number field and start button
                break;
            case STATE_CODE_SENT:
                Log.e(TAG, "STATE_CODE_SENT");
////                Prefs.edit().remove(Constants.BOTTOM_SHEET_IS_SHOWN).apply();
//                Intent intent = new Intent(this, EnterSmsCodeActivity.class);
//                startActivity(intent);
//                overridePendingTransition(R.anim.enter, R.anim.exit);
//                finish();

//               if(getIntent().hasExtra(Constants.NO_CURRENT_USER)){
//                   if (user == null) {
//                       // Signed out
//                       Log.e(TAG, "updateUI:0 ");
//                   } else {
//                       // Signed in
//                       registerUserInToFirebaseDatabase();
//                       Log.e(TAG, "updateUI:1 ");
//                   }
//               }

                break;
            case STATE_VERIFY_FAILED:
                Log.e(TAG, "STATE_VERIFY_FAILED");

                break;
            case STATE_VERIFY_SUCCESS:
                Log.e(TAG, "STATE_VERIFY_SUCCESS");
                break;
            case STATE_SIGNIN_FAILED:
                Log.e(TAG, "STATE_SIGNIN_FAILED");
                // No-op, handled by sign-in check
                break;
            case STATE_SIGNIN_SUCCESS:
                Log.e(TAG, "STATE_SIGNIN_SUCCESS");
                // Np-op, handled by sign-in check
//                registerUserToFireDatabase(user);
                break;
        }

        if (user == null) {
            // Signed out
            Log.e(TAG, "updateUI:0 ");
        } else {
            // Signed in
            registerUserInToFirebaseDatabase();
            Log.e(TAG, "updateUI:1 ");
        }
    }

    private void registerUserInToFirebaseDatabase() {
        Log.e(TAG, "222222222222222222222: ");
        if (spotsDialog != null)
            spotsDialog.show();


//        if (getIntent().hasExtra(Constants.NO_CURRENT_USER)) {
//            spotsDialog.setMessage(getString(R.string.enter));
//            Log.e(TAG, "222222222222222222222: " );
//
//        }


        if (FirebaseAuth.getInstance().getCurrentUser() == null)
            return;

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (Utils.customerOrWorker()) {
            user.setUserId(firebaseUser.getUid());
        } else {
            if (Utils.companyOrNot()) {
                //company
                Log.e(TAG, "registerUserInToFirebaseDatabase: ");
                company.setCompanyId(firebaseUser.getUid());

            } else {
                Log.e(TAG, "registerUserInToFirebaseDatabase: else ");
                cmWorker.setWorkerId(firebaseUser.getUid());
            }
        }


        if (Utils.customerOrWorker()) {//user
            if (uri != null) {
                uploadPhoto(uri);
            } else {
//                    Toasty.warning(getApplicationContext() , "yoooooooooh").show();
                saveCustomerInfo();
            }
//            uploadPhoto(uri);
        } else {
            if (Utils.companyOrNot()) {
                Log.e(TAG, "registerUserInToFirebaseDatabase: Company " + fileCompanyPdf);
                //upload pdf
                uploadPhotosAndPdf(fileCompanyPdf, false);
//                uploadPhotosAndPdf(fileCompanyLoge, true);
            } else {
                uploadPhotosAndPdf(uri, true);
                Log.e(TAG, "registerUserInToFirebaseDatabase: Not company " + uri);

            }

        }

    }

    private void addNotificationToControlPanel() {

        String key = RefBase.CPNotification().push().getKey();

        String userId = Prefs.getString(Constants.FIREBASE_UID, "");

        CPNotification cpNotification = new CPNotification();
        cpNotification.setCustomerId(userId);
        if (Utils.customerOrWorker()) {
            cpNotification.setKeyword(Constants.USERS);
        } else {
            if (Utils.companyOrNot()) {
                cpNotification.setKeyword(Constants.COMPANY);
            } else {
                cpNotification.setKeyword(Constants.WORKERS);
            }

        }
        Date currentDate = Calendar.getInstance().getTime();
        cpNotification.setTime(currentDate.toString());

        RefBase.CPNotification().child(key)
                .setValue(cpNotification)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.e(TAG, "onSuccess: Sent to CP ");


                    }
                });
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        if (isConnected) {
        } else {
            Common.showSnackError(this, isConnected);
        }
    }

    @Override
    public void onPasswordEncrypted(String encryptedPassword) {
        if (Utils.customerOrWorker()) {
            user.setMessageToken(task.getResult().getToken());
            phoneRegistered.setPhoneNumber(user.getUserPhoneNumber());
            phoneRegistered.setCustomerId(firebaseUser.getUid());
            RefBase.registerPhone().push().setValue(phoneRegistered)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            RefBase.refUsers()
//                .push()
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
//                .child(Prefs.getString(Constants.FIREBASE_UID, ""))
                                    .setValue(user)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.e(TAG, "onSuccess: ");
                                            //generate token for user

                                            spotsDialog.dismiss();
                                            Prefs.edit().putString(Constants.FIREBASE_UID, firebaseUser.getUid()).apply();
                                            Prefs.edit().putString(Constants.INSTANCE_ID, Constants.INSTANCE_ID).apply();

                                            addNotificationToControlPanel();

                                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                            startActivity(intent);
                                            overridePendingTransition(R.anim.enter, R.anim.exit);
                                            finish();
                                            Toast.makeText(EnterSmsCodeActivity.this, "Registered",
                                                    Toast.LENGTH_SHORT);

                                            EventBus.getDefault().removeAllStickyEvents();
//                                        EventBus.getDefault().postSticky(new MsgFrmLoginToNotification());
                                            EventBus.getDefault().postSticky(new MsgFromLoginToFragment());

                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            spotsDialog.dismiss();
                                        }
                                    });

                        }
                    });


        } else {
            if (Utils.companyOrNot()) {
                company.setMessageToken(task.getResult().getToken());
                phoneRegistered.setPhoneNumber(phoneMaterials.getPhoneNumber());
                phoneRegistered.setCustomerId(firebaseUser.getUid());
                RefBase.registerPhone().push().setValue(phoneRegistered)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.e(TAG, "onSuccess: ");
                                RefBase.refCompanies()
                                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .setValue(company)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {

                                                spotsDialog.dismiss();
                                                Prefs.edit().putString(Constants.INSTANCE_ID, Constants.INSTANCE_ID).apply();
                                                Prefs.edit().putString(Constants.FIREBASE_UID, firebaseUser.getUid()).apply();
                                                Prefs.edit().putString(Constants.WORKER_LOGGED_AS, Constants.COMPANY).apply();
                                                Toast.makeText(EnterSmsCodeActivity.this, "Registered",
                                                        Toast.LENGTH_SHORT);
                                                addNotificationToControlPanel();
                                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                                startActivity(intent);
                                                overridePendingTransition(R.anim.enter, R.anim.exit);
                                                finish();

                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
//                        Sneaker.with((Activity) getApplicationContext()).setCategoryName(Constants.NETWORK_ERROR)
//                                .sneakError();
                                                spotsDialog.dismiss();
                                            }
                                        });

                            }
                        });
            } else {
                cmWorker.setMessageToken(task.getResult().getToken());
                phoneRegistered.setPhoneNumber(phoneMaterials.getPhoneNumber());
                phoneRegistered.setCustomerId(firebaseUser.getUid());
                RefBase.registerPhone().push().setValue(phoneRegistered)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.e(TAG, "onSuccess: ");
                                RefBase.refWorkers()
                                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .setValue(cmWorker)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {

                                                spotsDialog.dismiss();
                                                Prefs.edit().putString(Constants.INSTANCE_ID, Constants.INSTANCE_ID).apply();
                                                Prefs.edit().putString(Constants.FIREBASE_UID, firebaseUser.getUid()).apply();
                                                Prefs.edit().putString(Constants.WORKER_LOGGED_AS, cmWorker.getWorkerType()).apply();
                                                Toast.makeText(EnterSmsCodeActivity.this, "Registered",
                                                        Toast.LENGTH_SHORT);
                                                addNotificationToControlPanel();
                                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                                startActivity(intent);
                                                overridePendingTransition(R.anim.enter, R.anim.exit);
                                                finish();

                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
//                        Sneaker.with((Activity) getApplicationContext()).setCategoryName(Constants.NETWORK_ERROR)
//                                .sneakError();
                                                spotsDialog.dismiss();
                                            }
                                        });

                            }
                        });
            }

        }
    }
}

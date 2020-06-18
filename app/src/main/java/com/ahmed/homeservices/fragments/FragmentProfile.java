package com.ahmed.homeservices.fragments;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;

import com.ahmed.homeservices.R;
import com.ahmed.homeservices.activites.cutomer_or_worker.login.CustomerWorkerLoginActivity;
import com.ahmed.homeservices.activites.meowbottomnavigaion.MainActivity;
import com.ahmed.homeservices.activites.profile.EditActivity;
import com.ahmed.homeservices.adapters.grid.CardFragmentPagerAdapter;
import com.ahmed.homeservices.adapters.view_pager.CardPagerAdapterAfterLogin;
import com.ahmed.homeservices.constants.Constants;
import com.ahmed.homeservices.easy_image.SeyanahEasyImage;
import com.ahmed.homeservices.fire_utils.RefBase;
import com.ahmed.homeservices.messages.MsgFrmLoginToNotification;
import com.ahmed.homeservices.models.CMWorker;
import com.ahmed.homeservices.models.CardItem;
import com.ahmed.homeservices.models.Company;
import com.ahmed.homeservices.models.ShadowTransformer;
import com.ahmed.homeservices.models.User;
import com.ahmed.homeservices.utils.FileUtils;
import com.ahmed.homeservices.utils.Utils;
import com.esafirm.imagepicker.features.ImagePicker;
import com.esafirm.imagepicker.model.Image;
import com.github.rubensousa.bottomsheetbuilder.BottomSheetBuilder;
import com.github.rubensousa.bottomsheetbuilder.BottomSheetMenuDialog;
import com.github.ybq.android.spinkit.SpinKitView;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.pixplicity.easyprefs.library.Prefs;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import es.dmoral.toasty.Toasty;
import es.voghdev.pdfviewpager.library.RemotePDFViewPager;
import es.voghdev.pdfviewpager.library.adapter.PDFPagerAdapter;
import es.voghdev.pdfviewpager.library.remote.DownloadFile;
import es.voghdev.pdfviewpager.library.util.FileUtil;
import id.zelory.compressor.Compressor;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import pl.aprilapps.easyphotopicker.EasyImage;
import pl.aprilapps.easyphotopicker.MediaFile;
import pl.aprilapps.easyphotopicker.MediaSource;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;
import pub.devrel.easypermissions.PermissionRequest;

public class FragmentProfile extends Fragment implements
        EasyPermissions.PermissionCallbacks,
        EasyPermissions.RationaleCallbacks,
        SwipeRefreshLayout.OnRefreshListener, DownloadFile.Listener, DownloadFile {
    public static final String[] PERMISSIONS = {
            Manifest.permission.CAMERA,
//            Manifest.permission.WRذITE_CONTACTS,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
    };
    private static final int RC_CAMERA_AND_STORAGE = 121;
    private static final String TAG = "FragmentProfile";
    @BindView(R.id.ivUserPhoto)
    ImageView ivUserPhoto;
    @BindView(R.id.llFullName)
    LinearLayout llFullName;
    @BindView(R.id.ll_profile)
    LinearLayout ll_profile;
    AlertDialog spotsDialog;
    @BindView(R.id.tvFullName)
    TextView tvFullName;
    @BindView(R.id.tvEmail)
    TextView tvEmail;
    @BindView(R.id.tvPhoneNumber)
    TextView tvPhoneNumber;
    @BindView(R.id.tvPassword)
    TextView tvPassword;
    @BindView(R.id.tvWorkerNameAr)
    TextView tvWorkerNameAr;
    @BindView(R.id.tvWorkerNameEn)
    TextView tvWorkerNameEn;
    @BindView(R.id.llWorkerNameAr)
    View llWorkerNameAr;
    @BindView(R.id.llWorkerNameEn)
    View llWorkerNameEn;
    @BindView(R.id.toolbarProfile)
    Toolbar toolbar;
    @BindView(R.id.progress)
    SpinKitView progress;
    @BindView(R.id.llLogin)
    LinearLayout llLogin;
    @BindView(R.id.viewPagerLogin)
    ViewPager viewPagerLogin;
    @BindView(R.id.includeLoginCustomer)
    View includeLoginCustomer;
    @BindView(R.id.includeLoginWorker)
    View includeLoginWorker;
    @BindView(R.id.llEmail)
    View llEmail;
    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.llPdfViewer)
    View llPdfViewer;
    //    @BindView(R.id.remotePdfViewPager)
    RemotePDFViewPager remotePdfViewPager;
    @BindView(R.id.llPdfContainer)
    ViewGroup llPdfContainer;
    private StorageReference ref = null;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private ProgressDialog progressDialog;
    private Intent intentEditActovoty;
    private Context context;
    private CardPagerAdapterAfterLogin mCardAdapter;
    private ShadowTransformer mCardShadowTransformer;
    private CardFragmentPagerAdapter mFragmentCardAdapter;
    private ShadowTransformer mFragmentCardShadowTransformer;
    private int delayMillis = 1000;
    private BottomSheetMenuDialog dialog = null;
    private PDFPagerAdapter adapter;

    @Nullable
    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
        return super.onCreateAnimation(transit, enter, nextAnim);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
        Log.e(TAG, "onStart: work4 ");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_worker_profile, container, false);
        ButterKnife.bind(this, view);
        Log.e(TAG, "onStart: work3 ");
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        checkUserType();
        updateProfilePhoto();
        initVars();

        new Utils(getActivity()).setColorSchemeToSwipeRefrseh(swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(this);

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.N) {
            tvPhoneNumber.setGravity(Gravity.START);
            tvPassword.setGravity(Gravity.START);
        } else {
            tvPhoneNumber.setGravity(Gravity.END);
            tvPassword.setGravity(Gravity.END);
        }
        initIntents();
        Log.e(TAG, "onStart: work2 ");

    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true, priority = 1)
    public void onEvent(MsgFrmLoginToNotification event) {
//        Toast.makeText(getContext(), "Hey, my message" + event.isPushedOrNot(), Toast.LENGTH_SHORT).show();
//        setAdapterToLv();
        if (event != null) {
            checkUserType();
            EventBus.getDefault().removeAllStickyEvents();
        }

    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);

//        checkUserType();
        Log.e(TAG, "onStart: work1 ");
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    private void checkUserType() {
        if (Prefs.contains(Constants.FIREBASE_UID)) {
            Log.e(TAG, "normal user: ");
            String id = Prefs.getString(Constants.FIREBASE_UID, "");
            llLogin.setVisibility(View.GONE);
            ll_profile.setVisibility(View.VISIBLE);
            if (Utils.customerOrWorker()) {
                llWorkerNameAr.setVisibility(View.GONE);
                llWorkerNameEn.setVisibility(View.GONE);
                setUserProfileData();
                toolbar.setNavigationOnClickListener(view -> {
                    //finish();
                    getActivity().onBackPressed();
                });

            } else {


                llWorkerNameAr.setVisibility(View.VISIBLE);
                llWorkerNameEn.setVisibility(View.VISIBLE);
                llFullName.setVisibility(View.GONE);

                setWorkerProfileData();

                switch (Prefs.getString(Constants.WORKER_LOGGED_AS, "")) {
                    case Constants.CM:
                        break;
                    case Constants.FREELANCER:
                        break;

                }
            }
        } else {
            llLogin.setVisibility(View.VISIBLE);
            ll_profile.setVisibility(View.GONE);
//            login();
            accessWorkerCustomerLoginIncludes();

        }
    }

    private void accessWorkerCustomerLoginIncludes() {
        if (Utils.customerOrWorker()) {
            includeLoginCustomer.setVisibility(View.VISIBLE);
            includeLoginWorker.setVisibility(View.GONE);
            includeLoginCustomer.findViewById(R.id.login_customer_btn).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getActivity(), CustomerWorkerLoginActivity.class);
                    getActivity().startActivity(intent);
                    Prefs.edit().putString(Constants.WORKER_OR_CUSTOMER, Constants.CUSTOMERS).apply();
                    getActivity().overridePendingTransition(R.anim.enter, R.anim.exit);

                }
            });
            includeLoginCustomer.findViewById(R.id.tvCustomerSkipRegisteration).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getActivity(), MainActivity.class);
                    getActivity().startActivity(intent);
                    Prefs.edit().putString(Constants.WORKER_OR_CUSTOMER, Constants.CUSTOMERS).apply();
                    getActivity().overridePendingTransition(R.anim.enter, R.anim.exit);

                }
            });

        } else {
            includeLoginCustomer.setVisibility(View.GONE);
            includeLoginWorker.setVisibility(View.VISIBLE);
            includeLoginWorker.findViewById(R.id.login_worker_btn).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.e(TAG, "onClick: test 1.5");
                    Intent intent = new Intent(getActivity(), CustomerWorkerLoginActivity.class);
                    getActivity().startActivity(intent);
                    Prefs.edit().putString(Constants.WORKER_OR_CUSTOMER, Constants.WORKERS).apply();
                    getActivity().overridePendingTransition(R.anim.enter, R.anim.exit);
                }
            });

        }


    }

    private void setWorkerProfileData() {
//        if (spotsDialog != null) {
//            spotsDialog.show();
//        }
        DatabaseReference ref = null;

        if (Utils.companyOrNot()) {
            ref = RefBase.refCompany(Prefs.getString(Constants.FIREBASE_UID, ""));
        } else {
            ref = RefBase.refWorker(Prefs.getString(Constants.FIREBASE_UID, ""));
        }

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                dataSnapshot.getRef().removeEventListener(this);
                if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {
//                            spotsDialog.dismiss();
                    accessViews(dataSnapshot);
                    spotsDialog.dismiss();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                spotsDialog.dismiss();
            }
        });
    }

    private void accessViews(@NonNull DataSnapshot dataSnapshot) {
        if (Utils.companyOrNot()) {
            Company company = dataSnapshot.getValue(Company.class);
            tvFullName.setText(company.getCompanyNameInEnglish());
            tvEmail.setText(company.getCompanyEmail());
            tvPhoneNumber.setText(company.getCompanyPhone());
            tvPassword.setText(company.getCompanyPassword());
            tvWorkerNameAr.setText(company.getCompanyNameInArabic());
            tvWorkerNameEn.setText(company.getCompanyNameInEnglish());

            //get photo
            HashMap<String, Object> map = (HashMap<String, Object>) dataSnapshot.getValue();
            if (map != null) {
                Picasso.get().load(Objects.requireNonNull(map.get(Constants.COMPANY_PHOTO)).toString())
                        .into(ivUserPhoto, new Callback() {
                            @Override
                            public void onSuccess() {
                                Log.e(TAG, "onDataChange: pro ");
                                progress.setVisibility(View.GONE);
                            }

                            @Override
                            public void onError(Exception e) {
                                progress.setVisibility(View.GONE);
                            }
                        });
            }


            //load pdf view
            loadPdf(company);


        } else {
            CMWorker cmWorker = dataSnapshot.getValue(CMWorker.class);
            tvFullName.setText(cmWorker.getWorkerNameInEnglish());
            tvEmail.setText(cmWorker.getWorkerEmail());
            tvPhoneNumber.setText(cmWorker.getWorkerPhone());
            tvPassword.setText(cmWorker.getWorkerPassword());
            tvWorkerNameAr.setText(cmWorker.getWorkerNameInArabic());
            tvWorkerNameEn.setText(cmWorker.getWorkerNameInEnglish());


            //get photo
            HashMap<String, Object> map = (HashMap<String, Object>) dataSnapshot.getValue();
            if (map != null) {
                Picasso.get().load(Objects.requireNonNull(map.get(Constants.WORKER_PHOTO)).toString())
                        .into(ivUserPhoto, new Callback() {
                            @Override
                            public void onSuccess() {
                                Log.e(TAG, "onDataChange: pro ");
                                progress.setVisibility(View.GONE);
                            }

                            @Override
                            public void onError(Exception e) {
                                progress.setVisibility(View.GONE);
                            }
                        });
            }

            llPdfViewer.setVisibility(View.GONE);
        }

    }

    private void loadPdf(Company company) {
        //show the view
        llPdfViewer.setVisibility(View.VISIBLE);

        //load pdf into pdf view pager
        String pdfFilePath = company.getCompanyAttachment();
        remotePdfViewPager = new RemotePDFViewPager(context, pdfFilePath, this);
//        remotePdfViewPager.setDownloader(this);
//        remotePdfViewPager.setId(R.id.remotePdfViewPager);


    }

    private void initIntents() {
        intentEditActovoty = new Intent(getActivity(), EditActivity.class);
    }

    private void initVars() {
        spotsDialog = Utils.getInstance().pleaseWait(getActivity());

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        try {
            if (adapter != null) {
                adapter.close();
            }
        } catch (Exception e) {

        }

    }

    private void setUserProfileData() {
//        if (spotsDialog != null)
//            spotsDialog.show();


        RefBase.refUser(Prefs.getString(Constants.FIREBASE_UID, ""))
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        dataSnapshot.getRef().removeEventListener(this);
                        if (dataSnapshot.exists()) {
//                            dataSnapshot.getRef().removeEventListener(this);
//                            spotsDialog.dismiss();

                            User user = dataSnapshot.getValue(User.class);

                            tvFullName.setText(user.getUserName());
                            tvEmail.setText(user.getUserEmail());
                            tvPhoneNumber.setText(user.getUserPhoneNumber());
                            tvPassword.setText(user.getUserPassword());
                            Log.e(TAG, "userPhoto: " + user.getUserPhoto());

                            Log.e(TAG, user.getUserEmail());

                            if (tvFullName.getText().toString().trim().length() == 0) {
//                                llFullName.setVisibility(View.GONE);
                                tvFullName.setHint(getString(R.string.update_email));
                            }

                            if (tvEmail.getText().toString().trim().length() == 0) {
//                                llEmail.setVisibility(View.GONE);
                                tvEmail.setHint(getString(R.string.update_full_name));
                            }


                            //get user photo
                            HashMap<String, Object> map = (HashMap<String, Object>) dataSnapshot.getValue();
                            if (map != null) {
//                                if (map.get(Constants.USER_PHOTO) != null && map.get(Constants.USER_PHOTO).toString().length() != 0) {
//                            if(user.getUserPhoto() != null){
//                            if(dataSnapshot.hasChild(Constants.USER_PHOTO)){
//                            if (!TextUtils.equals(user.getUserPhoto(), "Null") &&
//                                    map.get(Constants.USER_PHOTO) != null && map.get(Constants.USER_PHOTO).toString().length() != 0) {
//                                Log.e(TAG, "userPhoto: ");
//                                    Picasso.get().load(map.get(Constants.USER_PHOTO).toString())
                                Picasso.get().load(user.getUserPhoto())
                                        .into(ivUserPhoto, new Callback() {
                                            @Override
                                            public void onSuccess() {
//                                                    Toast.makeText(ProfileActivity.this, "Success", Toast.LENGTH_SHORT).show();
                                                progress.setVisibility(View.GONE);

                                            }

                                            @Override
                                            public void onError(Exception e) {
                                                progress.setVisibility(View.GONE);

                                            }
                                        });
//                            } else {
//                                progress.setVisibility(View.GONE);
////                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
////                                    ivUserPhoto.setBackground(getActivity().getDrawable(R.mipmap.account));
////                                } else {
////                                    ivUserPhoto.setBackground(ContextCompat.getDrawable(getActivity(), R.mipmap.account));
////                                }
//                            }
                            }

                            spotsDialog.dismiss();

                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        spotsDialog.dismiss();
                    }
                });

        if (spotsDialog != null && spotsDialog.isShowing())
            spotsDialog.dismiss();

//    }

//        if (FirebaseAuth.getInstance().getCurrentUser() == null)
//            return;
//        RefBase.refUser(FirebaseAuth.getInstance().getCurrentUser().getUid())
//        RefBase.refUser(Prefs.getString(Constants.FIREBASE_UID, ""))
//                .addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                        if (dataSnapshot.exists()) {
//
//                            User user = dataSnapshot.getValue(User.class);
//
//                            tvFullName.setText(user.getUserName());
//                            tvEmail.setText(user.getUserEmail());
//                            tvPhoneNumber.setText(user.getUserPhoneNumber());
//                            tvPassword.setText(user.getUserPassword());
//
//                            //get user photo
//                            HashMap map = (HashMap<String, Object>) dataSnapshot.getValue();
//                            if (map != null) {
//                                if (map.get(Constants.USER_PHOTO) != null) {
//                                    Picasso.get().load(map.get(Constants.USER_PHOTO).toString())
//                                            .into(ivUserPhoto, new Callback() {
//                                                @Override
//                                                public void onSuccess() {
////                                                    Toast.makeText(ProfileActivity.this, "Success", Toast.LENGTH_SHORT).show();
//                                                    progress.setVisibility(View.GONE);
//
//                                                }
//
//                                                @Override
//                                                public void onError(Exception e) {
//
//                                                }
//                                            });
//                                }
//                            }
//
//                            spotsDialog.dismiss();
//
//                        }
//
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError databaseError) {
//                        spotsDialog.dismiss();
//                    }
//                });


    }

    private void updateProfilePhoto() {
        dialog = new BottomSheetBuilder(getContext(), null)
                .setMode(BottomSheetBuilder.MODE_LIST)
//                .setMode(BottomSheetBuilder.MODE_GRID)
                .addDividerItem()
                .expandOnStart(true)
                .setDividerBackground(R.color.grey_400)
                .setBackground(R.drawable.ripple_grey)
                .setMenu(R.menu.menu_image_picker)
                .setItemClickListener(item -> {
                    switch (item.getItemId()) {
                        case R.id.chooseFromCamera:
                            //EasyImage.openChooserWithGallery(getApplicationContext(), "Ch", int type);
//                            SeyanahEasyImage.easyImage(getActivity()).openCameraForImage(FragmentProfile.this);
//                            SeyanahEasyImage.openCamera(getActivity());
//                            ImagePicker.cameraOnly().start(getActivity()); // Could be Activity, Fragment, Support Fragment
                            ImagePicker.cameraOnly().start(this); // Could be Activity, Fragment, Support Fragment


                            break;
                        case R.id.chooseFromGellery:
//                            SeyanahEasyImage.easyImage(getActivity()).openGallery(FragmentProfile.this);
//                            SeyanahEasyImage.openGallery(getActivity());
                            SeyanahEasyImage.openGallery(getActivity(), this);


                            break;
//                        case R.id.removePhoto:
//                            removePhoto();
//                            break;
                    }
                })
                .createDialog();
//        dialog.show();
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e(TAG, "onResume: it's back ");
        checkUserType();

    }

    @OnClick(R.id.tvFullName)
    public void llFullName(View v) {
        intentEditActovoty.putExtra(Constants.EDIT_FIELD_TYPE, Constants.EDIT_FULLNAME);
        intentEditActovoty.putExtra(Constants.EDIT_FULLNAME, tvFullName.getText().toString());
        startActivity(intentEditActovoty);
        Log.e(TAG, "llFullName:  full name1");
    }

    @OnClick(R.id.tvWorkerNameEn)
    public void tvWorkerNameEn(View v) {
        intentEditActovoty.putExtra(Constants.EDIT_FIELD_TYPE, Constants.EDIT_NAME_IN_EN);
        intentEditActovoty.putExtra(Constants.EDIT_NAME_IN_EN, tvWorkerNameEn.getText().toString());
        startActivity(intentEditActovoty);
    }

    @OnClick(R.id.tvWorkerNameAr)
    public void tvWorkerNameAr(View v) {
        intentEditActovoty.putExtra(Constants.EDIT_FIELD_TYPE, Constants.EDIT_NAME_IN_AR);
        intentEditActovoty.putExtra(Constants.EDIT_NAME_IN_AR, tvWorkerNameAr.getText().toString());
        startActivity(intentEditActovoty);
    }

    @OnClick(R.id.tvEmail)
    public void llEmail(View v) {
        Log.e(TAG, "llEmail: ");
        intentEditActovoty.putExtra(Constants.EDIT_FIELD_TYPE, Constants.EDIT_EMAIL);
        intentEditActovoty.putExtra(Constants.EDIT_EMAIL, tvEmail.getText().toString());
        startActivity(intentEditActovoty);
    }

    @OnClick(R.id.tvPhoneNumber)
    public void llPhoneNumber(View v) {
//        intentEditActovoty.putExtra(Constants.EDIT_FIELD_TYPE, Constants.EDIT_PHONE_NUMBER);
//        intentEditActovoty.putExtra(Constants.EDIT_PHONE_NUMBER, tvPhoneNumber.getText().toString());
//        startActivity(intentEditActovoty);


    }

    @OnClick(R.id.ivUserPhoto)
    public void ivUserPhoto(View v) {
        requestCamAndStoragePerms();
    }

    @OnClick(R.id.tvPassword)
    public void llPassword(View v) {
        Log.e(TAG, "llPassword: ");
        intentEditActovoty.putExtra(Constants.EDIT_FIELD_TYPE, Constants.EDIT_PASSWORD);
        intentEditActovoty.putExtra(Constants.EDIT_PASSWORD, tvPassword.getText().toString());
        startActivity(intentEditActovoty);
    }

    @AfterPermissionGranted(RC_CAMERA_AND_STORAGE)
    private void requestCamAndStoragePerms() {
        if (EasyPermissions.hasPermissions(getActivity(), PERMISSIONS)) {
            // Already have permission, do the thing


            if (!dialog.isShowing()) {
                updateProfilePhoto();
                dialog.show();
            }

        } else {
            // Do not have permissions, request them now
//            EasyPermissions.requestPermissions(this, getString(R.string.contacts_and_storage_rationale),
//                    RC_CONTACT_AND_STORAGE, perms);
            EasyPermissions.requestPermissions(
                    new PermissionRequest.Builder(this, RC_CAMERA_AND_STORAGE, PERMISSIONS)
                            .setRationale(R.string.cam_and_storage_rationale)
                            .setPositiveButtonText(R.string.rationale_ask_ok)
                            .setNegativeButtonText(R.string.rationale_ask_cancel)
//                            .setTheme(R.style.AppTheme)
                            .build());

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (ImagePicker.shouldHandle(requestCode, resultCode, data)) {
            // Get a list of picked images
            List<Image> images = ImagePicker.getImages(data);
            // or get a single image only
            Image image = ImagePicker.getFirstImageOrNull(data);
            Uri uri = Uri.fromFile(new File(image.getPath()));
            Log.e(TAG, "onActivityResult: shouldHandle ");
            Picasso.get().load(uri)
                    .into(ivUserPhoto, new Callback() {
                        @Override
                        public void onSuccess() {
//                                Toast.makeText(getActivity(), "Photo updated", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onError(Exception e) {

                        }
                    });
            uploadPhoto(uri);
        }


//        EasyImage.handleActivityResult(requestCode, resultCode, data, getActivity(), new DefaultCallback() {
//            @Override
//            public void onImagePicked(File imageFile, EasyImage.ImageSource source, int type) {
//                Uri uri = Uri.fromFile(imageFile);
//                Picasso.get().load(uri)
//                        .into(ivUserPhoto, new Callback() {
//                            @Override
//                            public void onSuccess() {
////                                Toast.makeText(getActivity(), "Photo updated", Toast.LENGTH_SHORT).show();
//                            }
//
//                            @Override
//                            public void onError(Exception e) {
//
//                            }
//                        });
//                uploadPhoto(uri);
//            }
//        });

        SeyanahEasyImage.easyImage(getActivity()).handleActivityResult(requestCode, resultCode, data, getActivity(), new EasyImage.Callbacks() {
            @Override
            public void onImagePickerError(@NotNull Throwable throwable, @NotNull MediaSource mediaSource) {

            }

            @Override
            public void onMediaFilesPicked(@NotNull MediaFile[] mediaFiles, @NotNull MediaSource mediaSource) {
                Uri uri = Uri.fromFile(mediaFiles[0].getFile());
                Picasso.get().load(uri)
                        .into(ivUserPhoto, new Callback() {
                            @Override
                            public void onSuccess() {
//                                Toast.makeText(getActivity(), "Photo updated", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onError(Exception e) {

                            }
                        });
                uploadPhoto(uri);
            }

            @Override
            public void onCanceled(@NotNull MediaSource mediaSource) {

            }
        });


        super.onActivityResult(requestCode, resultCode, data);


    }

    @SuppressLint("CheckResult")
    private void uploadPhoto(Uri filePath) {


        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        //ref = storageReference.child("images/" + UUID.randomUUID().toString());
        ref = storageReference.child(UUID.randomUUID().toString());

        if (filePath != null) {
            progressDialog = new ProgressDialog(context);
            progressDialog.setMessage("Uploading...");
            progressDialog.setCancelable(false);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();


            File file = null;
            try {
                file = FileUtils.from(context, filePath);
            } catch (IOException e) {
                e.printStackTrace();
            }
            final InputStream[] iStream = {null};
            final byte[][] inputData = {null};
            // Compress image using RxJava in background thread
            assert file != null;
            new Compressor(context)
                    .compressToFileAsFlowable(file)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<File>() {
                        @Override
                        public void accept(File file) {
                            try {
                                iStream[0] = context.getContentResolver().openInputStream(Uri.fromFile(file));
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
                                                        Log.e(TAG, "uploaded:");
                                                        String url = uri.toString();
                                                        String userId = Prefs.getString(Constants.FIREBASE_UID, "");
                                                        if (Utils.customerOrWorker()) {
                                                            RefBase.refUser(userId).addValueEventListener(new ValueEventListener() {
                                                                @Override
                                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                    dataSnapshot.getRef().removeEventListener(this);
                                                                    if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {
                                                                        User user = dataSnapshot.getValue(User.class);
                                                                        user.setUserPhoto(url);
                                                                        dataSnapshot.getRef().setValue(user);
                                                                        progressDialog.dismiss();
                                                                    }
                                                                }

                                                                @Override
                                                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                                                }
                                                            });

                                                        } else {
                                                            RefBase.refWorker(userId).addValueEventListener(new ValueEventListener() {
                                                                @Override
                                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                    dataSnapshot.getRef().removeEventListener(this);
                                                                    if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {
                                                                        CMWorker cmWorker = dataSnapshot.getValue(CMWorker.class);
                                                                        cmWorker.setWorkerPhoto(url);
                                                                        dataSnapshot.getRef().setValue(cmWorker);
                                                                        progressDialog.dismiss();

                                                                    }
                                                                }

                                                                @Override
                                                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                                                }
                                                            });
                                                        }
                                                    }
                                                }).addOnFailureListener(e -> {

                                                });
                                            })
                                            .addOnFailureListener(e -> {

                                                //                                                progressDialog.dismiss();
                                                spotsDialog.dismiss();
//                                            Toast.makeText(context, "Failed " + e.getBody(), Toast.LENGTH_SHORT).show();

                                                Toasty.error(Objects.requireNonNull(getActivity()), Constants.NETWORK_ERROR).show();
                                            })
                                            .addOnProgressListener(taskSnapshot -> {
                                                double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot
                                                        .getTotalByteCount());
                                                progressDialog.setMessage("Uploaded " + (int) progress + "%");
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
                            Toast.makeText(context, throwable.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });


        }

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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);


    }

    private void login() {
        mCardAdapter = new CardPagerAdapterAfterLogin(getActivity());
        mCardAdapter.addCardItem(new CardItem(R.string.title_1, R.string.text_1));
//        mCardAdapter.addCardItem(new CardItem(R.string.title_2, R.string.title_2));

        mFragmentCardAdapter = new CardFragmentPagerAdapter(getActivity().getSupportFragmentManager(),
                Utils.dpToPixels(2, getActivity()));

        mCardShadowTransformer = new ShadowTransformer(viewPagerLogin, mCardAdapter);
        mFragmentCardShadowTransformer = new ShadowTransformer(viewPagerLogin, mFragmentCardAdapter);

        viewPagerLogin.setAdapter(mCardAdapter);
        viewPagerLogin.setPageTransformer(false, mCardShadowTransformer);
        viewPagerLogin.setOffscreenPageLimit(3);
        viewPagerLogin.setPageMargin(1);

        mCardShadowTransformer.enableScaling(true);
        mFragmentCardShadowTransformer.enableScaling(true);


        //for confirming the user that there is an options
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                viewPagerLogin.setCurrentItem(1, true);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        viewPagerLogin.setCurrentItem(0, true);
                    }
                }, delayMillis);
            }
        }, delayMillis);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        if (!dialog.isShowing()) {
            dialog.show();
        }

    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {

    }

    @Override
    public void onRationaleAccepted(int requestCode) {
//        updateProfilePhoto();
    }

    @Override
    public void onRationaleDenied(int requestCode) {


    }

    @Override
    public void onRefresh() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                checkUserType();
                swipeRefreshLayout.setRefreshing(false);
            }
        }, 1300);
    }

    @Override
    public void onSuccess(String url, String destinationPath) {
        Log.e(TAG, "remotePDFViewPager onSuccess: " + url);
        try {
            llPdfContainer.addView(remotePdfViewPager);
            adapter = new PDFPagerAdapter(getActivity(), FileUtil.extractFileNameFromURL(url));
            remotePdfViewPager.setAdapter(adapter);
        } catch (Exception e) {
            Log.e(TAG, "onSuccess: " + e.getMessage());
        }

    }

    @Override
    public void onFailure(Exception e) {

    }

    @Override
    public void onProgressUpdate(int progress, int total) {

    }

    @Override
    public void download(String url, String destinationPath) {

    }
}

package com.ahmed.homeservices.activites.cutomer_or_worker.register;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ahmed.homeservices.R;
import com.ahmed.homeservices.activites.cutomer_or_worker.CustomerOrWorkerActivity;
import com.ahmed.homeservices.activites.cutomer_or_worker.login.CustomerWorkerLoginActivity;
import com.ahmed.homeservices.activites.meowbottomnavigaion.MainActivity;
import com.ahmed.homeservices.activites.sms.EnterSmsCodeActivity;
import com.ahmed.homeservices.adapters.rv.SelectCatAdapter;
import com.ahmed.homeservices.adapters.spinners.CustomAdapter;
import com.ahmed.homeservices.constants.Constants;
import com.ahmed.homeservices.easy_image.SeyanahEasyImage;
import com.ahmed.homeservices.fire_utils.RefBase;
import com.ahmed.homeservices.interfaces.OnRecyclerItemClicked;
import com.ahmed.homeservices.models.CMWorker;
import com.ahmed.homeservices.models.Category;
import com.ahmed.homeservices.models.Country;
import com.ahmed.homeservices.models.Location;
import com.ahmed.homeservices.models.PhoneMaterials;
import com.ahmed.homeservices.phone_utils.PhoneUtils;
import com.ahmed.homeservices.snekers.Snekers;
import com.ahmed.homeservices.utils.Utils;
import com.airbnb.lottie.LottieAnimationView;
import com.developer.kalert.KAlertDialog;
import com.esafirm.imagepicker.features.ImagePicker;
import com.esafirm.imagepicker.model.Image;
import com.github.rubensousa.bottomsheetbuilder.BottomSheetBuilder;
import com.github.rubensousa.bottomsheetbuilder.BottomSheetMenuDialog;
import com.google.android.material.textfield.TextInputEditText;
import com.google.common.reflect.TypeToken;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.mobsandgeeks.saripaar.Rule;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.ConfirmPassword;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.pixplicity.easyprefs.library.Prefs;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import co.ceryle.segmentedbutton.SegmentedButton;
import co.ceryle.segmentedbutton.SegmentedButtonGroup;
import es.dmoral.toasty.Toasty;
import pl.aprilapps.easyphotopicker.EasyImage;
import pl.aprilapps.easyphotopicker.MediaFile;
import pl.aprilapps.easyphotopicker.MediaSource;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;
import pub.devrel.easypermissions.PermissionRequest;

import static com.ahmed.homeservices.constants.Constants.STATE_CODE_SENT;
import static com.ahmed.homeservices.constants.Constants.STATE_INITIALIZED;
import static com.ahmed.homeservices.constants.Constants.STATE_SIGNIN_FAILED;
import static com.ahmed.homeservices.constants.Constants.STATE_SIGNIN_SUCCESS;
import static com.ahmed.homeservices.constants.Constants.STATE_VERIFY_FAILED;
import static com.ahmed.homeservices.constants.Constants.STATE_VERIFY_SUCCESS;

public class WorkerRegisterActivity extends AppCompatActivity implements OnRecyclerItemClicked, MaterialSpinner.OnItemSelectedListener
        , Validator.ValidationListener, EasyPermissions.PermissionCallbacks, EasyPermissions.RationaleCallbacks {

    public static final String[] PERMISSIONS = {
            Manifest.permission.CAMERA,
//            Manifest.permission.WRذITE_CONTACTS,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
    };
    private static final String TAG = "WorkerRegisterActivity";
    private static final int RC_CAMERA_AND_STORAGE = 121;

    @BindView(R.id.etWorkerPhone)
    EditText etWorkerPhone;
    @BindView(R.id.etWorkerNameInArabic)
    EditText etWorkerNameInArabic;

    @NotEmpty
    @BindView(R.id.etWorkerNameInEnglish)
    EditText etWorkerNameInEnglish;

    //    @TextRule(order = 1, minLength = 6, message = "Enter at least 6 characters.")
    @BindView(R.id.etWorkerPass)
    EditText etWorkerPass;


    //    @ConfirmPassword(order = 2)
    @ConfirmPassword(sequence = 2)
    @BindView(R.id.etWorkerConfirmPass)
    EditText etWorkerConfirmPass;
    @BindView(R.id.etWorkerEmail)
    EditText etWorkerEmail;
    @BindView(R.id.spinnerArea)
    MaterialSpinner spinnerArea;
    @BindView(R.id.spinnerCountry)
    MaterialSpinner spinnerCountry;
    @BindView(R.id.spinnerCity)
    MaterialSpinner spinnerCity;
    @BindView(R.id.etEnterLocation)
    TextInputEditText etEnterLocation;
    //Not finish yet
//    @BindView(R.id.tvFreelancer)
//    TextView tvFreelancer;
//    @BindView(R.id.tvCompanyProvider)
//    TextView tvCompanyProvider;
//    @BindView(R.id.rvSelectCat)
    RecyclerView rvSelectCat;
    AlertDialog dlgProgress;
    FirebaseAuth firebaseAuth;
    Gson gson = new Gson();
    //?????
    Type type = new TypeToken<CMWorker>() {
    }.getType();
    @BindView(R.id.segmentedButtonGroup)
    SegmentedButtonGroup segmentedButtonGroup;
    @BindView(R.id.segmentedBtnCompany)
    SegmentedButton segmentedBtnCompany;
    @BindView(R.id.segmentedBtnFreelancer)
    SegmentedButton segmentedBtnFreelancer;
    @BindView(R.id.tvCountryCode)
    TextView tvCountryCode;
    PhoneMaterials phoneMaterials = new PhoneMaterials();
    ChildEventListener childEventListener;
    ValueEventListener valueEventListener;
    @BindView(R.id.spinnerSelectCat)
//    MaterialSpinner spinnerSelectCat;
            Spinner spinnerSelectCat;
    List<String> listCountries = new ArrayList<>();
    List<Country> listCountriesObj = new ArrayList<>();
    List<String> listCities = new ArrayList<>();
    String selectedCity;
    String cityKeySelected;
    Country country;
    String countryId;
    @BindView(R.id.scrollView)
    ScrollView scrollView;
    @BindView(R.id.ivUserPhoto)
    ImageView ivUserPhoto;
    @BindView(R.id.tvFree)
    TextView tvFree;
    @BindView(R.id.tvCompany)
    TextView tvCompany;
    int posFreeOrWorker = 0;
    Validator validator;
    Uri uri;
    boolean validated = false;
//    @BindView(R.id.tvErrPhoneNumber)
//    TextView tvErrPhoneNumber;
//    @BindView(R.id.tvErrNameInAr)
//    TextView tvErrNameInAr;
//    @BindView(R.id.tvErrNameInEn)
//    TextView tvErrNameInEn;
//    @BindView(R.id.tvErrPassword)
//    TextView tvErrPassword;
//    @BindView(R.id.tvErrConfPass)
//    TextView tvErrConfPass;
//    @BindView(R.id.tvErrEmail)
//    TextView tvErrEmail;


    ArrayList<TextView> errors = new ArrayList<>();
    boolean autoRetrieval = false;
    private CMWorker cmWorker = new CMWorker();
    private int posSpinnerArea = 0, posSpinnerCountry = 0, posSpinnerCity = 0;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    //to save state of selectred category
    private Category category = null;
    private List<Category> categories = new ArrayList<>();
    private List<String> listKeyCities = new ArrayList<>();
    //for loading
    private AlertDialog spotsDialog;
    private BottomSheetMenuDialog dialog = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.fullScreen(this);
        setContentView(R.layout.activity_worker_register);
        ButterKnife.bind(this);
        init();
        initStaticSpinner();
//        initCatRecyclerView();
        initSpinnerSelectCat();
        updateProfilePhoto();
//        addingErrors();
    }

//    private void addingErrors() {
//        errors.clear();
//        errors.add(tvErrPhoneNumber);
//        errors.add(tvErrNameInAr);
//        errors.add(tvErrNameInEn);
//        errors.add(tvErrPassword);
//        errors.add(tvErrConfPass);
//        errors.add(tvErrEmail);
//
//    }

    //    @RequiresApi(api = Build.VERSION_CODES.M)
    @OnClick(R.id.tvCompany)
    public void tvCompanyClicked(View v) {

        posFreeOrWorker = 1;

        //text color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            tvCompany.setTextColor(getColor(R.color.white));
        } else {
            tvCompany.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));

        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            tvFree.setTextColor(getColor(R.color.grey_400));
        } else {
            tvFree.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.grey_400));
        }

        //background color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            tvFree.setBackgroundColor(getColor(R.color.white));
        } else {
            tvFree.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            tvCompany.setBackgroundColor(getColor(R.color.Orange));
        } else {
            tvCompany.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.Orange));
        }

    }

    private void showError(int pos, CharSequence error) {

        for (int i = 0; i < errors.size(); i++) {
            errors.get(i).setVisibility(View.GONE);
        }

        errors.get(pos).setVisibility(View.VISIBLE);
        errors.get(pos).setText(error.toString());
    }

    @OnClick(R.id.tvFree)
    public void tvFreeClicked(View v) {
        posFreeOrWorker = 0;

        //text color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            tvCompany.setTextColor(getColor(R.color.grey_400));
        } else {
            tvCompany.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.grey_400));

        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            tvFree.setTextColor(getColor(R.color.white));
        } else {
            tvFree.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
        }

        //background color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            tvFree.setBackgroundColor(getColor(R.color.Orange));
        } else {
            tvFree.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.Orange));
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            tvCompany.setBackgroundColor(getColor(R.color.white));
        } else {
            tvCompany.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
        }

    }

    @Override
    public void onPostCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onPostCreate(savedInstanceState, persistentState);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        validator = new Validator(this);
        validator.setValidationListener(this);


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (ImagePicker.shouldHandle(requestCode, resultCode, data)) {
            // Get a list of picked images
            List<Image> images = ImagePicker.getImages(data);
            // or get a single image only
            Image image = ImagePicker.getFirstImageOrNull(data);
            uri = Uri.fromFile(new File(image.getPath()));
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
        }

//        EasyImage.handleActivityResult(requestCode, resultCode, data, this, new DefaultCallback() {
//            @Override
//            public void onImagePicked(File imageFile, EasyImage.ImageSource source, int type) {
//                uri = Uri.fromFile(imageFile);
//
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
////                uploadPhoto(uri);
//            }
//        });


        SeyanahEasyImage.easyImage(this).handleActivityResult(requestCode, resultCode, data, this, new EasyImage.Callbacks() {
            @Override
            public void onImagePickerError(@NotNull Throwable throwable, @NotNull MediaSource mediaSource) {
                Log.e(TAG, "onImagePickerError: " + throwable.getLocalizedMessage());
            }

            @Override
            public void onMediaFilesPicked(@NotNull MediaFile[] mediaFiles, @NotNull MediaSource mediaSource) {
                uri = Uri.fromFile(mediaFiles[0].getFile());
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
//                uploadPhoto(uri);
            }

            @Override
            public void onCanceled(@NotNull MediaSource mediaSource) {

            }
        });

        super.onActivityResult(requestCode, resultCode, data);

    }

    @OnClick(R.id.ivUserPhoto)
    public void ivUserPhoto(View v) {
        Log.e(TAG, "ivUserPhoto: ");
        requestCamAndStoragePerms();
    }

    private void updateProfilePhoto() {
        dialog = new BottomSheetBuilder(this, null)
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
//                            SeyanahEasyImage.openCamera(this);
//                            SeyanahEasyImage.openCamera(WorkerRegisterActivity.this);
                            ImagePicker.cameraOnly().start(this); // Could be Activity, Fragment, Support Fragment

                            Log.e(TAG, "updateProfilePhoto: cam ");
                            break;
                        case R.id.chooseFromGellery:
//                            SeyanahEasyImage.easyImage(getActivity()).openGallery(FragmentProfile.this);
                            SeyanahEasyImage.openGallery(this);
                            Log.e(TAG, "updateProfilePhoto: ");
                            break;
//                        case R.id.removePhoto:
//                            removePhoto();
//                            break;
                    }
                })
                .createDialog();
//        dialog.show();
    }

    @AfterPermissionGranted(RC_CAMERA_AND_STORAGE)
    private void requestCamAndStoragePerms() {
        if (EasyPermissions.hasPermissions(this, PERMISSIONS)) {
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
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {

    }

    @Override
    public void onRationaleAccepted(int requestCode) {
//        updateProfilePhoto();
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        if (!dialog.isShowing()) {
            dialog.show();
        }

    }

    @Override
    public void onRationaleDenied(int requestCode) {


    }

    private void initSpinnerSelectCat() {
//        CustomAdapter customAdapter = new CustomAdapter(getApplicationContext(),
//                categories = Utils.generateStaticServiceCats());
//        CustomAdapter customAdapter = new CustomAdapter(getApplicationContext(), categories);
//        spinnerSelectCat.setAdapter(customAdapter);
        loadAllCatsIntoSpinner();

        Log.e(TAG, "initSpinnerSelectCat: 100");

//        spinnerSelectCat.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
//                category = categories.get(position);
//            }
//        });
        spinnerSelectCat.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                category = categories.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void initCatRecyclerView() {
        //fill categories
        categories = Utils.generateStaticServiceCats();
        //??
        SelectCatAdapter mAdapter = new SelectCatAdapter(categories, this);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3);
        rvSelectCat.setLayoutManager(gridLayoutManager);
        rvSelectCat.setItemAnimator(new DefaultItemAnimator());
        rvSelectCat.setHasFixedSize(false);
        rvSelectCat.setAdapter(mAdapter);
    }

    private void loadAllCatsIntoSpinner() {

        RefBase.refCategories()
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                        dataSnapshot.getRef().removeEventListener(this);
                        if (dataSnapshot.exists()) {
                            categories.clear();
                            for (DataSnapshot snap :
                                    dataSnapshot.getChildren()) {
                                HashMap<String, Object> map = (HashMap<String, Object>) snap.getValue();
                                Category category = new Category();
                                if (map.get("categoryName") != null)
                                    category.setCategoryName(map.get("categoryName").toString());
                                if (map.get("categoryNameArabic") != null)
                                    category.setCategoryNameArabic(map.get("categoryNameArabic").toString());
                                category.setCategoryId(snap.getKey());
                                if (map.get("categoryIcon") != null)
                                    category.setCategoryIcon(map.get("categoryIcon").toString());

                                categories.add(category);

                                Log.e(TAG, "initSpinnerSelectCat: 102" + categories);

                            }

//                            CustomAdapter customAdapter = new CustomAdapter(getApplicationContext(),
//                                    categories = Utils.generateStaticServiceCats());
                            Log.e(TAG, "initSpinnerSelectCat: 101");

                            CustomAdapter customAdapter = new CustomAdapter(getApplicationContext(),
                                    categories);
                            spinnerSelectCat.setAdapter(customAdapter);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

    }

    private void initStaticSpinner() {
//        spinnerCountry.setItems("Select Country", "Egypt", "Jordan");
        spinnerCountry.setOnItemSelectedListener(this);
//        spinnerCity.setItems("Select City", "Cairo", "Giza");
        spinnerCity.setOnItemSelectedListener(this);
//        spinnerArea.setItems("Select Area", "El-Haram", "El-Dokki", "Masr el Gedida");
//        spinnerArea.setOnItemSelectedListener(this);


        RefBase.CountrySection.refLocCountries()
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                        dataSnapshot.getRef().removeEventListener(this);
                        if (dataSnapshot.exists()) {
                            listCountries.clear();
                            listCountriesObj.clear();
                            listCountriesObj.add(new Country());//fake
                            listCountries.add(Constants.SELECT_COUNTRY);
                            spinnerCity.setVisibility(View.GONE);
                            for (DataSnapshot dataSnapshot1 :
                                    dataSnapshot.getChildren()) {
//                                Log.e(TAG, "ttttttttt: " + dataSnapshot1.toString());
                                HashMap<String, Object> hashMap = (HashMap<String, Object>)
                                        dataSnapshot1.getValue();
                                if (hashMap != null) {
//                                    Log.e(TAG, "onDataChange: " + hashMap.toString());
//                                    Log.e(TAG, "onDataChange: " + dataSnapshot1.getKey());
//                                    Log.e(TAG, "onDataChange: " + hashMap.get("countryName").toString());
                                    Country country = new Country();
                                    country.setCountryId(dataSnapshot1.getKey());

                                    country.setCountryName(hashMap.get("countryName").toString());
                                    country.setCountryNameArabic(hashMap.get("countryNameArabic").toString());
//                                    listCountries.add(country.getCountryName());

                                    if (Utils.lang()) {
                                        listCountries.add(country.getCountryNameArabic());
                                    } else {
                                        listCountries.add(country.getCountryName());
                                    }


                                    listCountriesObj.add(country);
                                }

                            }
                        }
                        spinnerCountry.setItems(listCountries);

                        dataSnapshot.getRef().removeEventListener(this);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

    }

    private void loadCatsFromFirebaseToGridView() {
        showNewSpotsDialog();
        RefBase.refCategories().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {
                    Log.e(TAG, "dataSnapshot 1  " + dataSnapshot.getKey());
                    for (DataSnapshot snapShot :
                            dataSnapshot.getChildren()) {
                        Log.e(TAG, "dataSnapshot 2 " + snapShot.getKey());
                        Category category = null;
                        category = dataSnapshot.getValue(Category.class);
//                        HashMap<String, Object> map = (HashMap<String, Object>) snapShot.getValue();
////                        if (map != null) {
////                            category.setCategoryIcon((String) map.get("CategoryIcon"));
////                            category.setCategoryName((String) map.get("CategoryName"));
////                        }
                        categories.add(category);
                    }
                    if (rvSelectCat != null) {
//                        rvSelectCat.getAdapter().notifyDataSetChanged();
                        CustomAdapter customAdapter = new CustomAdapter(getApplicationContext(), categories);
                        spinnerSelectCat.setAdapter(customAdapter);
                        spotsDialog.dismiss();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                spotsDialog.dismiss();
            }
        });
    }

    private void showNewSpotsDialog() {
        spotsDialog = Utils.getInstance().pleaseWait(this);
//        spotsDialog.setBody("Sending request");
        spotsDialog.setCancelable(true);
        spotsDialog.setCanceledOnTouchOutside(true);
        spotsDialog.show();
    }

    @Override
    public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
        switch (view.getId()) {
            case R.id.spinnerCountry:
                posSpinnerCountry = position;
                if (position != 0) {
                    spinnerCity.setVisibility(View.VISIBLE);
                    country = listCountriesObj.get(position);
                    countryId = country.getCountryId();
                    showNewSpotsDialog();
                    RefBase.CitySection.refLocCities(countryId)
                            .addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                                    dataSnapshot.getRef().removeEventListener(this);

                                    if (dataSnapshot.exists()) {
                                        listCities.clear();
                                        listKeyCities.clear();


                                        listCities.add(Constants.SELECT_CITY);
                                        listKeyCities.add("fake");
                                        for (DataSnapshot dataSnapshot1 :
                                                dataSnapshot.getChildren()) {
//                                        Log.e(TAG, "onDataChange: " + dataSnapshot.toString());
                                            HashMap<String, Object> map = (HashMap<String, Object>) dataSnapshot1.getValue();
                                            if (map != null) {
//                                            Log.e(TAG, "onDataChange: " + map.toString());
//                                            Log.e(TAG, "onDataChange: " + dataSnapshot1.getKey());
                                                Log.e(TAG, "onDataChange: " + map.get("cityName").toString());

//                                            String city = dataSnapshot1.getValue(String.class);
                                                String city;
                                                if (Utils.lang()) {
                                                    city = (String) map.get("cityNameArabic");
                                                } else {
                                                    city = (String) map.get("cityName");

                                                }

                                                if (city != null) {
                                                    String key = dataSnapshot1.getKey();
                                                    listCities.add(city);
                                                    listKeyCities.add(key);
                                                }
//                                            Log.e(TAG, "onDataChange: " + city);
//                                            Log.e(TAG, "onDataChange: " + key);
                                            }
                                        }

                                    }
                                    spotsDialog.dismiss();
                                    spinnerCity.setItems(listCities);
                                    dataSnapshot.getRef().removeEventListener(this);
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    spotsDialog.dismiss();
                                }
                            });
                } else {
                    if (posSpinnerCountry == 0) {
                        spinnerCity.setVisibility(View.GONE);
                        posSpinnerCity = 0;
                    }
                }
                break;
            case R.id.spinnerCity:
                posSpinnerCity = position;
                if (position != 0) {
                    selectedCity = listCities.get(position);
                    cityKeySelected = listKeyCities.get(position);
                }
                break;
            case R.id.spinnerArea:
                posSpinnerArea = position;
                break;
            default:
                break;
        }
    }

    private void init() {

        dlgProgress = Utils.getInstance().pleaseWait(this);

        firebaseAuth = FirebaseAuth.getInstance();

        //Check sms Verification ??
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) { //listener for if the code is send to the same device,
                // credential phoneNum style and its details
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the sms number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verification without
                //     user action.
                Log.e("onVerificationCompleted", "onVerificationCompleted:" + credential);
//                dlgProgress.dismiss();
                // Update the UI and attempt sign in with the sms credential
//                updateUI(STATE_VERIFY_SUCCESS, credential);
                signInWithPhoneAuthCredential(credential);
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) { //Called when some error occurred such as failing of sending SMS or Number format exception

                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the sms number format is not valid.
                Log.w(TAG, "onVerificationFailed", e);
//                btnRegisterPhoneNumber.setText(getString(R.string.lets_go));
                //Number format exception
                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                    etWorkerPhone.setError(Constants.INVALID_PHONE);
                    Toasty.error(getApplicationContext(), Constants.INVALID_PHONE).show();
                    //???
                    Utils.startWobble(getApplicationContext(), etWorkerPhone);
                    Utils.scrollToView(scrollView, etWorkerPhone);
                    dlgProgress.dismiss();

                } else if (e instanceof FirebaseTooManyRequestsException) {
                    // Quota exceeded
                    // The SMS quota for the project has been exceeded (u send a lot of codes in short time )
//                    Snackbar.make(findViewById(android.R.id.content), "Quota exceeded.",
//                            Snackbar.LENGTH_SHORT).show();
                    Toasty.error(getApplicationContext(),
                            "Quota exceeded. u send a lot of codes in short time").show();
//                    initCountDownTimerResendCode();
                    dlgProgress.dismiss();
//                    btnRegisterPhoneNumber.setEnabled(false);
//                    countDownTimer.start();
                }

                // Show a message and update the UI
//                updateUI(STATE_VERIFY_FAILED);
            }


            @Override
            public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken token) {

                // The SMS verification code has been sent to the provided sms number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                Log.e(TAG, "onCodeSent:" + verificationId);
                Log.e(TAG, "onCodeSent2:" + token);
                // Save verification ID and resending token so we can use them later
                mVerificationId = verificationId;
                mResendToken = token;
                updateUI(Constants.STATE_CODE_SENT);
            }
        };
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        Log.e(TAG, "signInWithPhoneAuthCredential: ");
        FirebaseAuth.getInstance().signInWithCredential(credential)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.e(TAG, "signInWithPhoneAuthCredential:    isSuccessful ");
                        autoRetrieval = true;
                        dlgProgress.dismiss();
                        codeSentSuccess();
//                            Intent intent = new Intent(VerifyPhoneActivity.this, ProfileActivity.class);
//                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                            startActivity(intent);

                    } else {
//                            Toast.makeText(VerifyPhoneActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
//                            progressBar.setVisibility(View.GONE);
                    }
                });
    }

    public boolean isValidPassword(final String password) {

        Pattern pattern;
        Matcher matcher;

        final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=\\S+$).{6,}$";

        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(password);

        return matcher.matches();

    }

    @OnClick(R.id.tvWorkerLogin)
    void tvWorkerLogin(View view) {
//        if (Utils.customerOrWorker()) {
//            Intent intentCustomer = new Intent(this, CustomerRegisterActivity.class);
//            startActivity(intentCustomer);
//        } else {
//            Intent intentWorkers = new Intent();
//            intentWorkers.setClass(this, WorkerRegisterActivity.class);
//            startActivity(intentWorkers);
//        }

        finish();
        Intent intentWorkers = new Intent();
        intentWorkers.setClass(this, CustomerWorkerLoginActivity.class);
        startActivity(intentWorkers);

    }

    @OnClick(R.id.btn_worker_register)
    void btnWorkerRegister(View v) {

//        validator.validate();

        if (uri == null) {
//            Utils.startWobble(getApplicationContext(), etEnterPhone);
//            etEnterPhone.setError("Enter sms number");
            //Toasty.warning(getApplicationContext(), "Upload photo").show();
            scrollView.fullScroll(View.FOCUS_UP);
            Snekers.getInstance().error(getString(R.string.upload_photo), this);
            updateProfilePhoto();
//            Utils.scrollToView(scrollView, etWorkerPhone);
            return;
        }

        if (TextUtils.isEmpty(etWorkerPhone.getText())) {
            Utils.startWobble(getApplicationContext(), etWorkerPhone);
            //etWorkerPhone.setError(getString(R.string.error_enter_phone));
//            showError(0, getString(R.string.error_enter_phone));
            Snekers.getInstance()
                    .error(getString(R.string.error_enter_phone),
                            this);
            Utils.scrollToView(scrollView, etWorkerPhone);
            return;
        }

        if (TextUtils.isEmpty(etWorkerNameInArabic.getText())) {
            Utils.startWobble(getApplicationContext(), etWorkerNameInArabic);
            //etWorkerNameInArabic.setError(getString(R.string.error_enter_name));
//            showError(1, getString(R.string.error_enter_name));
            Snekers.getInstance()
                    .error(getString(R.string.error_enter_name),
                            this);
            Utils.scrollToView(scrollView, etWorkerNameInArabic);
            return;
        }

        if (TextUtils.isEmpty(etWorkerNameInEnglish.getText())) {
            Utils.startWobble(getApplicationContext(), etWorkerNameInEnglish);
            //etWorkerNameInEnglish.setError(getString(R.string.error_enter_english_name));
//            showError(2, getString(R.string.error_enter_english_name));
            Snekers.getInstance()
                    .error(getString(R.string.error_enter_english_name),
                            this);
            Utils.scrollToView(scrollView, etWorkerNameInEnglish);
            return;
        }

        if (TextUtils.isEmpty(etWorkerPass.getText())) {
            Utils.startWobble(getApplicationContext(), etWorkerPass);
            //etWorkerPass.setError(getString(R.string.enter_pass));
//            showError(3, getString(R.string.enter_pass));
            Snekers.getInstance()
                    .error(getString(R.string.enter_pass),
                            this);
            Utils.scrollToView(scrollView, etWorkerPass);
            return;
        }

        if (!Utils.isValidPassword(etWorkerPass.getText().toString().trim())) {
            Utils.startWobble(getApplicationContext(), etWorkerPass);
            //etWorkerPass.setError(getString(R.string.enter_6_chars));
//            showError(4, getString(R.string.enter_6_chars));
//            Snekers.getInstance().error(getString(R.string.enter_6_chars), this);

            KAlertDialog kDialog = new KAlertDialog(this, KAlertDialog.WARNING_TYPE)
                    .setTitleText(getString(R.string.app_name_root))
                    .setContentText(getString(R.string.enter_6_chars))
                    .setConfirmText(getString(R.string.ok))
                    .setConfirmClickListener(new KAlertDialog.KAlertClickListener() {
                        @Override
                        public void onClick(KAlertDialog kAlertDialog) {
                            kAlertDialog.dismissWithAnimation();
                        }
                    });
            kDialog.setCancelable(true);
            kDialog.setCanceledOnTouchOutside(true);
            kDialog.show();

            Utils.scrollToView(scrollView, etWorkerPass);

            return;
        }

        if (!TextUtils.equals(etWorkerPass.getText(), etWorkerConfirmPass.getText())) {
            Utils.startWobble(getApplicationContext(), etWorkerConfirmPass);
            //etWorkerConfirmPass.setError(getString(R.string.error_pass_donot_match));
//            showError(5, getString(R.string.error_pass_donot_match));
            Snekers.getInstance()
                    .error(getString(R.string.error_pass_donot_match),
                            this);
            Utils.scrollToView(scrollView, etWorkerConfirmPass);
            return;
        }


        if (TextUtils.isEmpty(etWorkerEmail.getText())) {
            Utils.startWobble(getApplicationContext(), etWorkerEmail);
            //etWorkerEmail.setError(getString(R.string.enter_email));
//            showError(6, getString(R.string.enter_email));
            Snekers.getInstance()
                    .error(getString(R.string.enter_email),
                            this);
            Utils.scrollToView(scrollView, etWorkerEmail);
            return;
        }

        if (!Utils.isEmailValid(etWorkerEmail.getText().toString())) {
            Utils.startWobble(getApplicationContext(), etWorkerEmail);
            Utils.scrollToView(scrollView, etWorkerEmail);

            //etWorkerEmail.setError(getString(R.string.enter_valid_email));
//            showError(7, getString(R.string.error_enter_name));
            Snekers.getInstance()
                    .error(getString(R.string.enter_valid_email),
                            this);
            return;
        }


        if (posSpinnerCountry == 0) {
            //Toasty.error(this, getString(R.string.select_country)).show();
            Snekers.getInstance()
                    .error(getString(R.string.select_country),
                            this);
            Utils.scrollToView(scrollView, spinnerCountry);
            return;
        }

        if (posSpinnerCity == 0) {
            //Toasty.error(this, getString(R.string.select_city)).show();
            Snekers.getInstance()
                    .error(getString(R.string.select_city),
                            this);
            Utils.scrollToView(scrollView, spinnerCity);
            return;
        }
//        if (posSpinnerArea == 0) {
//            Toasty.error(this, "Select area").show();
//
//            return;
//        }


        if (TextUtils.isEmpty(etEnterLocation.getText())) {
            Utils.startWobble(getApplicationContext(), etEnterLocation);
            //etEnterLocation.setError(getString(R.string.enter_loc_address));
            Snekers.getInstance()
                    .error(getString(R.string.enter_loc_address),
                            this);
            Utils.scrollToView(scrollView, etEnterLocation);
            return;
        }

//        if (category == (null)) {
//            Toasty.error(this, "Select a category").show();
//            return;
//        }

        //else
        // register

        if (dlgProgress != null && !dlgProgress.isShowing()) {
            dlgProgress.setMessage("Registering");
            dlgProgress.show();
        }
        Query query = RefBase.registerPhone()
                .orderByChild(Constants.PHONE_FROM_USER_MODEL)
                .equalTo(etWorkerPhone.getText().toString().trim());

        valueEventListener = query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                dataSnapshot.getRef().removeEventListener(this);
                Log.e(TAG, "onDataChange: 4444444444444");
                if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {
                    childEventListener = query.addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                            dataSnapshot.getRef().removeEventListener(this);
                            Log.e(TAG, "onDataChange: 7777777777777");
                            if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {

                                Log.e(TAG, "onChildAdded: 3");
                                Toasty.error(getApplicationContext(),
                                        Constants.PHONE_EXIST).show();
                                Utils.clearAllErrors(findViewById(android.R.id.content));
                                etWorkerPhone.setError(Constants.PHONE_EXIST);
                                Utils.scrollToView(scrollView, etWorkerPhone);
                                dlgProgress.dismiss();
//                                dataSnapshot.getRef().removeEventListener(this);
//                                dataSnapshot.getRef().removeEventListener(listener1[0]);

                            } else {
                                Log.e(TAG, "onChildAdded: 4");
//                                                    Toasty.success(getApplicationContext(),
//                                                            "Phone not exist").show();
                                startVirificationOperation();
//                                dataSnapshot.getRef().removeEventListener(this);
                            }
//                            dataSnapshot.getRef().removeEventListener(childEventListener);


                        }

                        @Override
                        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                        }

                        @Override
                        public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                        }

                        @Override
                        public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

//                    dataSnapshot.getRef().removeEventListener(this);
                } else {
                    Log.e(TAG, "onDataChange: 8888888888888888");
                    startVirificationOperation();
                }
//                dataSnapshot.getRef().removeEventListener(valueEventListener);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
//        RefBase.refWorkers()
//                .addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                        Log.e(TAG, "onDataChange: 1 ");
//                        if (!dataSnapshot.exists()) {
//                            startVirificationOperation();
//                        } else {
//                            Log.e(TAG, "onDataChange: 2");
//
//
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                    }
//                });


    }

    @OnClick(R.id.tvWorkerLogin)
    void tv_login_worker(View v) {
        finish();
        Intent intentWorkers = new Intent();
        intentWorkers.setClass(this, CustomerOrWorkerActivity.class);
        startActivity(intentWorkers);
    }

    private void updateUI(int uiState) {
        updateUI(uiState, firebaseAuth.getCurrentUser(), null);

    }

    private void updateUI(int uiState, FirebaseUser user, PhoneAuthCredential cred) {
        if (dlgProgress != null) {
            dlgProgress.dismiss();
        }
        switch (uiState) {
            case STATE_INITIALIZED:
                Log.e(TAG, "STATE_INITIALIZED");
                // Initialized state, show only the sms number field and start button
                break;
            case STATE_CODE_SENT:
                Log.e(TAG, "STATE_CODE_SENT");
//                Prefs.edit().remove(Constants.BOTTOM_SHEET_IS_SHOWN).apply();
                codeSentSuccess();
                break;
            case STATE_VERIFY_FAILED:
                Log.e(TAG, "STATE_VERIFY_FAILED");

                break;
            case STATE_VERIFY_SUCCESS:
                Log.e(TAG, "");
                break;
            case STATE_SIGNIN_FAILED:
                Log.e(TAG, "STATE_SIGNING_FAILED");
                // No-op, handled by sign-in check
                break;
            case STATE_SIGNIN_SUCCESS:
                Log.e(TAG, "STATE_SIGNING_SUCCESS");
                // Np-op, handled by sign-in check
//                registerUserToFireDatabase(user);
                break;
        }


    }

    private void startVirificationOperation() {
        cmWorker.setWorkerPhone(etWorkerPhone.getText().toString());
        cmWorker.setWorkerNameInArabic(etWorkerNameInArabic.getText().toString());
        cmWorker.setWorkerNameInEnglish(etWorkerNameInEnglish.getText().toString());


//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    cmWorker.setWorkerPassword(Utils.encrypt(etWorkerPass.getText().toString()));
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        });
        cmWorker.setWorkerPassword(etWorkerPass.getText().toString());
//        cmWorker.setWorkerConfirmPassword(etWorkerConfirmPass.getText().toString());
        cmWorker.setWorkerEmail(etWorkerEmail.getText().toString());
        cmWorker.setLogin(true);

        Location location = new Location();
//        location.setCountry(spinnerCountry.getItems().get(posSpinnerCountry).toString());

        if (Utils.lang()) {
            location.setCountry(country.getCountryNameArabic());
        } else {
            location.setCountry(country.getCountryName());
        }


//        location.setCountry(country.getCountryName());
//        location.setCity(spinnerCity.getItems().get(posSpinnerCity).toString());
        location.setCity(selectedCity);
        location.setCityId(cityKeySelected);
        location.setCountryId(countryId);
//        location.setArea(spinnerArea.getItems().get(posSpinnerArea).toString());
//        cmWorker.setWorkerLocationId(locat);
        cmWorker.setWorkerLocation(location);
        cmWorker.setWorkerLocationAdress(etEnterLocation.getText().toString());

//        cmWorker.setWorkerType(Constants.WORKER_TYPE_CM);
//        if (segmentedButtonGroup.getPosition() == 0) {
        if (posFreeOrWorker == 0) {
            cmWorker.setWorkerType(Constants.FREELANCER);
        } else {
            cmWorker.setWorkerType(Constants.CM);
        }
        //type hereeeeeee

//        cmWorker.setWorkerCategory(category);
        cmWorker.setWorkerCategoryid(category.getCategoryId());

        Date date = Calendar.getInstance().getTime();
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String today = dateFormat.format(date);
        cmWorker.setCreateDate(today);


        //        String fullPhone = tvCountryCode.getText().toString() + etEnterPhone.getText().toString();
//        String fullPhone = getString(R.string.country_code) + etEnterPhone.getText().toString();
        String fullPhone = Constants.COUNTRY_CODE + etWorkerPhone.getText().toString();


        Log.e(TAG, "register: " + fullPhone);
        fullPhone = fullPhone.trim();

        new PhoneUtils(this).startPhoneNumberVerification(fullPhone, mCallbacks);
    }

    private void codeSentSuccess() {

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        Intent intent;


        if (firebaseUser == null || !Prefs.contains(Constants.FIREBASE_UID)) {
            // Signed out
            Log.e(TAG, "codeSentSuccess: ");
            phoneMaterials.setmResendToken(mResendToken);
            phoneMaterials.setVerificationId(mVerificationId);
            phoneMaterials.setPhoneNumber(etWorkerPhone.getText().toString());

            Log.e(TAG, "codeSentSuccess: " + phoneMaterials.getmResendToken() + phoneMaterials.getPhoneNumber() + phoneMaterials.getVerificationId());

            Prefs.putString(Constants.PHONE_MATERIALS, gson.toJson(phoneMaterials, new TypeToken<PhoneMaterials>() {
            }.getType()));
            Prefs.putString(Constants.WORKER, gson.toJson(cmWorker, new TypeToken<CMWorker>() {
            }.getType()));

            Log.e(TAG, "codeSentSuccess: " + Prefs.getString(Constants.WORKER, ""));

            intent = new Intent(this, EnterSmsCodeActivity.class);


            intent.putExtra(Constants.USER_PHOTO_URI, uri.toString());
            if (autoRetrieval) {
                intent.putExtra(Constants.AUTO_SMS_RETRIEVAL, true);
            }


            startActivity(intent);
            overridePendingTransition(R.anim.enter, R.anim.exit);
            finish();


        } else {
            // Signed in
            intent = new Intent(getApplicationContext(), MainActivity.class);
//          intent.putExtra(Constants.USER_TYPE, Constants.DRIVERS);
            overridePendingTransition(R.anim.enter, R.anim.exit);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void onClick(View view, int pos) {
        category = null;
        Log.e(TAG, "onClick: ooo ");
//        for (int i = 0; i < rvSelectCat.getChildCount(); i++) {
//            View view2 = rvSelectCat.getChildAt(i);
//            view2.findViewById(R.id.llChecked).setVisibility(View.GONE);
//        }
        if (view.findViewById(R.id.llChecked).getVisibility() != View.VISIBLE) {

            for (int i = 0; i < rvSelectCat.getChildCount(); i++) {
                View view2 = rvSelectCat.getChildAt(i);
                view2.findViewById(R.id.llChecked).setVisibility(View.GONE);
            }

            Log.e(TAG, "onClick: 1");
            category = categories.get(pos);
//            category.setVisible(true);
            view.findViewById(R.id.llChecked).setVisibility(View.VISIBLE);
            LottieAnimationView lottieAnimationView = view.findViewById(R.id.lottieAnimationChecked);
            if (!lottieAnimationView.isAnimating()) {
                lottieAnimationView.playAnimation();
            } else {
                lottieAnimationView.pauseAnimation();
                lottieAnimationView.playAnimation();
            }
        } else {
//            Log.e(TAG, "onClick: 2");
//            category.setVisible(false);
//            view.findViewById(R.id.llChecked).setVisibility(View.GONE);
//            category = null;

            for (int i = 0; i < rvSelectCat.getChildCount(); i++) {
                View view2 = rvSelectCat.getChildAt(i);
                view2.findViewById(R.id.llChecked).setVisibility(View.GONE);
            }

        }

//        int i = 0;
//        stateProgressBar.checkStateCompleted(true);
//        collapseAll(expansionLayouts.get(i));
//        rotateTo0Degree(expansionLayouts.get(i));
//
//        i++;
//        Utils.expand(expansionLayouts.get(i).getExpansionLayout());
//        rotateTo90Degree(expansionLayouts.get(i));


    }

    public void onValidationSucceeded() {
//        Toast.makeText(this, "Yay! we got it right!", Toast.LENGTH_SHORT).show();
        validated = true;
    }

    @Override
    public void onValidationFailed(List<ValidationError> errors) {

    }

    public void onValidationFailed(View failedView, Rule<?> failedRule) {
        validated = false;
//        String message = failedRule.getFailureMessage();
//
//        if (failedView instanceof EditText) {
//            failedView.requestFocus();
//            ((EditText) failedView).setError(message);
//        } else {
//            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
//        }
    }


}

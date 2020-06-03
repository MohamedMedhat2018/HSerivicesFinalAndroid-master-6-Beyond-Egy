package com.ahmed.homeservices.fragments;


import android.Manifest;
import android.animation.Animator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatRatingBar;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.ahmed.homeservices.R;
import com.ahmed.homeservices.activites.cutomer_or_worker.login.CustomerWorkerLoginActivity;
import com.ahmed.homeservices.activites.meowbottomnavigaion.MainActivity;
import com.ahmed.homeservices.adapters.firebase_adapters.FireFetchCategoryAdapter;
import com.ahmed.homeservices.adapters.grid.AttachPhotosAdapter;
import com.ahmed.homeservices.adapters.rv.SelectCatAdapter;
import com.ahmed.homeservices.constants.Constants;
import com.ahmed.homeservices.easy_image.SeyanahEasyImage;
import com.ahmed.homeservices.fire_utils.RefBase;
import com.ahmed.homeservices.interfaces.OnPhotoClick;
import com.ahmed.homeservices.interfaces.OnPhotoClosed;
import com.ahmed.homeservices.interfaces.OnRecyclerItemClicked;
import com.ahmed.homeservices.messages.MsgEventReloadFragment;
import com.ahmed.homeservices.messages.MsgFromLoginToFragment;
import com.ahmed.homeservices.models.CMWorker;
import com.ahmed.homeservices.models.CPNotification;
import com.ahmed.homeservices.models.Category;
import com.ahmed.homeservices.models.CmTask;
import com.ahmed.homeservices.models.ConnectionFree;
import com.ahmed.homeservices.models.Country;
import com.ahmed.homeservices.models.ExpansionData;
import com.ahmed.homeservices.models.Location;
import com.ahmed.homeservices.models.ModeEdit;
import com.ahmed.homeservices.models.Rate;
import com.ahmed.homeservices.models.orders.OrderPending;
import com.ahmed.homeservices.models.orders.OrderRequest;
import com.ahmed.homeservices.reusable.ExpansionHeaderWithLayout;
import com.ahmed.homeservices.utils.FileUtils;
import com.ahmed.homeservices.utils.Utils;
import com.airbnb.lottie.LottieAnimationView;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.esafirm.imagepicker.features.ImagePicker;
import com.esafirm.imagepicker.model.Image;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.github.rubensousa.bottomsheetbuilder.BottomSheetBuilder;
import com.github.rubensousa.bottomsheetbuilder.BottomSheetMenuDialog;
import com.github.siyamed.shapeimageview.CircularImageView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.pixplicity.easyprefs.library.Prefs;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.stepstone.apprating.listener.RatingDialogListener;
import com.watermark.androidwm_light.WatermarkBuilder;
import com.watermark.androidwm_light.bean.WatermarkText;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import es.dmoral.toasty.Toasty;
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

import static com.ahmed.homeservices.constants.Constants.Modes.EDIT_CASHED_ORDER;
import static com.ahmed.homeservices.constants.Constants.Modes.EDIT_PENDING_ORDER;
import static com.ahmed.homeservices.constants.Constants.Modes.NORMAL;
import static com.ahmed.homeservices.utils.Utils.getBytes;

public class FragmentRequestOrder extends Fragment implements DatePickerDialog.OnDateSetListener
        , TimePickerDialog.OnTimeSetListener
        , EasyPermissions.PermissionCallbacks
        , EasyPermissions.RationaleCallbacks
        , OnPhotoClick
        , OnPhotoClosed
        , OnRecyclerItemClicked
        , DialogInterface.OnDismissListener
        , RatingDialogListener
        , MaterialSpinner.OnItemSelectedListener
        , DialogInterface.OnCancelListener
        , SwipeRefreshLayout.OnRefreshListener,
        OnRecyclerItemClicked.FireUI {

    private static final String[] PERMISSIONS = {
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE,
    };
    private static final String TAG = "FragmentRequestOrder";
    //request code when back from camera with data
    private static final int RC_CAMERA_AND_STORAGE = 121;
    @BindView(R.id.questionsContainer)
    LinearLayout questionsContainer;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.btnOrderNow)
    AppCompatButton btnOrderNow;
    @BindView(R.id.btnBack)
    AppCompatButton btnBack;
    @BindView(R.id.tvConfirmRequest)
    TextView tvConfirmRequest;
    @BindView(R.id.ivWaterMark)
    ImageView ivWaterMark;
    @BindView(R.id.scrollView)
    ScrollView scrollView;
    HashMap<Integer, String> hmModeDeletion = new HashMap<>(), hmModeEdition = new HashMap<>();
    private List<ModeEdit> lisFromUserDev = new ArrayList<>();
    FireFetchCategoryAdapter fireFetchCategoryAdapter;
    private CircularImageView circularImageView;
    private View v;
    private AlertDialog alertDialog;
    private AlertDialog.Builder builder;
    private View progressBar;
    private AppCompatRatingBar ratingBar;
    private TextInputEditText textInputEditText;
    private String cityKeySelected;
    private Country country;
    private List<String> listCountries = new ArrayList<>();
    private List<Country> listCountriesObj = new ArrayList<>();
    private List<String> listCities = new ArrayList<>();
    private String selectedCity;
    private TimePickerDialog pickerTime;
    private int posUploadImage = 0;
    private Type type = new TypeToken<OrderRequest>() {
    }.getType();
    private int posSpinnerArea = 0, posSpinnerCountry = 0, posSpinnerCity = 0;
    private List<ExpansionData> fixedQuestions = null;
    //inflation: get layout from layout resource  (outside view)
//    private TextInputEditText etDatePicker;
//    private TextInputEditText etTimePicker;
    private TextView etDatePicker;
    private TextView etTimePicker;
    private LinearLayout llDatePicker;
    private LinearLayout llTimePicker;
    private GridView gridViewAttachedPhotos;
    private int position;
    private int positionForEdit;
    private Activity context;
    private Calendar calenderNow;
    private List<Category> categories = new ArrayList<>();
    //for loading
    private AlertDialog spotsDialog;
    private RecyclerView rvSelectCat;
    //use to fill data (list of Expansions header and layout)
    private List<ExpansionHeaderWithLayout> expansionLayouts = new ArrayList<>();
    private MaterialSpinner spinnerCountry, spinnerCity, spinnerArea;
    private Category category = null;
    private int dur = 300;
    private TextInputEditText etEnterLocation;
    private TextInputEditText etEnterDesc;
    private OrderRequest orderRequest = null;
    //    private ArrayList<Uri> uris = new ArrayList<>(10);
    //    private ArrayList<Uri> uris = new ArrayList<>();
//    private List<Uri> uris = Arrays.asList(new Uri[10]);
//    List<Uri> uris = FixedSizeList.fixedSizeList(Arrays.asList(new Uri[10]));
//    private HashMap<Integer, Uri> hmOrderRequestUris = new HashMap();
    private HashMap<Integer, String> hmOrderRequestUris = new HashMap();
    private Gson gson = new Gson();
    private BottomSheetMenuDialog dialog = null;
    private DatePickerDialog pickerDate;
    private StorageReference ref = null;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private ProgressDialog progressDialog;
    private File actualImage;
    private File compressedImage;
    //    private List<Uri> uris = new ArrayList<>();
    private List<String> uris = new ArrayList<>();
    private int posUploadPhoto = 0;
    private ArrayList<String> listLinks = new ArrayList<>();
    private List<String> listKeyCities = new ArrayList<>();
    private String countryId;
    private String userId = null;
    private Rate rateToSubmit = new Rate();
    private CmTask cmTask;
    private OrderRequest orderRequestRate;
    //    @BindView(R.id.ll_for_company_service_rate)
//    LinearLayout ll_for_company_service_rate;
//    @BindView(R.ID.usDatePickerDialogerPhoto)
    private View loadingCats = null;
    private boolean alerRateShown = true;
    private ValueEventListener valueEventListener, valueEventListener1;
    private DatabaseReference reference;
    private String modeOnPhotoClicked = null;
    private List<ModeEdit> listMo3adalaEditPending;
    private Location locationFetchedOrder = new Location();

    @OnClick(R.id.btnBack)
    void btnBack(View v) {
        EventBus.getDefault().post(new MsgEventReloadFragment(true));


    }

    @OnClick(R.id.btnOrderNow)
    void btnOrderNow(View v) {
        Log.e(TAG, "btnOrderNow: ");
//        BottomSheetFragment bottomSheetFragment = new BottomSheetFragment();
//        bottomSheetFragment.show(context.getSupportFragmentManager(), "BottomSheetFragment");
        confirmAndSedRequest();
//        sendOrderToFirebaseDatabase(orderRequest);
    }

    public void setLocale(String lang) {
        Locale myLocale = new Locale(lang);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
        Intent refresh = new Intent(context, MainActivity.class);
        context.finish();
        startActivity(refresh);
    }

    private void sendOrderToFirebaseDatabase(OrderRequest orderRequest) {
        if (uris.isEmpty()) {
            saveOrderIntoDatabase();
        } else {
            listLinks.clear();
            showNewSpotsDialog();
            if (!uris.isEmpty()) {
//                uploadPhoto(uris.get(posUploadImage), posUploadPhoto);
                uploadPhoto(Uri.parse(uris.get(posUploadImage)), posUploadPhoto);
            }
//            for (int i = 0; i < uris.size(); i++) {
////                uploadPhoto(uris.get(i));
//                uploadImage(uris.get(i), pos);
//            }
//        });
        }
    }

    private void showNewSpotsDialog() {
        spotsDialog = Utils.getInstance().pleaseWait(getActivity());
        spotsDialog.setMessage(context.getString(R.string.send_request));
        spotsDialog.setCancelable(false);
        spotsDialog.setCanceledOnTouchOutside(false);
        spotsDialog.show();
    }

    private void saveOrderIntoDatabase() {
        if (spotsDialog != null) {
            if (!spotsDialog.isShowing()) {
                showNewSpotsDialog();
            }
        }

        Log.e(TAG, "order is empty or not " + orderRequest.getCategoryId());

        String customerId = Prefs.getString(Constants.FIREBASE_UID, "");
        orderRequest.setCustomerId(customerId);
        orderRequest.setCustomerComment(null);
        orderRequest.setOrderPhotosUris(null);
//        orderRequest.setCategoryId(category.getCategoryId());
//        orderRequest.setCategoryId(category.getCategoryId());
        String key = RefBase
                .refRequests(customerId)
                .push()
//                .child(orderRequest.getOrderId())
                .getKey();
        orderRequest.setOrderId(key);
        orderRequest.setOrderPhotosUris(null);
        RefBase.refRequests()
                //.refRequests(customerId)
                .child(key).setValue(orderRequest)
                .addOnSuccessListener(aVoid -> {
                    OrderPending orderPending = new OrderPending();
                    orderPending.setUserId(orderRequest.getCustomerId());
                    orderPending.setCategoryId(orderRequest.getCategoryId());
                    Log.e(TAG, "saveOrderIntoDatabase: " + orderRequest.getCategoryId());

//                        orderPending.setCategoryId(category.getCategoryId());
                    orderPending.setOrderId(key);
//                        RefBase.requestPending(customerId)
////                                .push()
////                                .child(orderRequest.getOrderId())
//                                .child(key)
//                                .setValue(orderPending)
//                                .addOnSuccessListener(new OnSuccessListener<Void>() {
//                                    @Override
//                                    public void onSuccess(Void aVoid) {
                    spotsDialog.dismiss();

                    addNotificationToControlPanel();

                    Toasty.success(Objects.requireNonNull(getActivity()), context.getResources()
                            .getString(R.string.request_sent_success)).show();
                    Prefs.edit().remove(Constants.ORDER).apply();

                    EventBus.getDefault().post(new MsgEventReloadFragment(true));


                    //hiding the keyboard
                    //after sending request
//
//
//                                    }
//                                })
//                                .addOnFailureListener(new OnFailureListener() {
//                                    @Override
//                                    public void onFailure(@NonNull Exception e) {
//                                        spotsDialog.dismiss();
//                                        Toasty.error(Objects.requireNonNull(getActivity()), Constants.NETWORK_ERROR).show();
//                                    }
//                                });


                })
                .addOnFailureListener(e -> spotsDialog.dismiss());

    }

    //super classes have no public methods with the @Subscribe annotation
    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true, priority = 1)
    public void msgFromLoginToOrder(MsgFromLoginToFragment msg) {
        if (msg != null) {
            Log.e(TAG, "msgFromLoginToOrder: ");
            if (Prefs.contains(Constants.ORDER)) {
                sendOrderToFirebaseDatabase(orderRequest);
//            EventBus.getDefault().removeStickyEvent(msg);
                EventBus.getDefault().removeAllStickyEvents();
            }
        }
    }

    private void uploadImage(Uri filePath) {
        if (filePath != null) {
//            final ProgressDialog progressDialog = new ProgressDialog(getActivity());
//            progressDialog.setCategoryName("Uploading...");
//            progressDialog.show();
            ref = storageReference.child("images/" + UUID.randomUUID().toString());
            ref.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Log.e(TAG, "uploaded: ");
                                    String url = uri.toString();
                                    listLinks.add(url);
                                    if (posUploadImage < uris.size() - 1) {
                                        posUploadImage++;
                                        uploadImage(Uri.parse(uris.get(posUploadImage)));
                                    } else {
                                        orderRequest.setOrderPhotos(listLinks);
                                        saveOrderIntoDatabase();
                                        spotsDialog.dismiss();
                                    }
                                }
                            });

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
//                            progressDialog.dismiss();
                            Toast.makeText(context, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot
                                    .getTotalByteCount());
//                            progressDialog.setBody("Uploaded " + (int) progress + "%");
                        }
                    });
        }
    }

    private void saveOrderToDatabase() {


        AlertDialog spotsDialog = Utils.getInstance().pleaseWait(getActivity());

        spotsDialog.setMessage(context.getString(R.string.send_request));
        spotsDialog.setCancelable(false);
        spotsDialog.setCanceledOnTouchOutside(false);
        spotsDialog.show();


        //            String userId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        String userId = Prefs.getString(Constants.FIREBASE_UID, "");
        orderRequest.setCustomerId(userId);
        RefBase.refRequests(userId)
                .push()
                .setValue(orderRequest)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        OrderPending orderPending = new OrderPending();
                        orderPending.setUserId(orderRequest.getCustomerId());
//                            orderPending.setCategoryId(orderRequest.getCategory().getCategoryId());
                        orderPending.setCategoryId(category.getCategoryId());
                        orderPending.setOrderId(orderRequest.getOrderId());
                        orderRequest.setOrderId(userId);
                        RefBase.requestPending(userId)
                                .push()
                                .setValue(orderPending)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        spotsDialog.dismiss();
                                        Toasty.success(Objects.requireNonNull(getActivity()), context.getResources()
                                                .getString(R.string.request_sent_success)).show();

                                        EventBus.getDefault().post(new MsgEventReloadFragment(true));
                                        Prefs.edit().remove(Constants.ORDER).apply();


                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        spotsDialog.dismiss();
                                    }
                                });


                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        spotsDialog.dismiss();
                    }
                });

    }

    /**
     * inflate data into OrderRequest model
     * call preview data method
     * saving the orderRequest in the shared pref for accessing the fields
     */
    private void confirmAndSedRequest() {
        if (checkData()) return;

        try {

            spotsDialog = Utils.getInstance().pleaseWait(getActivity());
            spotsDialog.setMessage(getActivity().getString(R.string.preview));
            spotsDialog.show();

        } catch (Exception ex) {
            Log.e(TAG, "confirmAndSedRequest: " + ex.getMessage());
        }

        YoYo.with(Techniques.FadeInUp)
                .delay(1000)
                .onStart(new YoYo.AnimatorCallback() {
                    @Override
                    public void call(Animator animator) {
                        tvConfirmRequest.setVisibility(View.VISIBLE);
                    }
                })
                .onEnd(new YoYo.AnimatorCallback() {
                    @Override
                    public void call(Animator animator) {

                        if (spotsDialog != null) {
                            spotsDialog.dismiss();
                        }

                        //tvConfirmRequest.setVisibility(View.VISIBLE);
                        expandAll();

                        //disabling all input fields
                        previewOrderBeforeConfirm();

                        btnBack.setVisibility(View.VISIBLE);

                        btnOrderNow.setText(context.getString(R.string.conf_requ_order));
                        btnOrderNow.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                requestOrder();
                            }
                        });


                        if (!isThereEditOrder() && !Prefs.contains(Constants.ORDER)) {

                            Location location = new Location();
//                        location.setArea(spinnerArea.getItems().get(posSpinnerArea).toString());

                            location.setCountry(spinnerCountry.getItems().get(posSpinnerCountry).toString());

                            location.setCity(spinnerCity.getItems().get(posSpinnerCity).toString());
                            location.setCityId(cityKeySelected);
                            Log.e(TAG, "call: test city id  " + cityKeySelected);
                            location.setCountryId(countryId);
//                      Log.e(TAG, "btnOrderNow: " + location.getArea() + "  " +
//                      location.getCity() + "  " +location.getCountry());
                            Log.e(TAG, "AnimatorCallback: 1 " + location.getCountryId());
                            Log.e(TAG, "AnimatorCallback: 2 " + location.getCityId());
                            Log.e(TAG, "AnimatorCallback  3 " + location.getCity());
                            Log.e(TAG, "AnimatorCallback  4 " + location.getCountry());
                            orderRequest.setOrderDescription(etEnterDesc.getText().toString());
                            orderRequest.setLocation(location);
//                        orderRequest.setLocationId(location);
//                        orderRequest.setCategory(category);
                            orderRequest.setCost("0");
//                        Log.e(TAG, "btnOrderNow:xx " + category);
//                        orderRequest.setCategoryId(category.getCategoryId());
                            orderRequest.setCreationTime(etTimePicker.getText().toString());
                            orderRequest.setCreationDate(etDatePicker.getText().toString());
                            orderRequest.setLocationAddress(etEnterLocation.getText().toString());

//                        ArrayList<Uri> uris = new ArrayList<>();
                            ArrayList<String> uris = new ArrayList<>();
                            for (int key : hmOrderRequestUris.keySet()) {
                                //return the value of each key (uri)
                                uris.add(Uri.parse(hmOrderRequestUris.get(key)).toString());
//                            uris.add(hmOrderRequestUris.get(key));
                            }
                            orderRequest.setOrderPhotosUris(uris);
//                        Log.e(TAG, "call:1 " + orderRequest.getOrderPhotos().size());

//                        orderRequest.setOrderPhotos(uris);
//                        Log.e(TAG, "call:2 " + orderRequest.getOrderPhotosUris().size());


                            //saving the orderRequest in the shared pref for accessing the fields
                            //after reload the fragment
                            //Serializing the order request into string by gson (using orderrequest, type of class)
                            //put request into shared preference
//                        Prefs.edit().putString(Constants.ORDER, gson.toJson(orderRequest, type)).apply();
                            Prefs.edit().putString(Constants.ORDER, gson.toJson(orderRequest, OrderRequest.class)).apply();

                            Log.e(TAG, "AnimatorCallback: 1 " + orderRequest.getLocation().getCityId());
                            Log.e(TAG, "AnimatorCallback: 1 " + orderRequest.getLocation().getCity());
                            Log.e(TAG, "AnimatorCallback: 1 " + orderRequest.getLocation().getCountry());
                            Log.e(TAG, "AnimatorCallback: 1 " + orderRequest.getLocation().getCountryId());

                        } else {

                        }

                    }
                })
                .playOn(tvConfirmRequest);

    }

    private void requestOrder() {
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            if (!Prefs.contains(Constants.FIREBASE_UID)) {
                Toasty.warning(Objects.requireNonNull(getActivity()),
                        context.getString(R.string.please_login))
                        .show();
//            context.finish();
                Intent intent = new Intent(context, CustomerWorkerLoginActivity.class);
                context.startActivity(intent);
//                context.startActivityForResult(intent, Constants.REQUEST_CODE_Login_TO_RequestOrder);

            } else {
                sendOrderToFirebaseDatabase(orderRequest);

            }
        } else {

            if (orderRequest != null) {
                sendOrderToFirebaseDatabase(orderRequest);
            }

        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(
                savedInstanceState);
    }

    /*
     * preview data before confirm
     */
    @SuppressLint("SetTextI18n")
    private void previewOrderBeforeConfirm() {
        View headerSelectCat = expansionLayouts.get(0).getExpansionHeader();
        ((TextView) headerSelectCat.findViewById(R.id.tvTitle)).setText(context.getString(R.string.elected_service));

        LinearLayout bodySelectCat = (LinearLayout) fixedQuestions.get(0).getChildToAdd();
        bodySelectCat.getChildAt(0).setVisibility(View.GONE);
//        bodySelectCat.getChildAt(1).setVisibility(View.GONE);
//        RecyclerView recyclerView = (RecyclerView) bodySelectCat.getChildAt(2);
        //recyclerView.setA(new SelectedCatsAdapter(context,
        //      new ArrayList<>(Collections.singletonList(category))));
        if (category == null) {
            RefBase.refCategory(orderRequest.getCategoryId()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {
                        Category category = dataSnapshot.getValue(Category.class);
                        rvSelectCat.setAdapter(new SelectCatAdapter
                                (new ArrayList<>(Collections.singletonList(category)), null));
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        } else {
            rvSelectCat.setAdapter(new SelectCatAdapter
                    (new ArrayList<>(Collections.singletonList(category)), null));
        }

        //==========================================================

        View headerSelectDate = expansionLayouts.get(1).getExpansionHeader();
        ((TextView) headerSelectDate.findViewById(R.id.tvTitle)).setText(context.getString(R.string.enterd_date));


        LinearLayout bodySelectDate = (LinearLayout) fixedQuestions.get(1).getChildToAdd();
//        bodySelectDate.getChildAt(0).setEnabled(false);
//        bodySelectDate.getChildAt(0).setFocusable(false);
//        bodySelectDate.getChildAt(1).setEnabled(false);
//        bodySelectDate.getChildAt(1).setFocusable(false);

//        etTimePicker.setEnabled(false);
//        etTimePicker.setClickable(false);
//        etTimePicker.setFocusable(false);
//
//        etDatePicker.setEnabled(false);
//        etDatePicker.setClickable(false);
//        etDatePicker.setFocusable(false);

        llTimePicker.setOnClickListener(null);
        llDatePicker.setOnClickListener(null);

        //==============================================
        View headerSelectLoc = expansionLayouts.get(2).getExpansionHeader();
        ((TextView) headerSelectLoc.findViewById(R.id.tvTitle)).setText(context.getString(R.string.selected_location));

//        LinearLayout bodySelectLoc = (LinearLayout) fixedQuestions.get(2).getChildToAdd();
//        for (int i = 0; i < bodySelectLoc.getChildCount(); i++) {
//            bodySelectDate.getChildAt(i).setEnabled(false);
//        }
//        ((MaterialSpinner) bodySelectLoctDate.getChildAt(0)).setSelectedIndex(posSpinnerArea);
//        ((MaterialSpinner) bodySelectDate.getChildAt(1)).setSelectedIndex(posSpinnerCountry);
//        ((MaterialSpinner) bodySelectDate.getChildAt(2)).setSelectedIndex(posSpinnerCity);
        spinnerArea.setSelectedIndex(posSpinnerArea);
        spinnerCountry.setSelectedIndex(posSpinnerCountry);
        spinnerCity.setSelectedIndex(posSpinnerCity);

        spinnerArea.setEnabled(false);
        spinnerCountry.setEnabled(false);
        spinnerCity.setEnabled(false);

        etEnterLocation.setEnabled(false);
        //==============================================

        View headerSelectDesc = expansionLayouts.get(3).getExpansionHeader();
        ((TextView) headerSelectDesc.findViewById(R.id.tvTitle)).setText(context.getString(R.string.desc));

        LinearLayout bodySelectDesc = (LinearLayout) fixedQuestions.get(3).getChildToAdd();

        bodySelectDesc.getChildAt(0).setVisibility(View.GONE);
        bodySelectDesc.getChildAt(1).setVisibility(View.GONE);

        etEnterDesc.setEnabled(false);
        //==============================================

        View headerSelectPhotos = expansionLayouts.get(4).getExpansionHeader();
        ((TextView) headerSelectPhotos.findViewById(R.id.tvTitle)).setText(context.getString(R.string.selected_photos));

        LinearLayout bodySelectPhotos = (LinearLayout) fixedQuestions.get(4).getChildToAdd();

        bodySelectPhotos.getChildAt(0).setVisibility(View.GONE);
        bodySelectPhotos.getChildAt(1).setVisibility(View.GONE);
        //Mohamed 1

//        GridView gridView = ((GridView) bodySelectPhotos.getChildAt(3));
//        gridView.setAdapter(new PhotosPreviewAdapter(context, uris));
//        setGridViewHeightBasedOnChildren(gridView, 3);
        for (Integer key : hmOrderRequestUris.keySet()) {
            Log.e(TAG, "previewOrderBeforeConfirm: " + key);
//            System.out.println("key1.1 : " + key);
//            System.out.println("value1.1 : " + map.get(key));
            uris.add(hmOrderRequestUris.get(key));
        }
        gridViewAttachedPhotos.setAdapter(new AttachPhotosAdapter(context, uris, true));
        //setGridViewHeightBasedOnChildren(gridViewAttachedPhotos, 4);
        if (uris.isEmpty()) {
            headerSelectPhotos.setVisibility(View.GONE);
            bodySelectPhotos.setVisibility(View.GONE);
            expansionLayouts.get(3).getViewSeparator().setVisibility(View.GONE);
        }

        if (uris.isEmpty()) {
            headerSelectPhotos.setVisibility(View.GONE);
            bodySelectPhotos.setVisibility(View.GONE);
            expansionLayouts.get(3).getViewSeparator().setVisibility(View.GONE);
        }


        //==============================================


    }

    private void expandAll() {
        for (int i = 0; i < expansionLayouts.size(); i++) {
            Utils.expand(expansionLayouts.get(i).getExpansionLayout());
            rotateTo90Degree(expansionLayouts.get(i));
            expansionLayouts.get(i).getExpansionHeader().setOnClickListener(null);
        }
    }

    /**
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_services, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
        initStateProgressBar();
//        createExpansionHeadersAndLayouts();
        addDynamicExpansionHeaderWithLayout();
        //unused
        setCustomToolbar();
//        setListenerForExpansions();
        //Prefs.remove(Constants.ORDER);
        fetchPhotoFromCamOrGallery();
        initAlertRate();
        checkIfAnyServiceNeedToRate();
//        initFirebaseRecyclerViewUI();
//        addingWaterMark();
//        removeTheMessageToken();
        //=====================================the question 0  - select cat =========
        accessQuestion1();
        initFirebaseRecyclerViewUI();
        accessExpansionDataViews();
        editOrderIfExist();
    }

    @Override
    public void onAttachFragment(@NonNull Fragment childFragment) {
        super.onAttachFragment(childFragment);
        Log.e(TAG, "rrrrrrrrrrr onAttachFragment : XxX");
    }

    private void initFirebaseRecyclerViewUI() {
        //it's skip auto generated id and get the data that inside it.
        DatabaseReference refAllCats = RefBase.refCategories();
        FirebaseRecyclerOptions<Category> firebaseRecyclerOptions = new FirebaseRecyclerOptions.Builder<Category>()
                .setQuery(refAllCats, Category.class)
                .build();
        fireFetchCategoryAdapter = new FireFetchCategoryAdapter(firebaseRecyclerOptions, this);
//        fireFetchCategoryAdapter.setCategoryId(null);
        //from accessing the question1 view
        rvSelectCat.setAdapter(fireFetchCategoryAdapter);
        fireFetchCategoryAdapter.startListening();

    }

    private void removeTheMessageToken() {
        if (Utils.customerOrWorker()) {
            //Customer
            //Deletating the message token from firebase as FaceBook
            if (Prefs.getPreferences().contains(Constants.FIREBASE_UID)) {
                RefBase.refUser(Prefs.getString(Constants.FIREBASE_UID, ""))
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                dataSnapshot.getRef().removeEventListener(this);
                                if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {
//                                    User user = dataSnapshot.getValue(User.class);
                                    if (dataSnapshot.hasChild(Constants.MESSAGE_TOKEN)) {
                                        dataSnapshot.getRef().child(Constants.MESSAGE_TOKEN).removeValue(new DatabaseReference.CompletionListener() {
                                            @Override
                                            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                                                Toasty.success(getActivity(), "Deleted success", Toasty.LENGTH_SHORT).show();
                                            }
                                        });
                                    }

                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
            }
        } else {

        }
    }

    private void addingWaterMark() {

        String inputText = Objects.requireNonNull(getActivity())
                .getString(R.string.app_name_root);

        WatermarkText watermarkText = new WatermarkText(inputText)
//        WatermarkText watermarkText = new WatermarkText(tvWaterMark)
                .setPositionX(0.5)
                .setPositionY(0.5)
                .setTextColor(Color.LTGRAY)
                .setTextFont(R.font.noto)
                .setTextShadow(0.1f, 5, 5, Color.BLUE)
                .setTextAlpha(150)
                .setRotation(30)
                .setTextSize(20);

        WatermarkBuilder
                .create(context, R.drawable.seyana_logo)
                .loadWatermarkText(watermarkText) // use .loadWatermarkImage(watermarkImage) to load an image.
                .getWatermark()
                .setToImageView(ivWaterMark);

    }

    private void initAlertRate() {


        v = LayoutInflater.from(getActivity()).inflate(R.layout.layout_rate_service, null);


        builder = new AlertDialog.Builder(getActivity())
                .setView(v);
        alertDialog = builder.create();
        alertDialog.setCancelable(false);
        alertDialog.setCanceledOnTouchOutside(false);


        LinearLayout linearLayout = new LinearLayout(getActivity());
        final RatingBar rating = new RatingBar(getActivity());

//        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
//                LinearLayout.LayoutParams.WRAP_CONTENT,
//                LinearLayout.LayoutParams.WRAP_CONTENT
//        );
//        rating.setLayoutParams(lp);
//        rating.setNumStars(4);
//        rating.setStepSize(1);
//        //add ratingBar to linearLayout
//        linearLayout.addView(rating);
//        builder.setIcon(android.R.drawable.btn_star_big_on);
//        builder.setTitle("Add Rating: ");
//        //add linearLayout to dailog
//        builder.setView(linearLayout);


        progressBar = v.findViewById(R.id.progress);
//        RatingBar ratingBar = v.findViewById(R.id.rb_customer_order_rate);
        ratingBar = v.findViewById(R.id.rb_customer_order_rate);
        circularImageView = v.findViewById(R.id.ivUserPhoto);
        textInputEditText = v.findViewById(R.id.etEnterFeedback);

        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {

            }
        });

    }

    private void checkIfAnyServiceNeedToRate() {
        userId = Prefs.getString(Constants.FIREBASE_UID, "");
        Log.e(TAG, "checkIfAnyServiceNeedToRate: " + userId);
        if (userId != null) {

//            observeOnCmTasks(alertDialog, progressBar, circularImageView, Constants.FREELANCE_FINISHED);
//            observeOnCmTasks(alertDialog, progressBar, circularImageView, Constants.CM_FINISHED);


            valueEventListener =
                    observeOnRequests(alertDialog, progressBar, circularImageView, Constants.FREELANCE_FINISHED);
//            pos++;
//            valueEventListener1 =
//                    observeOnRequests(alertDialog, progressBar, circularImageView, Constants.CM_FINISHED);


        }


    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.e(TAG, "rrrrrrrrrrr onDetach : XxX");
//        Prefs.edit().remove(Constants.ORDER).apply();

//        reference1.removeEventListener(valueEventListener);
//        reference2.removeEventListener(valueEventListener1);

        orderRequest = null;

    }

    private void observeOnCmTasks(AlertDialog alertDialog, View progressBar, CircularImageView circularImageView, String state) {
        RefBase.refCmtTasks()
                .orderByChild(Constants.TYPE)//filter by orders state
//                .equalTo(Constants.CM_FINISHED)//one object
                .equalTo(state)//one object
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            Log.e(TAG, "11111:");
//                            for (DataSnapshot dataSnapshot1 :
//                                    dataSnapshot.getChildren()) {
//                                cmTask = dataSnapshot1.getValue(CmTask.class);
                            cmTask = dataSnapshot.getChildren().iterator().next().getValue(CmTask.class);
                            Log.e(TAG, "oooooooooo: " + cmTask.getCustomerId() + "    " + userId);
                            if (TextUtils.equals(cmTask.getCustomerId(), userId)) {
                                //second filter by user id
                                Log.e(TAG, "oooooooooo: " + userId);//one ob  ject
                                rateToSubmit.setRequestId(cmTask.getRequestId());
                                String rateStr = null;
                                rateStr = String.valueOf(cmTask.getRate());
                                Log.e(TAG, "iiiiiiiii: " + rateStr);
                                if (rateStr.length() == 1) {
                                    if (!alertDialog.isShowing()) {
                                        alertDialog.show();
//                                            dataSnapshot1.getRef().removeEventListener(this);
                                        if (v != null)
                                            v.findViewById(R.id.btnSubmitRate).setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    if (textInputEditText.getText().length() == 0) {
                                                        Toasty.warning(getActivity(), getActivity().getString(R.string.enter_feed)).show();
                                                        return;
                                                    }
                                                    if (ratingBar.getNumStars() == 0) {
                                                        Toasty.warning(getActivity(), getActivity().getString(R.string.set_star)).show();
                                                        return;
                                                    }
                                                    rateToSubmit.setMessage((textInputEditText.getText().toString()));
//                rate.setNumberOfStars(ratingBar.getNumStars() + "");
                                                    rateToSubmit.setNumberOfStars(ratingBar.getRating() + "");
//                rate.setCustomerId(orderRequestRate.getCustomerId());
                                                    rateToSubmit.setCustomerId(userId);
                                                    String key = RefBase.rate()
                                                            .push().getKey();
                                                    RefBase.rate()
                                                            .child(key)
                                                            .setValue(rateToSubmit)
                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void aVoid) {
                                                                    Toasty.warning(getActivity(),
                                                                            getActivity().getString(R.string.sent_success)).show();
                                                                    alertDialog.dismiss();

                                                                    RefBase.refCmtTasks()
                                                                            .orderByChild(Constants.TYPE)//filter by orders state
                                                                            .equalTo(state)//one object
                                                                            .addValueEventListener(new ValueEventListener() {
                                                                                @Override
                                                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                                    if (dataSnapshot.exists()) {
                                                                                        dataSnapshot.getRef().removeEventListener(this);
                                                                                        Log.e(TAG, "11111:");
                                                                                        for (DataSnapshot dataSnapshot1 :
                                                                                                dataSnapshot.getChildren()) {
                                                                                            cmTask = dataSnapshot1.getValue(CmTask.class);
                                                                                            cmTask.setRate(key);
                                                                                            dataSnapshot1.getRef().setValue(cmTask);

                                                                                        }
                                                                                    }
                                                                                }

                                                                                @Override
                                                                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                                                                }
                                                                            });

                                                                }
                                                            });


                                                }
                                            });

                                        RefBase.refWorker(cmTask.getCmId())
                                                .addValueEventListener(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                                        if (dataSnapshot.exists()) {
                                                            CMWorker user = dataSnapshot.getValue(CMWorker.class);
                                                            Picasso.get().load(user.getWorkerPhoto())
                                                                    .into(circularImageView, new Callback() {
                                                                        @Override
                                                                        public void onSuccess() {
                                                                            progressBar.setVisibility(View.GONE);
                                                                        }

                                                                        @Override
                                                                        public void onError(Exception e) {
                                                                            progressBar.setVisibility(View.GONE);
                                                                        }
                                                                    });

                                                        }
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                                    }
                                                });

                                    }
//                                    }
                                }

                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private ValueEventListener observeOnRequests(AlertDialog alertDialog, View progressBar, CircularImageView circularImageView, String state) {
        alerRateShown = true;
        Prefs.remove(Constants.FLAG_RATE);
        return RefBase.refRequests()
                .orderByChild(Constants.ORDER_STATE)//filter by orders state
//                .equalTo(Constants.CM_FINISHED)//one object
                .equalTo(state)
                .addValueEventListener(new ValueEventListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                        dataSnapshot.getRef().removeEventListener(this);
                        if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {
                            Log.e(TAG, "rrrrrrrrrr:");
                            for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
//                            if (true) {
//                                DataSnapshot dataSnapshot1 = dataSnapshot.getChildren().iterator().next();

                                OrderRequest orderRequest = dataSnapshot1.getValue(OrderRequest.class);
                                Log.e(TAG, "55555555: " + orderRequest.getCustomerId());
                                Log.e(TAG, "55555555: " + userId);
                                Log.e(TAG, "55555555: " + orderRequest.getRate().length());
                                Log.e(TAG, "55555555: " + orderRequest.getState());


                                if (orderRequest != null) {
                                    if (TextUtils.equals(orderRequest.getCustomerId(), userId) && orderRequest.getRate().length() == 1) {
//                                        dataSnapshot1.getRef().removeEventListener(this);
                                        Log.e(TAG, "wwwtttttt: ");
                                        if (!alertDialog.isShowing() && !Prefs.contains(Constants.FLAG_RATE)) {
                                            Log.e(TAG, "6756ghgfh: ");
//                                            alerRateShown = false;

                                            Prefs.edit().putString(Constants.FLAG_RATE, "").apply();
                                            if (getActivity() != null && !getActivity().isFinishing()) {
                                                alertDialog.show();

                                                if (TextUtils.equals(state, Constants.CM_FINISHED)) {
                                                    if (v != null) {
                                                        v.findViewById(R.id.userPhoto).setVisibility(View.GONE);
                                                        v.findViewById(R.id.ll_for_company_service_rate).setVisibility(View.VISIBLE);

                                                        TextView tv_creation_date_rate = v.findViewById(R.id.tv_creation_date_rate);
                                                        TextView tv_category_rate = v.findViewById(R.id.tv_category_rate);
                                                        TextView tv_country_and_city_rate = v.findViewById(R.id.tv_country_and_city_rate);
                                                        TextView tv_desc = v.findViewById(R.id.tv_desc);


                                                        RefBase.refCategories()
                                                                .child(orderRequest.getCategoryId())
                                                                .addValueEventListener(new ValueEventListener() {
                                                                    @Override
                                                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                        if (dataSnapshot.exists()) {
                                                                            HashMap<String, Object> map = (HashMap<String, Object>) dataSnapshot.getValue();
                                                                            if (map != null && map.get(Constants.CAT_NAME) != null) {
                                                                                String catName = (String) map.get(Constants.CAT_NAME);
                                                                                tv_category_rate.setText(catName);

                                                                                if (tv_category_rate.getText().length() == 0) {
                                                                                    ((LinearLayout) tv_category_rate.getParent()).setVisibility(View.GONE);
                                                                                }

                                                                            }
                                                                        }
                                                                    }

                                                                    @Override
                                                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                                                    }
                                                                });
                                                        tv_creation_date_rate.setText(orderRequest.getCreationDate());
                                                        tv_country_and_city_rate.setText(orderRequest.getLocation().getCountry() + "/" + orderRequest.getLocation().getCity());
                                                        tv_desc.setText(orderRequest.getOrderDescription());


                                                        if (tv_creation_date_rate.getText().length() == 0) {
                                                            ((ViewGroup) tv_creation_date_rate.getParent()).setVisibility(View.GONE);
                                                        }
                                                        if (tv_country_and_city_rate.getText().length() == 0) {
                                                            ((ViewGroup) tv_country_and_city_rate.getParent()).setVisibility(View.GONE);
                                                        }
                                                        if (tv_desc.getText().length() == 0) {
                                                            ((ViewGroup) tv_desc.getParent()).setVisibility(View.GONE);
                                                        }
                                                    }

                                                } else {
                                                    if (v != null) {
                                                        v.findViewById(R.id.userPhoto).setVisibility(View.VISIBLE);
                                                        v.findViewById(R.id.ll_for_company_service_rate).setVisibility(View.GONE);
                                                        CircularImageView ivUserPhoto = v.findViewById(R.id.ivUserPhoto);
                                                        RefBase.refFreelancersConnection()
                                                                .orderByChild(Constants.REQUEST_ID)
                                                                .equalTo(orderRequest.getOrderId())
                                                                .addValueEventListener(new ValueEventListener() {
                                                                    @Override
                                                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                        dataSnapshot.getRef().removeEventListener(this);
                                                                        if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {
                                                                            for (DataSnapshot dataSnapshot13 : dataSnapshot.getChildren()) {
                                                                                ConnectionFree connectionFree = dataSnapshot13.getValue(ConnectionFree.class);
                                                                                if (connectionFree != null) {
                                                                                    Log.e(TAG, "onDataChange: " + connectionFree.getFreelancerId());
                                                                                    RefBase.refWorker(connectionFree.getFreelancerId())
                                                                                            .addValueEventListener(new ValueEventListener() {
                                                                                                @Override
                                                                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                                                    dataSnapshot.getRef().removeEventListener(this);
                                                                                                    if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {
                                                                                                        CMWorker cmWorker = dataSnapshot.getValue(CMWorker.class);
                                                                                                        if (cmWorker != null) {
                                                                                                            Picasso.get().load(cmWorker.getWorkerPhoto())
                                                                                                                    .into(ivUserPhoto, new Callback() {
                                                                                                                        @Override
                                                                                                                        public void onSuccess() {
//                                                                                                                v.findViewById(R.id.progress).setVisibility(View.VISIBLE);
//                                                                                                                v.findViewById(R.id.progress).setVisibility(View.VISIBLE);
                                                                                                                        }

                                                                                                                        @Override
                                                                                                                        public void onError(Exception e) {
                                                                                                                            //                                                                                                                v.findViewById(R.id.progress).setVisibility(View.GONE);
                                                                                                                        }
                                                                                                                    });
                                                                                                        }
                                                                                                    }

                                                                                                }

                                                                                                @Override
                                                                                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                                                                                }
                                                                                            });
                                                                                }
                                                                            }
                                                                        }
                                                                    }

                                                                    @Override
                                                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                                                    }
                                                                });

                                                    }

                                                }

//                                            if (v != null) {
                                                v.findViewById(R.id.btnSubmitRate).setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        if (textInputEditText.getText().length() == 0) {
                                                            Toasty.warning(getActivity(), getString(R.string.enter_feed)).show();
                                                            return;
                                                        }
                                                        if (ratingBar.getNumStars() == 0) {
                                                            Toasty.warning(getActivity(), getString(R.string.set_star)).show();
                                                            return;
                                                        }
                                                        rateToSubmit.setMessage((textInputEditText.getText().toString()));
//                rate.setNumberOfStars(ratingBar.getNumStars() + "");
                                                        rateToSubmit.setNumberOfStars(ratingBar.getRating() + "");
//                rate.setCustomerId(orderRequestRate.getCustomerId());
                                                        rateToSubmit.setCustomerId(userId);
                                                        String key = RefBase.rate()
                                                                .push().getKey();
                                                        if (key != null) {
                                                            RefBase.rate()
                                                                    .child(key)
                                                                    .setValue(rateToSubmit)
                                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                        @Override
                                                                        public void onSuccess(Void aVoid) {
//                                                                        Toasty.warning(Objects.requireNonNull(getActivity()),
//                                                                                getActivity().getString(R.string.sent_success)).show();
                                                                            alertDialog.dismiss();
                                                                            RefBase.refRequests()
                                                                                    .orderByChild(Constants.ORDER_STATE)//filter by orders state
                                                                                    //                                        .equalTo(Constants.CM_FINISHED)//one object
                                                                                    .equalTo(state)//one object
                                                                                    .addValueEventListener(new ValueEventListener() {
                                                                                        @Override
                                                                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                                            dataSnapshot.getRef().removeEventListener(this);
                                                                                            if (dataSnapshot.exists()) {
                                                                                                Log.e(TAG, "werwerewr:");
                                                                                                for (DataSnapshot dataSnapshot1 :
                                                                                                        dataSnapshot.getChildren()) {
                                                                                                    //                                                                                                            OrderRequest orderRequest = dataSnapshot1.getValue(OrderRequest.class);
                                                                                                    OrderRequest orderRequest = dataSnapshot1.getValue(OrderRequest.class);
                                                                                                    if (orderRequest != null) {
                                                                                                        if (TextUtils.equals(orderRequest.getCustomerId(), userId) &&
                                                                                                                orderRequest.getRate().length() == 1) {
                                                                                                            HashMap<String, Object> map = new HashMap<>();
                                                                                                            map.put(Constants.RATE, key);
                                                                                                            dataSnapshot1.getRef().updateChildren(map);
//                                                                                                        alerRateShown = true;

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
                                                                    });
                                                        }


                                                    }
                                                });
//                                            }
                                            }
                                        }


//                                        RefBase.rate()
//                                                .orderByChild(Constants.REQUEST_ID)
//                                                .equalTo(orderRequest.getOrderId())
//                                                .addValueEventListener(new ValueEventListener() {
//                                                    @Override
//                                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                                                        if (!dataSnapshot.exists()) {
//                                                        }
//                                                    }
//
//                                                    @Override
//                                                    public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                                                    }
//                                                });
                                        Log.e(TAG, "2222222:");
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

    private boolean checkData() {
        //select cat
//        if (category == null) {
        if (orderRequest.getCategoryId() == null && TextUtils.isEmpty(orderRequest.getCategoryId())) {
            if (expansionLayouts.get(0).getExpansionLayout().getVisibility() != View.VISIBLE) {
                rotateTo90Degree(expansionLayouts.get(0));
                collapseAll(expansionLayouts.get(0));
                Utils.expand(expansionLayouts.get(0).getExpansionLayout());
            }
            Toasty.warning(context, context.getString(R.string.select_one_cat)).show();
            return true;
        }

        //select date
        if (etDatePicker.getText().toString().trim().length() == 0) {
            if (expansionLayouts.get(1).getExpansionLayout().getVisibility() != View.VISIBLE) {
                rotateTo90Degree(expansionLayouts.get(1));
                collapseAll(expansionLayouts.get(1));
                Utils.expand(expansionLayouts.get(1).getExpansionLayout());
            }
            Toasty.warning(context, context.getString(R.string.select_date)).show();
            return true;
        }

        ///select location
        if (etTimePicker.getText().toString().trim().length() == 0) {
            if (expansionLayouts.get(1).getExpansionLayout().getVisibility() != View.VISIBLE) {
                rotateTo90Degree(expansionLayouts.get(1));
                collapseAll(expansionLayouts.get(1));
                Utils.expand(expansionLayouts.get(1).getExpansionLayout());
            }
            Toasty.warning(context, context.getString(R.string.select_time)).show();
            return true;
        }

        if (etEnterLocation.getText().toString().trim().length() == 0
//                || posSpinnerArea == 0
                || posSpinnerCity == 0
                || posSpinnerCountry == 0
        ) {
            if (expansionLayouts.get(2).getExpansionLayout().getVisibility() != View.VISIBLE) {
                rotateTo90Degree(expansionLayouts.get(2));
                collapseAll(expansionLayouts.get(2));
                Utils.expand(expansionLayouts.get(2).getExpansionLayout());
            }
            Toasty.warning(context, context.getString(R.string.select_loc)).show();
            return true;
        }
        //Add desc
        if (etEnterDesc.getText().toString().trim().length() == 0) {
            if (expansionLayouts.get(3).getExpansionLayout().getVisibility() != View.VISIBLE) {
                rotateTo90Degree(expansionLayouts.get(3));
                collapseAll(expansionLayouts.get(3));
                Utils.expand(expansionLayouts.get(3).getExpansionLayout());
            }
            Toasty.warning(context, context.getString(R.string.add_clear_desc)).show();
            return true;
        }
        return false;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);


    }

    private void initStateProgressBar() {
//        stateProgressBar.setStateDescriptionData(descriptionData);
//        stateProgressBar.setStateDescriptionData(new ArrayList<>());
    }

    private void setCustomToolbar() {
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar()
                .setTitle(getString(R.string.app_name_root));
    }

    private void addDynamicExpansionHeaderWithLayout() {
        //clear at first
        expansionLayouts.clear();


//        RotateAnimation rotate80 = new RotateAnimation(0, 80
//                , Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0);
//        rotate80.setDuration(1000);
////        rotate80.setInterpolator(new LinearInterpolator());
//
//        RotateAnimation rotate0 = new RotateAnimation(80, 0
//                , Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
//        rotate0.setDuration(2000);
//        rotate0.setInterpolator(new LinearInterpolator());

        //mapping the fixedQuestions list to create ExpansionViewGroup
        //for header
        for (int i = 0; i < fixedQuestions.size(); i++) {

            ExpansionData expansionData = fixedQuestions.get(i);
            ExpansionHeaderWithLayout layout = new ExpansionHeaderWithLayout();
            layout.getExpansionLayout().setVisibility(View.GONE);//for hiding all

            layout.getTvTitle().setText(expansionData.title);
            layout.getIvIcon().setImageResource(expansionData.imageIcon);
            layout.getMyViewContainer().addView(expansionData.getChildToAdd());

            expansionLayouts.add(layout);

//            expansionsViewGroupLinearLayout.addView(layout.getRoot());
            questionsContainer.addView(layout.getRoot());

        }

        //for hiding the view (Line) separator at final ExpansionLayout
        for (int i = 0; i < expansionLayouts.size(); i++) {
            ExpansionHeaderWithLayout layout = expansionLayouts.get(i);
            if (i == 0) {
//                layout.getExpansionLayout().findViewById(R.id.btnBack).setVisibility(View.INVISIBLE);
            }
            if (i == expansionLayouts.size() - 1) {
//                layout.getExpansionLayout().findViewById(R.id.btnNext).setVisibility(View.INVISIBLE);
                layout.getRoot().findViewById(R.id.viewSeparator).setVisibility(View.INVISIBLE);
            }
//            layout.getExpansionHeader().setToggleOnClick(false);

//            if (i == 0) {
            //for expand first view
//                layout.getExpansionLayout().setVisibility(View.VISIBLE);//for show the first item onlt
//                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        Utils.expand(layout.getExpansionLayout());
//                        rotateTo90Degree(layout);
//                    }
//                }, 1200);

//            }
            //clicked listener for header
            layout.getExpansionHeader().setOnClickListener(new OnClickListener(layout));
        }


        ExpansionHeaderWithLayout layout = expansionLayouts.get(0);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Utils.expand(layout.getExpansionLayout());
                rotateTo90Degree(layout);
            }
        }, 1200);

    }

    private void collapseAll2() {

        for (int i = 0; i < expansionLayouts.size(); i++) {
            ExpansionHeaderWithLayout layout1 = expansionLayouts.get(i);
            if (layout1.getExpansionLayout().getVisibility() == View.VISIBLE) {
//                if (i == 0) {
//                    layout1.getExpansionLayout().setVisibility(View.GONE);
//                } else {
//                    Utils.collapse(layout1.getExpansionLayout());
//                }
                if (i > 0) {
                    Utils.collapse(layout1.getExpansionLayout(),
                            i,
                            expansionLayouts.size() - 1,
                            scrollView);
                    rotateTo0Degree(layout1);
                }
            } else {
                Log.e(TAG, "collapseAll: 111 ");
            }

//            if (layout1.getExpansionLayout().getVisibility() == View.VISIBLE) {
//                Utils.collapse(layout1.getExpansionLayout());
//                rotateTo0Degree(layout1);
//            }
        }


//        for (ExpansionHeaderWithLayout layout1 : expansionLayouts) {
//            if (layout1.getExpansionLayout().getVisibility() == View.VISIBLE) {
//                Utils.collapse(layout1.getExpansionLayout());
//                rotateTo0Degree(layout1);
//            }
//        }
//


    }

    private void collapseAll(ExpansionHeaderWithLayout layout) {
        for (int i = 0; i < expansionLayouts.size(); i++) {
            ExpansionHeaderWithLayout layout1 = expansionLayouts.get(i);
            if (layout1.getExpansionLayout().getVisibility() == View.VISIBLE) {
//                if (i == 0) {
//                    layout1.getExpansionLayout().setVisibility(View.GONE);
//                } else {
//                    Utils.collapse(layout1.getExpansionLayout());
//                }
                if (i > 0) {
                    Utils.collapse(layout1.getExpansionLayout(),
                            i,
                            expansionLayouts.size() - 1,
                            scrollView);
                    rotateTo0Degree(layout1);
                }
            } else {
                Log.e(TAG, "collapseAll: 111 ");
            }
        }

//        for (ExpansionHeaderWithLayout layout1 : expansionLayouts) {
//            if (layout1.getExpansionLayout().getVisibility() == View.VISIBLE) {
//                Utils.collapse(layout1.getExpansionLayout());
//                rotateTo0Degree(layout);
//            }
//            else {
//                Log.e(TAG, "collapseAll: 111 " );
//            }
//        }


    }

    private void rotateTo0Degree(ExpansionHeaderWithLayout layout) {
        layout.getHeaderIndicator().animate()
                .rotationBy(0f)
                .withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        layout.getHeaderIndicator().setRotation(0f);
                        Log.e(TAG, "run: 120 ");
                    }
                })
                .setDuration(dur)
                .setInterpolator(new LinearInterpolator())
                .start();
    }

    private void rotateTo90Degree(ExpansionHeaderWithLayout layout) {
        layout.getHeaderIndicator().animate()
                .rotationBy(90f)
                .withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        layout.getHeaderIndicator().setRotation(90f);
                    }
                })
                .setDuration(dur)
                .setInterpolator(new LinearInterpolator())
                .start();
    }

    private View createViewSeparator() {
        return LayoutInflater.from(getActivity()).inflate(R.layout.view_separator, null);
    }

    @SuppressLint("InflateParams")
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(TAG, "onCreate: ");
        context = getActivity();
//        context = FragmentRequestOrder.this;

        //fill Questions
        fixedQuestions = Arrays.asList(
                new ExpansionData(R.drawable.ic_select_service, context.getString(R.string.select_service),
                        LayoutInflater.from(getActivity()).inflate(R.layout.layout_select_cat, null)),

                new ExpansionData(R.drawable.ic_date, context.getString(R.string.select_date),
                        LayoutInflater.from(getActivity()).inflate(R.layout.layout_select_date, null)),

                new ExpansionData(R.drawable.ic_location, context.getString(R.string.set_location),
                        LayoutInflater.from(getActivity()).inflate(R.layout.layout_select_location, null)),


                new ExpansionData(R.drawable.ic_description, context.getString(R.string.set_desc),
                        LayoutInflater.from(getActivity()).inflate(R.layout.layout_set_description, null)),


                new ExpansionData(R.drawable.ic_attach,
                        context.getString(R.string.attach_photos),
                        LayoutInflater.from(getActivity()).inflate(R.layout.layout_select_photos, null))


        );
    }

    @SuppressLint("CheckResult")
    private void uploadPhotoEditPending(Uri filePath, int posUploadPhoto) {

//        if (TextUtils.equals(listMo3adalaEditPending.get(posUploadImage).getMode(), Constants.Modes.FROM_USER_DEVICE) &&
//                posUploadImage < listMo3adalaEditPending.size()) {
//
//        } else {
//            posUploadImage++;
//            uploadPhotoEditPending(Uri.parse(listMo3adalaEditPending.get(posUploadImage).getPath()), posUploadImage);
//            return;
//        }


        if (filePath != null) {
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
                                    ref = storageReference.child("images/" + UUID.randomUUID().toString());
                                ref.putBytes(inputData[0])
                                        .addOnSuccessListener(taskSnapshot -> {
//                                                progressDialog.dismiss();
                                            ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                @Override
                                                public void onSuccess(Uri uri) {
                                                    Log.e(TAG, "uploaded:2 ");
                                                    String url = uri.toString();
                                                    listLinks.add(url);
                                                    if (posUploadImage < lisFromUserDev.size() - 1) {
                                                        posUploadImage++;
                                                        uploadPhotoEditPending(Uri.parse(lisFromUserDev.get(posUploadImage).getPath()), posUploadImage);
                                                    } else {
                                                        submitDataSetChanges();
                                                    }
                                                }
                                            }).addOnFailureListener(e -> {

                                            });
                                        })
                                        .addOnFailureListener(e -> {
                                            progressDialog.dismiss();
//                                            Toast.makeText(context, "Failed " + e.getBody(), Toast.LENGTH_SHORT).show();
                                            Toasty.error(Objects.requireNonNull(getActivity()), Constants.NETWORK_ERROR).show();
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
                        }
                    });


        }

    }

    private void submitDataSetChanges() {


        for (int i = 0; i < listMo3adalaEditPending.size(); i++) {
            if (TextUtils.equals(listMo3adalaEditPending.get(i).getMode(), Constants.Modes.FROM_ABOVE)) {
                listLinks.add(listMo3adalaEditPending.get(i).getPath());
            }
        }

        orderRequest.setOrderPhotos(listLinks);
        orderRequest.setOrderPhotosUris(null);
        orderRequest.setLocationAddress(etEnterLocation.getText().toString());
        orderRequest.setOrderDescription(etEnterDesc.getText().toString());
        orderRequest.setCustomerComment(null);

        Log.e(TAG, "submitDataSetChanges:1 " + etEnterLocation.getText().toString());
        Log.e(TAG, "submitDataSetChanges:2 " + etEnterDesc.getText().toString());

        RefBase.refRequests(orderRequest.getOrderId())
                .setValue(orderRequest).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(context, R.string.edited_success, Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
                getActivity().recreate();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "onFailure: " + e.getMessage());
            }
        });
    }

    @SuppressLint("CheckResult")
    private void uploadPhoto(Uri filePath, int posUploadPhoto) {
        if (filePath != null) {
//            progressDialog = new ProgressDialog(context);
//            progressDialog.setCategoryName("Uploading...");
//            progressDialog.setCancelable(false);
//            progressDialog.setCanceledOnTouchOutside(false);
//            progressDialog.show();


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
                                    ref = storageReference.child("images/" + UUID.randomUUID().toString());
                                ref.putBytes(inputData[0])
                                        .addOnSuccessListener(taskSnapshot -> {
//                                                progressDialog.dismiss();
                                            ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                @Override
                                                public void onSuccess(Uri uri) {
                                                    Log.e(TAG, "uploaded:1 ");
                                                    String url = uri.toString();
                                                    Log.e(TAG, "uploaded:2 " + uris.size());

                                                    listLinks.add(url);
                                                    if (posUploadImage < uris.size() - 1) {
//                                                    if (posUploadImage < uris.size()) {
                                                        posUploadImage++;
                                                        Log.e(TAG, "uploaded:1.1 " + url + " and " + posUploadImage);
                                                        uploadPhoto(Uri.parse(uris.get(posUploadImage)), posUploadImage);
                                                    } else {
                                                        Log.e(TAG, "uploaded:1.2 " + url);
                                                        orderRequest.setOrderPhotos(listLinks);
                                                        spotsDialog.dismiss();
                                                        if (isThereEditOrder()) {
                                                            //update EXIST
                                                            Log.e(TAG, "onSuccess: Update excite images ");
                                                        } else {
                                                            Log.e(TAG, "isThereEditOrder: No edit order");
//                                                            orderRequest
                                                            //insert new
                                                            saveOrderIntoDatabase();
                                                        }

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
                        }
                    });


        } else {
            Log.e(TAG, "uploadPhoto:file is null  ");
        }

    }

    /**
     * handle:
     * case1: when order is cancel by user before he confirm it
     * case2: when edit order
     */

    private void editOrderIfExist() { //cashed order
        if (Prefs.contains(Constants.ORDER)) {//cashed order case
            Log.e(TAG, "editOrderIfExist: cashed");

            //deserializable: get model form gson then call it from shared pref

//            Log.e(TAG, "editOrderIfExist:before 1 " + orderRequest.getOrderPhotosUris().size());
//            Log.e(TAG, "editOrderIfExist:before1 2 " + orderRequest.getOrderPhotos().size());
            orderRequest = gson.fromJson(Prefs.getString(Constants.ORDER, ""), type);

            //photos
            refreshCashedOrderMode();

            //set image size for grid view (num of column)
            //wrapping image view according to children height.

            setGridViewHeightBasedOnChildren(gridViewAttachedPhotos, 4);

            //filling the hashmap above from prev saved order request
            fillingHmWithPrevOrderRequest();

//            ViewGroup group = (ViewGroup) gridViewAttachedPhotos.getChildAt(position);
//            Log.e(TAG, "setUriToGridItem:1 " + group + ", " + position);
//            ImageView ivPhoto = group.findViewById(R.id.ivPhoto);
//            Log.e(TAG, "setUriToGridItem:2 " + ivPhoto);
//            group.findViewById(R.id.ivClose).setVisibility(View.VISIBLE);
//            group.findViewById(R.id.ivClose).setTag(Integer.valueOf(hmOrderRequestUris.size()));
//            group.findViewById(R.id.ivPhoto2).setVisibility(View.GONE);
//            group.findViewById(R.id.ivPhoto).setVisibility(View.VISIBLE);
//            ivPhoto.setImageURI(uri);
//            fireFetchCategoryAdapter.setEditMode(EDIT_CASHED_ORDER);

        } else if (isThereEditOrder()) { //edit order
            Log.e(TAG, "editOrderIfExist: isThereEditOrder");
            orderRequest = (OrderRequest) getArguments().getSerializable(Constants.EDIT_ORDER_);
            btnBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Prefs.edit().remove(Constants.ORDER).apply();
                    //recreate fragment
                    getActivity().recreate();
                }
            });

            btnBack.setVisibility(View.VISIBLE);
            btnOrderNow.setText(getString(R.string.edit_order));
            btnOrderNow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.e(TAG, "onClick:1 " + orderRequest.getOrderId());
//                    Toast.makeText(context, "Edit lolo", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "onClick:2 " + orderRequest.getOrderPhotos().size());
                    //using firebase
//                    uploadPhoto(Uri.parse(hmModeEdition.get(posUploadImage)), posUploadImage);
                    for (int i = 0; i < listMo3adalaEditPending.size(); i++) {
                        Log.e(TAG, "onClick:3 " + listMo3adalaEditPending.get(i).getMode());

                        if (TextUtils.equals(listMo3adalaEditPending.get(i).getMode(), Constants.Modes.FROM_USER_DEVICE)) {
                            lisFromUserDev.add(listMo3adalaEditPending.get(i));
                            Log.e(TAG, "onClick:4 " + listMo3adalaEditPending.get(i).getPath() + " F u 7agazy " + listMo3adalaEditPending.get(i).getMode());
                        }
                    }

                    progressDialog = new ProgressDialog(getActivity());
                    progressDialog.setTitle(getString(R.string.editing_pending_order));
                    progressDialog.setCancelable(false);
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.show();


                    Log.e(TAG, "onClick: pos " + posUploadImage + " list " + lisFromUserDev.size());


                    if (lisFromUserDev.isEmpty()) {
                        submitDataSetChanges();
                    } else {
                        uploadPhotoEditPending(Uri.parse(lisFromUserDev.get(posUploadImage).getPath()), posUploadImage);
                    }

                }
            });


            ArrayList<ModeEdit> list = new ArrayList<>();
            for (String path : orderRequest.getOrderPhotos()) {
                list.add(new ModeEdit(path, Constants.Modes.FROM_ABOVE));
            }
//
//
//        List<String> listMo3adalah = createListMo3adalah(orderRequest.getOrderPhotos());
//        List<ModeEdit> listMo3adalah = createListMo3adalahEditMode(list);
            listMo3adalaEditPending = createListMo3adalahEditMode(list);


//            AttachPhotosAdapter adapter = new AttachPhotosAdapter(context, listMo3adalah, true);
            AttachPhotosAdapter adapter = new AttachPhotosAdapter(context,
                    //orderRequest.getOrderPhotosUris(),
                    listMo3adalaEditPending,
                    true,
                    this,
                    this);
            gridViewAttachedPhotos.setAdapter(adapter);
//            refreshPendingOrderMode();
            setGridViewHeightBasedOnChildren(gridViewAttachedPhotos, 4);

            //filling the hashMap above from prev saved order request
            fillingHmWithPrevOrderRequest();

        } else {

        }

        //no edit or cashed
        if (orderRequest == null) {
            orderRequest = new OrderRequest();
            refreshGridAttachPhotosNormalMode();
            Log.e(TAG, "editOrderIfExist: XxX");
            return;
        }


//        Log.e(TAG, "editOrderIfExist: test cashed order 1 " + orderRequest.getLocation().getCity());

//        orderRequest.setCategoryId(order);

        Log.e(TAG, "editOrderIfExist: before 201 " + fireFetchCategoryAdapter.getCategoryId());
//        initFirebaseRecyclerViewUI();
        fireFetchCategoryAdapter.setCategoryId(orderRequest.getCategoryId());
//        fireFetchCategoryAdapter.setCategoryId("-LtyL3ZaZhhWnsE3KGYE");
//        Prefs.edit().putString(Constants.ORDER_ID_PENDING_ORDER, orderRequest.getCategoryId()).apply();
//        fireFetchCategoryAdapter.setCategoryId("");
        fireFetchCategoryAdapter.startListening();
        //selected category
        Log.e(TAG, "editOrderIfExist: 201 " + orderRequest.getCategoryId());
        Log.e(TAG, "editOrderIfExist: after 201 " + fireFetchCategoryAdapter.getCategoryId());


        //==============================================
        loadAllCountries();
//        spinnerCity.setVisibility(MaterialSpinner.VISIBLE);
        if (orderRequest.getLocation() != null) {
            countryId = orderRequest.getLocation().getCountryId();
            Log.e(TAG, "editOrderIfExist: " + orderRequest.getLocation().getCityId());
            Log.e(TAG, "editOrderIfExist: " + orderRequest.getLocation().getCountryId());
            loadAllCities();
        }

        //======================================================
        etTimePicker.setText(orderRequest.getCreationTime());
        etDatePicker.setText(orderRequest.getCreationDate());
        //======================================================
        etEnterDesc.setText(orderRequest.getOrderDescription());
        etEnterLocation.setText(orderRequest.getLocationAddress());

//        AttachPhotosAdapter adapter = new AttachPhotosAdapter(context, this, this);
//        gridViewAttachedPhotos.setAdapter(adapter);
//        setGridViewHeightBasedOnChildren(gridViewAttachedPhotos, 4);

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

    private void refreshPendingOrderMode() {
//
//        ArrayList<ModeEdit> list = new ArrayList<>();
//        for (String path : orderRequest.getOrderPhotos()) {
//            list.add(new ModeEdit(path, Constants.Modes.FROM_ABOVE));
//        }
////
////
////        List<String> listMo3adalah = createListMo3adalah(orderRequest.getOrderPhotos());
////        List<ModeEdit> listMo3adalah = createListMo3adalahEditMode(list);
//        listMo3adalaEditPending = createListMo3adalahEditMode(list);


//            AttachPhotosAdapter adapter = new AttachPhotosAdapter(context, listMo3adalah, true);
        AttachPhotosAdapter adapter = new AttachPhotosAdapter(context,
                //orderRequest.getOrderPhotosUris(),
                listMo3adalaEditPending,
                true,
                this,
                this);
        gridViewAttachedPhotos.setAdapter(adapter);

//        ((AttachPhotosAdapter)gridViewAttachedPhotos.getAdapter()).notifyDataSetChanged();


    }

    @NotNull
    private List<String> createListMo3adalah(ArrayList<String> orderPhotos) {
        List<String> listMo3adalah = new ArrayList<>();
        listMo3adalah.addAll(orderPhotos);
        for (int i = listMo3adalah.size(); i < 10; i++) {
            listMo3adalah.add(null);
        }
        return listMo3adalah;
    }

    @NotNull
    private List<ModeEdit> createListMo3adalahEditMode(ArrayList<ModeEdit> orderPhotos) {
        List<ModeEdit> listMo3adalah = new ArrayList<>();
        listMo3adalah.addAll(orderPhotos);
        for (int i = listMo3adalah.size(); i < 10; i++) {
//            listMo3adalah.add(null);
            listMo3adalah.add(new ModeEdit());
        }
        return listMo3adalah;
    }

    private void setUriToGridItemWhenEdit(ArrayList<String> listOfPaths) {
//        uris.add(position, uri);
//        uris.add(uri);

//        adpter(listOfPaths)

        for (int i = 0; i < listOfPaths.size(); i++) {

            positionForEdit = i;
//            hmOrderRequestUris.put(positionForEdit, Uri.parse(listOfPaths.get(positionForEdit)));
            Log.e(TAG, "setUriToGridItem:2 " + positionForEdit);

            ViewGroup group = (ViewGroup) gridViewAttachedPhotos.getChildAt(positionForEdit);
            Log.e(TAG, "setUriToGridItemWhenEdit: " + group);
            ImageView ivPhoto = group.findViewById(R.id.ivPhoto);
            Log.e(TAG, "setUriToGridItemWhenEdit: " + ivPhoto.getResources());


            group.findViewById(R.id.ivClose).setVisibility(View.VISIBLE);
            group.findViewById(R.id.ivClose).setTag(Integer.valueOf(hmOrderRequestUris.size()));


            group.findViewById(R.id.ivPhoto2).setVisibility(View.GONE);
            group.findViewById(R.id.ivPhoto).setVisibility(View.VISIBLE);
            Log.e(TAG, "setUriToGridItem:3  Done Bro :) ");


            ivPhoto.setImageURI(Uri.parse(listOfPaths.get(i)));
            break;

        }

//        hmOrderRequestUris.put(position, uri);
        Log.e(TAG, "setUriToGridItem: " + hmOrderRequestUris.size());
//        for (int i = 0; i < gridViewAttachedPhotos.getChildCount(); i++) {
//            if (i == position) {
//                Log.e(TAG, "setUriToGridItem:2 " + position);
//                ViewGroup group = (ViewGroup) gridViewAttachedPhotos.getChildAt(position);
//                ImageView ivPhoto = group.findViewById(R.id.ivPhoto);
//
//                group.findViewById(R.id.ivClose).setVisibility(View.VISIBLE);
//                group.findViewById(R.id.ivClose).setTag(Integer.valueOf(hmOrderRequestUris.size()));
//
//
//                group.findViewById(R.id.ivPhoto2).setVisibility(View.GONE);
//                group.findViewById(R.id.ivPhoto).setVisibility(View.VISIBLE);
//
//                ivPhoto.setImageURI(uri);
//                break;
//            }
//        }
    }

    @Override
    public void onDestroyOptionsMenu() {
        super.onDestroyOptionsMenu();
    }

    private void setGridViewHeightBasedOnChildren(GridView gridView, int columns) {
        ListAdapter listAdapter = gridView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        int items = listAdapter.getCount();
        int rows = 0;

        View listItem = listAdapter.getView(0, null, gridView);
        listItem.measure(0, 0);
        totalHeight = listItem.getMeasuredHeight();

        float x = 1;
        if (items > 4) {
            x = items / 4;
            rows = (int) (x + 1);
            totalHeight *= rows;
        }

        ViewGroup.LayoutParams params = gridView.getLayoutParams();
        params.height = totalHeight;
        gridView.setLayoutParams(params);

    }

    @Override
    public void onSaveInstanceState(@NotNull Bundle outState) {
        super.onSaveInstanceState(outState);

    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        String date = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
//        etDatePicker.setText(date);

        @SuppressLint("SimpleDateFormat") SimpleDateFormat format =
                new SimpleDateFormat("MM-dd-yyyy");
        String strDate = format.format(calenderNow.getTime());
        etDatePicker.setText(date);
        Log.e(TAG, "onDateSet: " + date);
        etDatePicker.clearFocus();

//        etDatePicker.clearFocus();
    }

    private void init() {
        spotsDialog = Utils.getInstance().pleaseWait(getActivity());


        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        //ref = storageReference.child("images/" + UUID.randomUUID().toString());
        ref = storageReference.child(UUID.randomUUID().toString());


    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {
//        etTimePicker.setText( "" + hourOfDay + ":" + minute);

        Calendar datetime = Calendar.getInstance();
        datetime.set(Calendar.HOUR_OF_DAY, hourOfDay);
        datetime.set(Calendar.MINUTE, minute);

        String am_pm = null;
        if (datetime.get(Calendar.AM_PM) == Calendar.AM)
            am_pm = "AM";
        else if (datetime.get(Calendar.AM_PM) == Calendar.PM)
            am_pm = "PM";

        String strHrsToShow = (datetime.get(Calendar.HOUR) == 0) ? "12" : datetime.get(Calendar.HOUR) + "";
        etTimePicker.setText(strHrsToShow + ":" + datetime.get(Calendar.MINUTE) + " " + am_pm);

//        if (context.getCurrentFocus() != null) {

        etTimePicker.clearFocus();
//        }
        if (etDatePicker.getText().length() != 0 && etTimePicker.getText().length() != 0) {
//            collapseAll(expansionLayouts.get(1));
            Utils.collapse(expansionLayouts.get(1).getExpansionLayout());
            rotateTo0Degree(expansionLayouts.get(1));

            Utils.expand(expansionLayouts.get(2).getExpansionLayout());
            rotateTo90Degree(expansionLayouts.get(2));
        }

    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
//        fetchPhotoFromCamOrGallery();
        if (!dialog.isShowing()) {
            dialog.show();
        }

    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {

    }

    @Override
    public void onRationaleAccepted(int requestCode) {
//        fetchPhotoFromCamOrGallery();
    }

    @Override
    public void onRationaleDenied(int requestCode) {


    }

    @AfterPermissionGranted(RC_CAMERA_AND_STORAGE)
    private void requestCamAndStoragePerms() {
        if (EasyPermissions.hasPermissions(Objects.requireNonNull(getActivity()), PERMISSIONS)) {
//            Log.e(TAG, "requestCamAndStoragePerms: ");
            if (!dialog.isShowing()) {
                fetchPhotoFromCamOrGallery();
                dialog.show();
            }
        } else {
            EasyPermissions.requestPermissions(
                    new PermissionRequest.Builder(this, RC_CAMERA_AND_STORAGE, PERMISSIONS)
                            .setRationale(R.string.cam_and_storage_rationale)
                            .setPositiveButtonText(R.string.rationale_ask_ok)
                            .setNegativeButtonText(R.string.rationale_ask_cancel)
//                            .setTheme(R.style.AppTheme)
                            .build());

        }
    }

    private void fetchPhotoFromCamOrGallery() {
//        @SuppressLint("ResourceType") BottomSheetMenuDialog dialog =
        dialog = new BottomSheetBuilder(context
                , null)
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
                            ImagePicker.cameraOnly().start(this);

                            break;
                        case R.id.chooseFromGellery:
//                            SeyanahEasyImage.easyImage(getActivity()).openGallery(FragmentProfile.this);
//                            SeyanahEasyImage.openGallery(getActivity());
//                            SeyanahEasyImage.openGallery(FragmentRequestOrder.this);
                            SeyanahEasyImage.openGallery(getActivity(), this);

                            break;
//                        case R.id.removePhoto:
//                            removePhoto();
//                            break;
                    }
                })
                .createDialog();

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "onDestroy: " + TAG);
//        Prefs.edit().remove(Constants.ORDER).apply();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.e(TAG, "rrrrrrrrrrr onDestroyView : XxX");

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (ImagePicker.shouldHandle(requestCode, resultCode, data)) {
            // Get a list of picked images
            List<Image> images = ImagePicker.getImages(data);
            // or get a single image only
            Image image = ImagePicker.getFirstImageOrNull(data);
            Uri uri = Uri.fromFile(new File(image.getPath()));
//                        uris.add(position, uri);
//                        uris.add(uri);
            setUriToGridItem(uri); // send uri
            Log.e(TAG, "onImagePicked: ");
        }


//        EasyImage.handleActivityResult(requestCode,
//                resultCode,
//                data,
//                context
//                , new DefaultCallback() {
//                    @Override
//                    public void onImagePicked(File imageFile, EasyImage.ImageSource source, int type) {
//                        Uri uri = Uri.fromFile(imageFile);
////                        uris.add(position, uri);
////                        uris.add(uri);
//                        setUriToGridItem(uri); // send uri
//                        Log.e(TAG, "onImagePicked: ");
//                    }
//                });

        SeyanahEasyImage.easyImage(getActivity()).handleActivityResult(requestCode, resultCode, data, context, new EasyImage.Callbacks() {
            @Override
            public void onImagePickerError(@NotNull Throwable throwable, @NotNull MediaSource mediaSource) {

            }

            @Override
            public void onMediaFilesPicked(@NotNull MediaFile[] mediaFiles, @NotNull MediaSource mediaSource) {
                Uri uri = Uri.fromFile(mediaFiles[0].getFile());
//                        uris.add(position, uri);
//                        uris.add(uri);
                setUriToGridItem(uri); // send uri
                Log.e(TAG, "onImagePicked: ");
            }

            @Override
            public void onCanceled(@NotNull MediaSource mediaSource) {

            }
        });


//        if (requestCode == Constants.REQUEST_CODE_Login_TO_RequestOrder && resultCode == Constants.RESULT_CODE_Login_To_RequestOrder) {
////            String testResult = data.getStringExtra(Constants.);

//            Log.e(TAG, "onActivityResult ");
//            Toasty.success(Objects.requireNonNull(getActivity()), "Oh yeah").show();
//            sendOrderToFirebaseDatabase(orderRequest);
//        }

        super.onActivityResult(requestCode, resultCode, data);

    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true, priority = 1)
    public void msgFromLoginToHere(MsgFromLoginToFragment msg) {
        if (msg != null) {
            if (Prefs.contains(Constants.ORDER)) {
                Log.e(TAG, "msgFromLoginToHere: ");
                sendOrderToFirebaseDatabase(orderRequest);
                // optionally you can clean your sticky event in different ways
                EventBus.getDefault().removeAllStickyEvents();
                // EventBus.getDefault().removeStickyEvent(stickyEvent);
                // EventBus.getDefault().removeStickyEvent(StickyEvent.class);
            }
        }


    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        Log.e(TAG, "rrrrrrrrrrr onAttach : XxX");

    }


    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        Log.e(TAG, "rrrrrrrrrrr onViewStateRestored : XxX");

//        editOrderIfExist();

    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);

    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
//        inflater.inflate(R.menu.menu_order_now, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPhotoClick(View convertView, int pos, String modeType) {
        this.modeOnPhotoClicked = modeType;
        position = pos;
        requestCamAndStoragePerms();
    }

    private void fillingHmWithPrevOrderRequest() {
        hmOrderRequestUris.clear();
        List<String> orderPhotosPaths = null;
        if (isThereEditOrder()) {
            orderPhotosPaths = orderRequest.getOrderPhotos();
        } else {
            orderPhotosPaths = orderRequest.getOrderPhotosUris();
        }
        for (int i = 0; i < orderPhotosPaths.size(); i++) {
            hmOrderRequestUris.put(i, orderPhotosPaths.get(i));
            Log.e(TAG, "editOrderIfExist: " + orderPhotosPaths.get(i));
        }
    }

    @Override
    public void onPhotoClosed(View convertView, int position, String modeType) {


        switch (modeType) {
            //Done
            case NORMAL:
                Log.e(TAG, "onPhotoClosed:  NORMAL");

                Log.e(TAG, "onPhotoClosed: " + position);
//                int pos = Integer.valueOf(convertView.findViewById(R.id.ivClose).getTag().toString());
//                Log.e(TAG, "onPhotoClosed: " + pos);
                hmOrderRequestUris.remove(position);
                orderRequest.getOrderPhotosUris().remove(position);
                Log.e(TAG, "setUriToGridItem: " + hmOrderRequestUris.size());
                ((ImageView) convertView.findViewById(R.id.ivPhoto)).setImageResource(R.drawable.add_file2);
                for (int i = 0; i < gridViewAttachedPhotos.getChildCount(); i++) {
                    if (i == position) {
//                        orderRequest.getOrderPhotosUris().remove(i);
                        convertView.findViewById(R.id.ivClose).setVisibility(View.GONE);
                        convertView.findViewById(R.id.ivPhoto2).setVisibility(View.VISIBLE);
                        convertView.findViewById(R.id.ivPhoto).setVisibility(View.GONE);
                        break;
                    }
                }
                refreshGridAttachPhotosNormalMode();

                break;
            case EDIT_CASHED_ORDER: {
                Log.e(TAG, "onPhotoClosed:  EDIT_CASHED_ORDER");
                Log.e(TAG, "onPhotoClosed: " + position);
//                int pos2 = Integer.valueOf(convertView.findViewById(R.id.ivClose).getTag().toString());
//                Log.e(TAG, "onPhotoClosed: " + pos2);
                hmOrderRequestUris.remove(position);
                Log.e(TAG, "setUriToGridItem: " + hmOrderRequestUris.size());
                ((ImageView) convertView.findViewById(R.id.ivPhoto)).setImageResource(R.drawable.add_file2);
                for (int i = 0; i < gridViewAttachedPhotos.getChildCount(); i++) {
                    if (i == position) {
                        orderRequest.getOrderPhotosUris().remove(i);
//                        Prefs.edit().putString(Constants.ORDER, gson.toJson(orderRequest, OrderRequest.class)).apply();
                        convertView.findViewById(R.id.ivClose).setVisibility(View.GONE);
                        convertView.findViewById(R.id.ivPhoto2).setVisibility(View.VISIBLE);
                        convertView.findViewById(R.id.ivPhoto).setVisibility(View.GONE);
                        break;
                    }
                }
                refreshCashedOrderMode();
            }

            break;

            case EDIT_PENDING_ORDER:

                Log.e(TAG, "onPhotoClosed:  EDIT_PENDING_ORDER" + hmOrderRequestUris.size());


                ModeEdit modeEdit = listMo3adalaEditPending.get(position);
                modeEdit.setMode(Constants.Modes.NO_FROM_USER_OR_ABOVE);
                modeEdit.setPath(null);
                orderRequest.getOrderPhotos().remove(position);
//                hmOrderRequestUris.remove(position);
                Log.e(TAG, "onPhotoClosed:1 " + position);
                ((ImageView) convertView.findViewById(R.id.ivPhoto)).setImageResource(R.drawable.add_file2);
                for (int i = 0; i < gridViewAttachedPhotos.getChildCount(); i++) {
                    if (i == position) {
                        Log.e(TAG, "onPhotoClosed:2 " + i);

//                        orderRequest.getOrderPhotos().remove(i);
//                        Prefs.edit().putString(Constants.ORDER, gson.toJson(orderRequest, OrderRequest.class)).apply();
                        convertView.findViewById(R.id.ivClose).setVisibility(View.GONE);
                        convertView.findViewById(R.id.ivPhoto2).setVisibility(View.VISIBLE);
                        convertView.findViewById(R.id.ivPhoto).setVisibility(View.GONE);
                        break;
                    }
                }
                refreshPendingOrderMode();
                break;
        }

    }

    private void setUriToGridItem(Uri uri) {

        switch (modeOnPhotoClicked) {
            //Done
            case NORMAL:
                Log.e(TAG, "onPhotoClick:  NORMAL");
                hmOrderRequestUris.put(position, uri.toString());
                Log.e(TAG, "setUriToGridItem: " + hmOrderRequestUris.size());
                for (int i = 0; i < gridViewAttachedPhotos.getChildCount(); i++) {
                    if (i == position) {
                        addPhoto(uri, position);
                        break;
                    }
                }
                orderRequest.getOrderPhotosUris().add(uri.toString());
                refreshGridAttachPhotosNormalMode();

                break;
            case EDIT_CASHED_ORDER:
                hmOrderRequestUris.put(position, uri.toString());
                Log.e(TAG, "setUriToGridItem: " + hmOrderRequestUris.size());
                for (int i = 0; i < gridViewAttachedPhotos.getChildCount(); i++) {
                    if (i == position) {
                        addPhoto(uri, position);
                        break;
                    }
                }
                orderRequest.getOrderPhotosUris().add(uri.toString());
                refreshCashedOrderMode();

                break;
            case EDIT_PENDING_ORDER:
                orderRequest.getOrderPhotos().add(uri.toString());
//                hmOrderRequestUris.put(position, uri.toString());
                Log.e(TAG, "setUriToGridItem: " + hmOrderRequestUris.size());
                for (int i = 0; i < gridViewAttachedPhotos.getChildCount(); i++) {
                    if (i == position) {
                        addPhoto(uri, position);
                        break;
                    }
                }
//                orderRequest.getOrderPhotos().add(uri.toString());
                ModeEdit modeEdit = listMo3adalaEditPending.get(position);
                modeEdit.setMode(Constants.Modes.FROM_USER_DEVICE);
                modeEdit.setPath(uri.toString());


//                listMo3adalaEditPending.add(new ModeEdit(uri.toString(), Constants.Modes.FROM_USER_DEVICE));
//                listMo3adalaEditPending.get(position).setMode(Constants.Modes.FROM_USER_DEVICE);
                refreshPendingOrderMode();

                Log.e(TAG, "onPhotoClick:  EDIT_PENDING_ORDER");
                break;
        }


    }

    private void refreshCashedOrderMode() {
        List<String> listMo3adalah = createListMo3adalah(orderRequest.getOrderPhotosUris());
        AttachPhotosAdapter adapter = new AttachPhotosAdapter(context,
                listMo3adalah,
                this,
                this,
                true);
        gridViewAttachedPhotos.setAdapter(adapter);
    }

    private void addPhoto(Uri uri, int position) {
        ViewGroup group = (ViewGroup) gridViewAttachedPhotos.getChildAt(position);
        Log.e(TAG, "setUriToGridItem:1 " + group + ", " + position);
        //it's container
        ImageView ivPhoto = group.findViewById(R.id.ivPhoto);
        Log.e(TAG, "setUriToGridItem:2 " + ivPhoto);


        group.findViewById(R.id.ivClose).setVisibility(View.VISIBLE);
        if (isThereEditOrder()) {
            group.findViewById(R.id.ivClose).setTag(UUID.randomUUID().toString());
            Log.e(TAG, "setUriToGridItem:2.1 " + group.findViewById(R.id.ivClose).getTag().toString());

        }


        //+ icon
        group.findViewById(R.id.ivPhoto2).setVisibility(View.GONE);

        ivPhoto.setImageURI(uri);
        //it's uploaded image
        group.findViewById(R.id.ivPhoto).setVisibility(View.VISIBLE);


    }

    @Override
    public void onClick(View view, int pos) {
        category = null;
        Log.e(TAG, "onClick: ooo ");
        selectDeselctCategory(view, pos);
    }

    private void selectDeselctCategory(View view, int pos) {
        if (view.findViewById(R.id.llChecked).getVisibility() != View.VISIBLE) {
            setCatAsSelected(view, pos);

        } else {
            for (int i = 0; i < rvSelectCat.getChildCount(); i++) {
                View view2 = rvSelectCat.getChildAt(i);
                view2.findViewById(R.id.llChecked).setVisibility(View.GONE);
            }

        }
    }

    private void setCatAsSelected(View view, int pos) {
        for (int i = 0; i < rvSelectCat.getChildCount(); i++) {
            View view2 = rvSelectCat.getChildAt(i);
            view2.findViewById(R.id.llChecked).setVisibility(View.GONE);
        }

        Log.e(TAG, "onClick: 1");
        if (categories != null && !categories.isEmpty()) {
            category = categories.get(pos);
        }
//            category.setVisible(true);
        highlightCatIcon(view);
    }

    private void highlightCatIcon(View view) {
        view.findViewById(R.id.llChecked).setVisibility(View.VISIBLE);

        TextView textView = view.findViewById(R.id.tvTitle);
        textView.setSelected(true);

        LottieAnimationView lottieAnimationView = view.findViewById(R.id.lottieAnimationChecked);
        if (!lottieAnimationView.isAnimating()) {
            lottieAnimationView.playAnimation();
        } else {
            lottieAnimationView.pauseAnimation();
            lottieAnimationView.playAnimation();
        }
    }

    private void accessExpansionDataViews() {


        //=====================================the question 1  - select date =========
        View viewSelectDate = fixedQuestions.get(1).getChildToAdd();
        etDatePicker = viewSelectDate.findViewById(R.id.etDatePicker);
        etTimePicker = viewSelectDate.findViewById(R.id.etTimePicker);

        llDatePicker = viewSelectDate.findViewById(R.id.llDatePicker);
        llTimePicker = viewSelectDate.findViewById(R.id.llTimePicker);

        llDatePicker.setOnClickListener(view -> {


            calenderNow = Calendar.getInstance();
            pickerDate = DatePickerDialog.newInstance(
                    FragmentRequestOrder.this,
                    calenderNow.get(Calendar.YEAR), // Initial year selection
                    calenderNow.get(Calendar.MONTH), // Initial month selection
                    calenderNow.get(Calendar.DAY_OF_MONTH) // Inital day selection
            );
            pickerDate.setMinDate(calenderNow);

            assert getFragmentManager() != null;
            pickerDate.show(getFragmentManager(), "Datepickerdialog");


        });
        llTimePicker.setOnClickListener(view -> {
            calenderNow = Calendar.getInstance();
            pickerTime = TimePickerDialog.newInstance(
                    FragmentRequestOrder.this,
                    calenderNow.get(Calendar.YEAR), // Initial year selection
                    calenderNow.get(Calendar.MONTH), // Initial month selection
                    false
            );
            //disabling prev timepicker dialog
            pickerTime.setMinTime(calenderNow.get(Calendar.HOUR_OF_DAY),
                    calenderNow.get(Calendar.MINUTE),
                    calenderNow.get(Calendar.SECOND));


            assert getFragmentManager() != null;
            pickerTime.show(getFragmentManager(), "TimePickerDialog");
        });


        //=====================================the question 2  - select location=========
        View viewSelectLoc = fixedQuestions.get(2).getChildToAdd();

        etEnterLocation = viewSelectLoc.findViewById(R.id.etEnterLocation);

        spinnerCountry = viewSelectLoc.findViewById(R.id.spinnerCountry);
        spinnerCity = viewSelectLoc.findViewById(R.id.spinnerCity);
        spinnerArea = viewSelectLoc.findViewById(R.id.spinnerArea);


        if (!Prefs.contains(Constants.ORDER) && !isThereEditOrder()) {
            loadAllCountries();
        }

        spinnerCountry.setOnItemSelectedListener(this);

        spinnerCity.setOnItemSelectedListener(this);

        //=====================================the question 3  - add desc=========
        View viewAddDesc = fixedQuestions.get(3).getChildToAdd();
        etEnterDesc = viewAddDesc.findViewById(R.id.etEnterDesc);

        //=====================================the question 4  - select photos=========
        View viewSelectPhotos = fixedQuestions.get(4).getChildToAdd();
        gridViewAttachedPhotos = viewSelectPhotos.findViewById(R.id.gridViewAttachedPhotos);
//        orderRequest = new OrderRequest();
//        refreshGridAttachPhotosNormalMode();
//        setGridViewHeightBasedOnChildren(gridViewAttachedPhotos, 4);
    }

    private void accessQuestion1() {
        View viewSelectCat = fixedQuestions.get(0).getChildToAdd();
        rvSelectCat = viewSelectCat.findViewById(R.id.rvSelectCat);
        loadingCats = viewSelectCat.findViewById(R.id.rlLoadingCats);

//        swipeLoadAlCats = viewSelectCat.findViewById(R.id.swipeLoadAlCats);
//        swipeLoadAlCats.setOnRefreshListener(this);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(context, 3);
        rvSelectCat.setLayoutManager(gridLayoutManager);
        rvSelectCat.setItemAnimator(new DefaultItemAnimator());
        rvSelectCat.setHasFixedSize(false);
//        SelectCatAdapter mAdapter = new SelectCatAdapter(categories, FragmentRequestOrder.this::onClick);
//        rvSelectCat.setAdapter(mAdapter);

//        loadCatsFromFirebaseToGridView();
        //loadCatsUsingFirebaseUI();

//        fireFetchCategoryAdapter.setCategoryId(null);
//        Prefs.edit().remove(Constants.ORDER_ID_PENDING_ORDER).apply();
//        fireFetchCategoryAdapter.startListening();

        rvSelectCat.setVisibility(View.VISIBLE);
        loadingCats.setVisibility(View.GONE);
    }

    private void setSelectedCountry() {
        Log.e(TAG, "setSelectedLocation: Start");
        RefBase.CountrySection.refLocGetCountry(orderRequest.getLocation().getCountryId())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        dataSnapshot.getRef().removeEventListener(this);
                        if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {
//                            Log.e(TAG, "onDataChange__474" + dataSnapshot.getValue());
//                            Log.e(TAG, "onDataChange__474" + dataSnapshot.toString());
                            HashMap<String, Object> hm = (HashMap<String, Object>) dataSnapshot.getValue();
                            String countryName = null;
                            if (hm != null) {
                                if ((countryName =
                                        String.valueOf(hm.get(Constants.COUNTRY_NAME))) != null) {
                                    Log.e(TAG, "onDataChange__474" + countryName);
                                    //locationFetchedOrder.setCountry(countryName);
                                    for (int i = 0; i < listCountries.size(); i++) {
                                        if (TextUtils.equals(countryName, listCountries.get(i))) {
                                            spinnerCountry.setSelectedIndex(i);
                                            posSpinnerCountry = i;
                                            break;
                                        }
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

    private void setSelectedCity() {
        RefBase.CitySection.refLocGetCity(orderRequest.getLocation().getCountryId(),
                orderRequest.getLocation().getCityId())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        dataSnapshot.getRef().removeEventListener(this);
                        if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {
//                            Log.e(TAG, "onDataChange__474" + dataSnapshot.getValue());
//                            Log.e(TAG, "onDataChange__474" + dataSnapshot.toString());
                            HashMap<String, Object> hm = (HashMap<String, Object>) dataSnapshot.getValue();
                            String cityName = null;
                            if (hm != null) {
                                if ((cityName = String.valueOf(hm.get(Constants.CITY_NAME))) != null) {
                                    Log.e(TAG, "onDataChange__474" + cityName);
                                    //locationFetchedOrder.setCity(cityName);
                                    for (int i = 0; i < listCities.size(); i++) {
                                        if (TextUtils.equals(cityName, listCities.get(i))) {
                                            spinnerCity.setSelectedIndex(i);
                                            posSpinnerCity = i;
                                            break;
                                        }
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

    private void loadAllCountries() {
        RefBase.CountrySection.refLocCountries()
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        dataSnapshot.getRef().removeEventListener(this);
                        if (dataSnapshot.exists()) {
                            listCountries.clear();
                            listCountriesObj.clear();
//                            listCountries.add(Constants.SELECT_COUNTRY);
                            listCountries.add(context.getString(R.string.select_country2));
                            listCountriesObj.add(new Country());//fake
                            for (DataSnapshot dataSnapshot1 :
                                    dataSnapshot.getChildren()) {
                                Log.e(TAG, "ttttttttt: " + dataSnapshot1.toString());
                                HashMap<String, Object> hashMap = (HashMap<String, Object>) dataSnapshot1.getValue();
                                if (hashMap != null) {
                                    Country country = new Country();
                                    country.setCountryId(dataSnapshot1.getKey());
                                    if (hashMap.get("countryName") != null) {
                                        country.setCountryName(hashMap.get("countryName").toString());
                                    }
                                    if (hashMap.get("countryNameArabic") != null) {
                                        country.setCountryNameArabic(hashMap.get("countryNameArabic").toString());
                                    }

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
                        if (isThereEditOrder() || Prefs.contains(Constants.ORDER)) {
                            setSelectedCountry();
                        }
                        dataSnapshot.getRef().removeEventListener(this);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void refreshGridAttachPhotosNormalMode() {
        List<String> listMo3adalah = null;
        if (isThereEditOrder() || Prefs.contains(Constants.ORDER)) {
            listMo3adalah = createListMo3adalah(new ArrayList<>());
        } else {
            listMo3adalah = createListMo3adalah(orderRequest.getOrderPhotosUris());
        }
        AttachPhotosAdapter adapter = new AttachPhotosAdapter(listMo3adalah, context,
                this,
                this,
                true);
        gridViewAttachedPhotos.setAdapter(adapter);
        setGridViewHeightBasedOnChildren(gridViewAttachedPhotos, 4);
        Log.e(TAG, "refreshGridAttachPhotosNormalMode: " + "tessst");
    }

    private void loadCatsUsingFirebaseUI() {

        rvSelectCat.setVisibility(View.VISIBLE);
        loadingCats.setVisibility(View.GONE);

        //it's skip auto generated id and get the data that inside it.
        DatabaseReference refAllCats = RefBase.refCategories();

        FirebaseRecyclerOptions<Category> firebaseRecyclerOptions = new FirebaseRecyclerOptions.Builder<Category>()
                .setQuery(refAllCats, Category.class)
                .build();

        fireFetchCategoryAdapter = new FireFetchCategoryAdapter(firebaseRecyclerOptions, this);
        rvSelectCat.setAdapter(fireFetchCategoryAdapter);


    }

    private void loadCatsFromFirebaseToGridView() {

//        swipeLoadAlCats.setRefreshing(true);


        //spotsDialog.setBody("Please wait");
//        spotsDialog.setCancelable(false);
//        spotsDialog.setCanceledOnTouchOutside(false);
//        spotsDialog.show();

        rvSelectCat.setVisibility(View.GONE);
        loadingCats.setVisibility(View.VISIBLE);

        RefBase.refCategories().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                dataSnapshot.getRef().removeEventListener(this);
                if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {
//                    dataSnapshot.getRef().removeEventListener(this);
                    Log.e(TAG, "dataSnapshot 1  " + dataSnapshot.getKey());
                    categories.clear();
//                    spotsDialog.dismiss();
                    for (DataSnapshot snapShot :
                            dataSnapshot.getChildren()) {

                        Category category = new Category();
                        HashMap<String, Object> map = (HashMap<String, Object>) snapShot.getValue();
                        category.setCategoryId(snapShot.getKey());
                        if (map != null) {

                            if (map.get("categoryIcon") != null) {
                                category.setCategoryIcon(map.get("categoryIcon").toString());
                            }


                            category.setCategoryName(map.get("categoryName").toString());
                            category.setCategoryNameArabic(map.get("categoryNameArabic").toString());
                            Log.e(TAG, "getCategoryName: " + Utils.getCatName(category));

                        }
                        categories.add(category);
                    }
                    if (rvSelectCat != null) {

                        SelectCatAdapter mAdapter = new SelectCatAdapter(categories, FragmentRequestOrder.this);
                        rvSelectCat.setAdapter(mAdapter);
//                        spotsDialog.dismiss();

                        rvSelectCat.setVisibility(View.VISIBLE);
                        loadingCats.setVisibility(View.GONE);


//                        String categoryId = orderRequest.getCategoryId();
//                        if (categoryId != null && categoryId != "") {
//                            for (int i = 0; i < categories.size(); i++) {
//                                if (TextUtils.equals(categoryId, categories.get(i).getCategoryId())) {
//                                    category = categories.get(i);
//                                    categories.get(i).setVisible(true);
//                                    break;
//                                } else {
//                                    categories.get(i).setVisible(false);
//                                }
//                            }
//                            rvSelectCat.getAdapter().notifyDataSetChanged();
//                        }

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //spotsDialog.dismiss();

            }
        });
    }

    private void setSelectedCat() {
        if (isThereEditOrder() || Prefs.contains(Constants.ORDER)) {
            if (orderRequest != null) {
                RefBase.refCategory(orderRequest.getCategoryId())
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                dataSnapshot.getRef().removeEventListener(this);
                                if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {
                                    Category category = dataSnapshot.getValue(Category.class);
                                    for (int i = 0; i < categories.size(); i++) {
                                        if (TextUtils.equals(Utils.getCatName(category), categories.get(i).getCategoryName())) {
                                            ViewGroup viewGroup = (ViewGroup) rvSelectCat.getChildAt(i);
                                            highlightCatIcon(viewGroup);
                                            Log.e(TAG, "onDataChange_2323 sdfsf" + categories.get(i).getCategoryName());
                                            Log.e(TAG, "onDataChange: " + category.getCategoryName());
//                                    for (int i = 0; i < categories.size(); i++) {
//                                    for (int i = 0; i < rvSelectCat.getChildCount(); i++) {
//                                        if (TextUtils.equals(category.getCategoryName(), categories.get(i).getCategoryName())) {
//                                            View viewGroup = rvSelectCat.getChildAt(i);
////                                            highlightCatIcon(viewGroup);
////                                        Log.e(TAG, "onDataChange_2323 sdfsf");
//                                            selectDeselctCategory(viewGroup, i);
//                                            Log.e(TAG, "onDataChange___ " + "sos");
//                                            break;
//                                        }
                                        }
                                    }
                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }

                        });
            }
        }
    }

    private boolean isThereEditOrder() {

        return getArguments() != null && getArguments().containsKey(Constants.EDIT_ORDER_);
    }

    private void loadAllCatsIntoRecyclerView() {

        RefBase.refCategories()
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            categories.clear();
                            for (DataSnapshot snap :
                                    dataSnapshot.getChildren()) {

                                HashMap<String, Object> map = (HashMap<String, Object>) snap.getValue();
                                Category category = new Category();
                                category.setCategoryName(map.get("categoryName").toString());
                                category.setCategoryNameArabic(map.get("categoryNameArabic").toString());
                                category.setCategoryId(snap.getKey());
                                category.setCategoryIcon(map.get("categoryIcon").toString());

                                categories.add(category);

                            }
                            SelectCatAdapter mAdapter = new SelectCatAdapter(categories, FragmentRequestOrder.this::onClick);
                            rvSelectCat.setAdapter(mAdapter);
//                            swipeLoadAlCats.setRefreshing(false);
                            if (Prefs.getPreferences().contains(Constants.ORDER)) {
                                Log.e(TAG, "onBindViewHolder: uuu");
                                orderRequest = new Gson().fromJson(Prefs.getString(Constants.ORDER, ""), type);
                                String categoryId = orderRequest.getCategoryId();
                                if (categoryId != null && categoryId != "") {
                                    for (int i = 0; i < categories.size(); i++) {
                                        if (TextUtils.equals(categoryId, categories.get(i).getCategoryId())) {
                                            category = categories.get(i);
                                            Log.e(TAG, "btnOrderNow:vvvv ");
                                            categories.get(i).setVisible(true);
                                            break;
                                        } else {
                                            categories.get(i).setVisible(false);
                                        }
                                    }
                                    rvSelectCat.getAdapter().notifyDataSetChanged();
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

    }

    @Override
    public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
        switch (view.getId()) {
            case R.id.spinnerCity:
                posSpinnerCity = position;
                Log.e(TAG, "onItemSelected: posSpinnerCity " + posSpinnerCity);
                if (position != 0) {
                    selectedCity = listCities.get(position);
                    cityKeySelected = listKeyCities.get(position);
                }
                break;
            case R.id.spinnerArea:
                posSpinnerArea = position;
                break;
            case R.id.spinnerCountry:
                posSpinnerCountry = position;
                Log.e(TAG, "onItemSelected: posSpinnerCountry before " + posSpinnerCountry);

                if (position != 0) {
//                if (position == 0) {
                    spinnerCity.setVisibility(View.VISIBLE);
                    country = listCountriesObj.get(position);
                    countryId = country.getCountryId();
                    Log.e(TAG, "onItemSelected: posSpinnerCountry after " + posSpinnerCountry);

                    spotsDialog = Utils.getInstance().pleaseWait(getActivity());
                    spotsDialog.setMessage(context.getString(R.string.please_wait));
                    spotsDialog.setCancelable(false);
                    spotsDialog.setCanceledOnTouchOutside(false);
                    spotsDialog.show();

                    loadAllCities();
                } else {
                    selectedCity = listCities.get(position);
                    cityKeySelected = listKeyCities.get(position);
                    spinnerCity.setVisibility(View.GONE);
                }
                break;
            default:
                break;
        }
    }

    private void loadAllCities() {

//        if (isThereEditOrder())

        RefBase.CitySection.refLocCities(countryId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        dataSnapshot.getRef().removeEventListener(this);
                        if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {
                            listCities.clear();
                            listKeyCities.clear();
//                                        listCities.add(Constants.SELECT_CITY);
                            listCities.add(context.getString(R.string.select_city));
                            listKeyCities.add("fake");
                            for (DataSnapshot dataSnapshot1 :
                                    dataSnapshot.getChildren()) {
//                                        Log.e(TAG, "onDataChange: " + dataSnapshot.toString());
                                HashMap<String, Object> map =
                                        (HashMap<String, Object>) dataSnapshot1.getValue();
                                if (map != null) {
//                                            Log.e(TAG, "onDataChange: " + map.toString());
//                                            Log.e(TAG, "onDataChange: " + dataSnapshot1.getKey());
                                    Log.e(TAG, "onDataChange: " + map.get("cityName").toString());

//                                            String city = dataSnapshot1.getValue(String.class);
                                    String city = "";
                                    String key = dataSnapshot1.getKey();
                                    if (Utils.lang()) {
                                        city = (String) map.get("cityNameArabic");
                                    } else {
                                        city = (String) map.get("cityName");
                                    }

                                    if (city != null) {
                                        listCities.add(city);
                                    }
//                                    listCities.add(city);
                                    listKeyCities.add(key);
//                                            Log.e(TAG, "onDataChange: " + city);
//                                            Log.e(TAG, "onDataChange: " + key);
                                }
                            }
                            spotsDialog.dismiss();
                        }

                        spinnerCity.setItems(listCities);
                        if (isThereEditOrder() || Prefs.contains(Constants.ORDER)) {
                            setSelectedCity();
                        }
                        dataSnapshot.getRef().removeEventListener(this);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    @Override
    public void onDismiss(DialogInterface dialogInterface) {
        Log.e(TAG, "onDismiss: ");
        if (dialogInterface == pickerDate) {
//            if (context.getCurrentFocus() != null) {
            etDatePicker.clearFocus();
//            }
            Log.e(TAG, "onDismiss: pickerDate");
        }
        if (dialogInterface == pickerTime) {
//            if (context.getCurrentFocus() != null) {
            etTimePicker.clearFocus();
//            }
            Log.e(TAG, "onDismiss: pickerTime");
        }
    }

    @Override
    public void onCancel(DialogInterface dialogInterface) {


    }

    @Override
    public void onNegativeButtonClicked() {

    }

    @Override
    public void onNeutralButtonClicked() {

    }

    @Override
    public void onPositiveButtonClicked(int i, @NotNull String s) {

    }

    @Override
    public void onRefresh() {

//        loadAllCatsIntoRecyclerView();
//        loadCatsFromFirebaseToGridView();

    }

    private void addNotificationToControlPanel() {

        String key = RefBase.CPNotification().push().getKey();

        String userId = Prefs.getString(Constants.FIREBASE_UID, "");

        CPNotification cpNotification = new CPNotification();
        cpNotification.setCustomerId(userId);
        cpNotification.setRequestId(orderRequest.getOrderId());
        cpNotification.setKeyword(Constants.REQUESTS);
//        if (Utils.customerOrWorker()) {
//            cpNotification.setKeyword(Constants.USERS);
//        } else {
//            cpNotification.setKeyword(Constants.WORKERS);
//        }
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
    public void onClick(View v, int pos, Category category) {
        Log.e(TAG, "onClick_firebase_ui " + category.getCategoryId());
//        FragmentRequestOrder.this.category = category;
        this.category = category;
        orderRequest.setCategoryId(category.getCategoryId());
        Log.e(TAG, "onClick: 000  " + category.getCategoryId());
        setCatAsSelected(v, pos);
    }

    private class OnClickListener implements View.OnClickListener {
        ExpansionHeaderWithLayout layout;

        OnClickListener(ExpansionHeaderWithLayout layout) {
            this.layout = layout;
        }

        @Override
        public void onClick(View view) {
            if (layout.getExpansionLayout().getVisibility() == View.VISIBLE) {
                collapseAll(layout);
                Log.e(TAG, "onClick: 222 ");
            } else {
                //close all then open clicked layout
                Log.e(TAG, "onClick: 333 ");

                collapseAll2();
                Utils.expand(layout.getExpansionLayout());
                rotateTo90Degree(layout);
            }
        }
    }

    @Override
    public void onAttach(@NonNull Activity activity) {
        super.onAttach(activity);
        Log.e(TAG, "rrrrrrrrrrr onAttach : XxX");

    }

}
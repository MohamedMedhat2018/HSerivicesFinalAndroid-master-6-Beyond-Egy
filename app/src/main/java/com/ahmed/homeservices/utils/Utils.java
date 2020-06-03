package com.ahmed.homeservices.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Point;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.text.TextUtils;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.ahmed.homeservices.R;
import com.ahmed.homeservices.constants.Constants;
import com.ahmed.homeservices.models.Category;
import com.ahmed.homeservices.models.Country;
import com.asksira.loopingviewpager.LoopingViewPager;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.kazakago.cryptore.CipherAlgorithm;
import com.kazakago.cryptore.Cryptore;
import com.kazakago.cryptore.DecryptResult;
import com.kazakago.cryptore.EncryptResult;
import com.pixplicity.easyprefs.library.Prefs;
import com.rd.PageIndicatorView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import dmax.dialog.SpotsDialog;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

//import com.facebook.login.LoginManager;

public final class Utils {
    @SuppressLint("StaticFieldLeak")
    private static final Utils ourInstance = new Utils();
    private static final String TAG = "Utils";
    @SuppressLint("StaticFieldLeak")
    private static Context context;

    public Utils() {
    }

    public Utils(Context context) {
        Utils.context = context;
    }

    /**
     * Used to scroll to the given view.
     *
     * @param scrollViewParent Parent ScrollView
     * @param view             View to which we need to scroll.
     */
    public static void scrollToView(final ScrollView scrollViewParent, final View view) {
        // Get deepChild Offset
        Point childOffset = new Point();
        getDeepChildOffset(scrollViewParent, view.getParent(), view, childOffset);
        // Scroll to child.
        scrollViewParent.smoothScrollTo(0, childOffset.y);
    }

    /**
     * Used to get deep child offset.
     * <p/>
     * 1. We need to scroll to child in scrollview, but the child may not the direct child to scrollview.
     * 2. So to get correct child position to scroll, we need to iterate through all of its parent views till the main parent.
     *
     * @param mainParent        Main Top parent.
     * @param parent            Parent.
     * @param child             Child.
     * @param accumulatedOffset Accumulated Offset.
     */
    public static void getDeepChildOffset(final ViewGroup mainParent, final ViewParent parent,
                                          final View child, final Point accumulatedOffset) {
        ViewGroup parentGroup = (ViewGroup) parent;
        accumulatedOffset.x += child.getLeft();
        accumulatedOffset.y += child.getTop();
        if (parentGroup.equals(mainParent)) {
            return;
        }
        getDeepChildOffset(mainParent, parentGroup.getParent(), parentGroup, accumulatedOffset);
    }


    public static boolean isValidPassword(final String password) {

        Pattern pattern;
        Matcher matcher;

        final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=\\S+$).{6,}$";

        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(password);

        return matcher.matches();

    }

    public static void buildCalligraphyFont() {
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
//                .setDefaultFontPath("fonts/lato-regular.ttf")
                        .setDefaultFontPath("fonts/roboto_bold.ttf")
                        .setFontAttrId(R.attr.fontPath)
                        .build()
        );
    }

    public static String getCurrentDeviceLang() {


//        Locale.getDefault().getLanguage()       ---> en
//        Locale.getDefault().getISO3Language()   ---> eng
//        Locale.getDefault().getCountry()        ---> US
//        Locale.getDefault().getISO3Country()    ---> USA
//        Locale.getDefault().getDisplayCountry() ---> United States
//        Locale.getDefault().getDisplayName()    ---> English (United States)
//        Locale.getDefault().toString()          ---> en_US
//        Locale.getDefault().getDisplayLanguage()---> English


//        return Locale.getDefault().getDisplayLanguage();
//        return Locale.getDefault().getLanguage();
//        return Locale.getDefault().getISO3Language();
//        return Locale.getDefault().getDisplayName();
//        return Locale.getDefault().getCountry();
//        return Locale.getDefault().getDisplayCountry();

        return Resources.getSystem().getConfiguration().locale.toString();

    }

    public static String setFirstUpperCharOfWord(String str) {
        String s = "";

        String update = (str.substring(0, 1).toUpperCase() + (str.substring(1)).toLowerCase());
        update = update.replaceAll("_", " ");

        switch (str) {
            case Constants.PENDING:
//                s =
                break;
            case Constants.CM_WORKING:

                break;
            case Constants.CM_FINISHED:


                break;
            case Constants.FREELANCE_WORKING:

                break;
            case Constants.FREELANCE_FINISHED:

                break;
            case Constants.POST:

                break;

        }


        return update;
    }

    public static void attachIndicatiorToViewPager(LoopingViewPager loopingViewPager, PageIndicatorView pageIndicatorView) {
        //Custom bind indicator
        pageIndicatorView.setCount(loopingViewPager.getIndicatorCount());
        loopingViewPager.setIndicatorPageChangeListener(new LoopingViewPager.IndicatorPageChangeListener() {
            @Override
            public void onIndicatorProgress(int selectingPosition, float progress) {
                pageIndicatorView.setProgress(selectingPosition, progress);
            }

            @Override
            public void onIndicatorPageChange(int newIndicatorPosition) {
//                indicatorView.setSelection(newIndicatorPosition);
            }
        });
    }

//    private static void fullScreen(Activity activity){
//        // remove title
//        activity.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);
//    }

    public static boolean updateResources(Context context, String language) {
        Locale locale = new Locale(language);
        Locale.setDefault(locale);
        Resources resources = context.getResources();
        Configuration configuration = resources.getConfiguration();
        configuration.locale = locale;
        resources.updateConfiguration(configuration, resources.getDisplayMetrics());
        return true;
    }

    // Generic function to join two lists in Java
    public static <T> List<T> merge(List<T>... list) {
        List<T> list1 = new ArrayList<>();
//
//        list.addAll(listPending);
//        list.addAll(listCmFinishing);
//        list.addAll(listCmWorking);


        for (List<T> object : list) {
            list1.addAll(object);
        }
        return list1;
    }

    public static float dpToPixels(int dp, Context context) {
        return dp * (context.getResources().getDisplayMetrics().density);
    }

    public static int dpToPx(Context context, float dp) {
        return (int) (dp * ((float) context.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    public static float pxToDp(Context context, float px) {
        return px / ((float) context.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }

    public static Utils getInstance() {
        return ourInstance;
    }

    public static List<Category> generateStaticServiceCats() {
        List<Category> categories = new ArrayList<>();

//        categories.clear();
        categories.clear();
//        categories.add(new Category("AC", R.drawable.ac));
//        categories.add(new Category("Electrician", R.drawable.electrician));
//        categories.add(new Category("Plumber", R.drawable.plumber));
//        categories.add(new Category("carpenter", R.drawable.hammer));
//        categories.add(new Category("TV", R.drawable.tv));
//        categories.add(new Category("Refrigerator", R.drawable.refrigerator));
//        categories.add(new Category("Appliances", R.drawable.appliances));
//        categories.add(new Category("RO", R.drawable.ro));
//        categories.add(new Category("Computers", R.drawable.computers));
//        categories.add(new Category("Mobile", R.drawable.mobile));
//        categories.add(new Category("Home Secure", R.drawable.home_secure));
//        categories.add(new Category("Pests Control", R.drawable.peast_control));
//        categories.add(new Category("Car Wash", R.drawable.car_wash));
//        categories.add(new Category("Cleaning", R.drawable.cleaning));
//        categories.add(new Category("Painting", R.drawable.painting));
//        categories.add(new Category("Washing Machine", R.drawable.car_wash));
//        categories.add(new Category("Packers Movers", R.drawable.furniture));
//        categories.add(new Category("Laundry", R.drawable.laundry));

        return categories;
    }

    public static void expand(final View v) {
        try {
            int matchParentMeasureSpec = View.MeasureSpec.makeMeasureSpec(((View) v.getParent()).getWidth(), View.MeasureSpec.EXACTLY);
            int wrapContentMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
            v.measure(matchParentMeasureSpec, wrapContentMeasureSpec);
            final int targetHeight = v.getMeasuredHeight();

            // Older versions of android (pre API 21) cancel animations for views with a height of 0.
            v.getLayoutParams().height = 1;
            v.setVisibility(View.VISIBLE);

            Animation a = new Animation() {
                @Override
                protected void applyTransformation(float interpolatedTime, Transformation t) {
                    v.getLayoutParams().height = interpolatedTime == 1
                            ? ViewGroup.LayoutParams.WRAP_CONTENT
                            : (int) (targetHeight * interpolatedTime);
                    v.requestLayout();
                    Log.e(TAG, "applyTransformation: ");
                }

                @Override
                public boolean willChangeBounds() {
                    return true;
                }
            };

            // Expansion speed of 1dp/ms
            a.setDuration((int) (targetHeight / v.getContext().getResources().getDisplayMetrics().density));
            v.startAnimation(a);
        } catch (Exception e) {
            v.setVisibility(View.VISIBLE);
        }


//        v.setVisibility(View.VISIBLE);

    }

    public static void collapse(final View v) {
        final int initialHeight = v.getMeasuredHeight();

        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if (interpolatedTime == 1) {
                    v.setVisibility(View.GONE);


                } else {
                    v.getLayoutParams().height = initialHeight - (int) (initialHeight * interpolatedTime);
                    v.requestLayout();
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // Collapse speed of 1dp/ms
        a.setDuration((int) (initialHeight / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
    }

    public static void collapse(final View v, int pos, int max, ScrollView scrollView) {
        final int initialHeight = v.getMeasuredHeight();

        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if (interpolatedTime == 1) {
                    v.setVisibility(View.GONE);

//                  if (pos == max){
//                      scrollView.post(() -> {
//                          //        scrollView.fullScroll(View.FOCUS_UP);
//                          scrollView.fullScroll(View.FOCUS_DOWN);
//                      });
//                      Log.e(TAG, "dsdd_ dsdd");
//                  }

                } else {
                    v.getLayoutParams().height = initialHeight - (int) (initialHeight * interpolatedTime);
                    v.requestLayout();
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // Collapse speed of 1dp/ms
        a.setDuration((int) (initialHeight / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
    }

    public static boolean customerOrWorker() {
        if (Prefs.contains(Constants.WORKER_OR_CUSTOMER)) {
            if (TextUtils.equals(Prefs.getString(Constants.WORKER_OR_CUSTOMER, ""),
                    Constants.CUSTOMERS)) {
                Log.e(TAG, "CUSTOMERS: ");
                return true;//for customers
            } else {
                Log.e(TAG, "WORKERS: ");
                return false;//for workers
            }
        }
        return false;
    }

    public static boolean companyOrNot() {
//        if (Prefs.contains(Constants.WORKER_OR_CUSTOMER)) {
        if (TextUtils.equals(Prefs.getString(Constants.WORKER_OR_CUSTOMER, ""),
                Constants.COMPANIES)) {
            Log.e(TAG, "COMPANIES: ");
            return true;//for customers
        }
//            else {
//                Log.e(TAG, "WORKERS: ");
//                return false;//for workers
//            }
//        }
        return false;
    }

    public static void fullScreen(Activity activity) {
// remove title
        activity.requestWindowFeature(Window.FEATURE_NO_TITLE);
        activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

    }

    //    @SuppressLint("ResourceAsColor")
    public static String getCatName(Category category) {
        String categoryName = "";
        if (Prefs.contains(Constants.SELECTED_LANG)) {
            String selectedLanguage = Prefs.getString(Constants.SELECTED_LANG, "");
            Log.e(TAG, "onBindViewHolder: " + selectedLanguage);

            if (TextUtils.equals(selectedLanguage, Constants.LANG_ARABIC)) {
                categoryName = category.getCategoryNameArabic();
            } else {
                categoryName = category.getCategoryName();
            }
        }
        return categoryName;

    }


    public static boolean lang() {
        if (Prefs.contains(Constants.SELECTED_LANG)) {
            String selectedLanguage = Prefs.getString(Constants.SELECTED_LANG, "");
            return TextUtils.equals(selectedLanguage, Constants.LANG_ARABIC);
        }
        return false;
    }

    public static String getCountryName(Country country) {
        String categoryName = "";
        if (Prefs.contains(Constants.SELECTED_LANG)) {
            String selectedLanguage = Prefs.getString(Constants.SELECTED_LANG, "");
            Log.e(TAG, "onBindViewHolder: " + selectedLanguage);

            if (TextUtils.equals(selectedLanguage, Constants.LANG_ARABIC)) {
                categoryName = country.getCountryNameArabic();
            } else {
                categoryName = country.getCountryName();
            }
        }
        return categoryName;

    }

    public static void setColorSchemeToSwipeRefrseh(SwipeRefreshLayout swipeRefreshLayout) {
//        swipeRefreshLayout.setColorSchemeColors(Color.BLUE, Color.YELLOW, Color.BLUE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            swipeRefreshLayout.setColorSchemeColors(context.getColor(R.color.my_color1),
                    context.getColor(R.color.my_color2), context.getColor(R.color.my_color3));
        } else {
            swipeRefreshLayout.setColorSchemeColors(ContextCompat.getColor(context, R.color.my_color1),
                    ContextCompat.getColor(context, R.color.my_color2), ContextCompat.getColor(context, R.color.my_color3));
        }

    }

    public static void startWobble(Activity activity, EditText editText) {
        YoYo.with(Techniques.Wobble)
                .duration(400)
                .playOn(editText);
//        Utils.getInstance().vibrate(activity, 35);
    }

    public static void startWobble(Context activity, TextView textView) {
        YoYo.with(Techniques.Wobble)
                .duration(400)
                .playOn(textView);
//        Utils.getInstance().vibrate(activity, 35);
    }

    public static void startWobble(Context activity, View view) {
        YoYo.with(Techniques.Wobble)
                .duration(400)
                .playOn(view);
//        Utils.getInstance().vibrate(activity, 35);
    }

    public static void startWobble(Context context, EditText editText) {
        YoYo.with(Techniques.Wobble)
                .duration(400)
                .playOn(editText);
//        Utils.getInstance().vibrate(context, 35);
    }

    /**
     * method is used for checking valid email id format.
     *
     * @param email
     * @return boolean true for valid false for invalid
     */
    public static boolean isEmailValid(String email) {
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public static void clearAllErrors(ViewGroup view) {
        for (int i = 0; i < view.getChildCount(); i++) {
            View v = view.getChildAt(i);

            if (v instanceof EditText) {
                // Do something
                ((EditText) v).setError(null);
            } else if (v instanceof ViewGroup) {

                clearAllErrors((ViewGroup) v);
            }
        }
    }

    public void vibrate(Activity activity, int milliSec) {
        Vibrator v = (Vibrator) activity.getSystemService(Context.VIBRATOR_SERVICE);
// Vibrate for 500 milliseconds
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            //deprecated in API 26
            v.vibrate(milliSec);
        }
    }

    public AlertDialog pleaseWait(Activity activity) {
        AlertDialog spotsDialog = new SpotsDialog.Builder()
                .setContext(activity)
                .setCancelable(true)
//                .setCancelable(false)
                .setMessage("Please wait ...")
                .build();
        return spotsDialog;
    }

    public boolean isValidEmail(String target) {
        //return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
        return (Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

    public GoogleSignInClient initGoogleSignInClient(Context context) {
        GoogleSignInClient googleSignInClient;
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(context.getString(R.string.default_web_client_id))
//                .requestIdToken("764111094900-6ut82vlc5pg34clg74cl7bs3nlntf88i.apps.googleusercontent.com")
                .requestEmail()
                .build();

//        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//                .requestEmail()
//                .build();

        googleSignInClient = GoogleSignIn.getClient(context, gso);
        return googleSignInClient;
    }

    public void signOutFromGoogleFacebook(Context context) {
//        LoginManager.getInstance().logOut();
        FirebaseAuth.getInstance().signOut();
        Utils.getInstance().initGoogleSignInClient(context).signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(context, "Google signed out", Toast.LENGTH_SHORT).show();
            }
        });

    }

    static Cryptore getCryptore(Context context, String alias) throws Exception {
        Cryptore.Builder builder = new Cryptore.Builder(alias, CipherAlgorithm.RSA);
        builder.setContext(context); //Need Only RSA on below API Lv22.
//    builder.setBlockMode(BlockMode.ECB); //If Needed.
//    builder.setEncryptionPadding(EncryptionPadding.RSA_PKCS1); //If Needed.
        return builder.build();
    }

    public static String encrypt(String plainStr) throws Exception {
        byte[] plainByte = plainStr.getBytes();
        EncryptResult result = getCryptore(context, Constants.PASSWORD_KEY).encrypt(plainByte);
        return Base64.encodeToString(result.getBytes(), Base64.DEFAULT);
    }

    public static String decrypt(String encryptedStr) throws Exception {
        byte[] encryptedByte = Base64.decode(encryptedStr, Base64.DEFAULT);
        DecryptResult result = getCryptore(context, Constants.PASSWORD_KEY).decrypt(encryptedByte, null);
        return new String(result.getBytes());
    }


    public static byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }


}

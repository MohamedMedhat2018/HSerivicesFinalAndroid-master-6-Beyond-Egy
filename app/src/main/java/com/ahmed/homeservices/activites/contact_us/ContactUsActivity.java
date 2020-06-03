package com.ahmed.homeservices.activites.contact_us;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.ahmed.homeservices.R;
import com.ahmed.homeservices.utils.Utils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ContactUsActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "ContactUsActivity";
    @BindView(R.id.tvGmail)
    TextView tvGmail;

    @OnClick(R.id.ivBack)
    public void onBackClicked(View v) {
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        Utils.fullScreen(getApplicationContext());
        Utils.fullScreen(this);

        setContentView(R.layout.fragment_contact_us);

        ButterKnife.bind(this);


//        findViewById(R.id.tvCallPhone).setOnClickListener(this);
//        findViewById(R.id.ivGmail).setOnClickListener(this);
//
//        findViewById(R.id.ivFacebook).setOnClickListener(this);
//        findViewById(R.id.ivGPlus).setOnClickListener(this);
//        findViewById(R.id.ivTwitter).setOnClickListener(this);
//        findViewById(R.id.ivSnabchat).setOnClickListener(this);
//        findViewById(R.id.ivInsat).setOnClickListener(this);
//        tvGmail =findViewById(R.id.tvGmail);
//        tvGmail.setOnClickListener(this);

    }

    @OnClick(R.id.tvCallPhone)
    public void callPhone() {

        Intent callIntent = new Intent(Intent.ACTION_CALL); //use ACTION_CALL class
        callIntent.setData(Uri.parse(getResources().getString(R.string.contact_us_phone_num).trim()));    //this is the sms number calling
        //check permission
        //If the device is running Android 6.0 (API level 23) and the app's targetSdkVersion is 23 or higher,
        //the system asks the user to grant approval.
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            //request permission from user if the app hasn't got the required permission
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CALL_PHONE},   //request specific permission from user
                    10);
            return;
        } else {     //have got permission
            try {
                startActivity(callIntent);  //call activity and make sms call
            } catch (android.content.ActivityNotFoundException ex) {
                Toast.makeText(getApplicationContext(), "yourActivity is not founded", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @OnClick(R.id.ivGmail)
    public void sendEmail() {
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                "mailto", tvGmail.getText().toString(), null));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Report message");
//        emailIntent.putExtra(Intent.EXTRA_TEXT, tvGmail.getText().toString());
        startActivity(Intent.createChooser(emailIntent, "Send email..."));
    }

    @OnClick(R.id.ivFacebook)
    public void getOpenFacebook() {

        try {
//            context.getPackageManager().getPackageInfo("com.facebook.katana", 0);
            Log.e(TAG, "getOpenFacebook: ");
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/Seyanah.UAE/"));
            startActivity(intent);
        } catch (Exception e) {
            new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com"));
        }
    }

    @OnClick(R.id.ivGPlus)
    public void getOpenGPlus() {

        try {
//            context.getPackageManager().getPackageInfo("com.facebook.katana", 0);
            Log.e(TAG, "getOpenYoutube: ");
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/channel/UCaFKwaGVGd_sTXL3fGz3SEg/featured?disable_polymer=true"));
            startActivity(intent);
        } catch (Exception e) {
            new Intent(Intent.ACTION_VIEW, Uri.parse("https://plus.google.com"));
        }
    }

    @OnClick(R.id.ivTwitter)
    public void getOpenTwitter() {

        try {
//            context.getPackageManager().getPackageInfo("com.facebook.katana", 0);
            Log.e(TAG, "getOpenTwitter: ");
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/SeyanahU"));
            startActivity(intent);
        } catch (Exception e) {
            new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com"));
        }
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case (R.id.ivCallPhone):
                callPhone();
                Log.e(TAG, "onClick:   ");
                break;

            case (R.id.tvCallPhone):
                callPhone();
                break;
            case (R.id.tvGmail):
                sendEmail();
                Log.e(TAG, "onClick:");
                break;

            case (R.id.ivFacebook):
                getOpenFacebook();
                Log.e(TAG, "onClick: m4 clickable ");
                break;

            case (R.id.ivGPlus):
                getOpenGPlus();
                break;

            case (R.id.ivTwitter):
                getOpenTwitter();
                break;

            case (R.id.ivSnabchat):
                getOpenSnap();
                break;

            case (R.id.ivInsat):
                getOpenInst();
                break;

        }
    }

    @OnClick(R.id.ivInsat)
    public void getOpenInst() {
        try {
//            context.getPackageManager().getPackageInfo("com.facebook.katana", 0);
            Log.e(TAG, "getOpenInsat: ");
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.instagram.com/justcall_uae/"));
            startActivity(intent);
        } catch (Exception e) {
            new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.instagram.com/justcall_uae/"));
        }
    }

    @OnClick(R.id.ivSnabchat)
    public void getOpenSnap() {

        Log.e(TAG, "getOpenSnap: ");
        try {
//            context.getPackageManager().getPackageInfo("com.facebook.katana", 0);
            Log.e(TAG, "getOpenSnap: ");
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://accounts.snapchat.com/accounts/welcome"));
            startActivity(intent);
        } catch (Exception e) {
            new Intent(Intent.ACTION_VIEW, Uri.parse("https://plus.google.com"));
        }
    }


}

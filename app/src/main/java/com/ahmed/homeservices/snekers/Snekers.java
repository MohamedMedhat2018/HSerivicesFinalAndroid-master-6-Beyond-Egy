package com.ahmed.homeservices.snekers;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.ahmed.homeservices.R;
import com.irozon.sneaker.Sneaker;

//correctional design patter and SingleTone
public class Snekers {
    private static Snekers ourInstance = null;

    public static Snekers getInstance() {
        if (ourInstance == null) {
            ourInstance = new Snekers();
        }
        return ourInstance;
    }

    private Snekers() {
    }

    public void error(String msg, Activity context) {
//        Sneaker.with(context) // Activity, Fragment or ViewGroup
//                //.setTitle("Error!!")
//                .setMessage(msg)
////                .setDuration(3000)
//                //.setDuration(Toast.LENGTH_LONG)
////                .setDuration(Toast.LENGTH_SHORT)
//                .sneakError();


//        Sneaker.with(context) // Activity, Fragment or ViewGroup
////                .setTitle("Title", R.color.white) // Title and title color
//                .setMessage(msg, R.color.white) // Message and message color
////                .setDuration(4000) // Time duration to show
//                .autoHide(true) // Auto hide Sneaker view
//                .setHeight(ViewGroup.LayoutParams.WRAP_CONTENT) // Height of the Sneaker layout
//                .setIcon(R.drawable.error_white, R.color.white, false) // Icon, icon tint color and circular icon view
////                .setIcon(R.drawable.error_white, 0, false) // Icon, icon tint color and circular icon view
////                .setTypeface(Typeface.createFromAsset(context.getAssets(), "font/" + fontName)) // Custom font for title and message
////                .setOnSneakerClickListener(this) // Click listener for Sneaker
////                .setOnSneakerDismissListener(this) // Dismiss listener for Sneaker. - Version 1.0.2
////                .setCornerRadius(radius, margin) // Radius and margin for round corner Sneaker. - Version 1.0.2
//                .sneak(R.color.grey_205); // Sneak with background color


        //Inflating the view
        Sneaker sneaker = Sneaker.with(context); // Activity, Fragment or ViewGroup
        View view = LayoutInflater.from(context)
                .inflate(R.layout.sneaker_custom_view,
                        sneaker.getView(),
                        false);

        //Access views
        // Your custom view code
//        view.findViewById<TextView>(R.id.tvInstall).setOnClickListener{
//            Toast.makeText(this, "Clicked", Toast.LENGTH_SHORT).show()
//        }
        TextView tvError = view.findViewById(R.id.tvError);
        tvError.setText(msg);


        //show th sneaker
        sneaker.sneakCustom(view);

    }

    public void error(Activity context, String msg) {
        Sneaker.with(context) // Activity, Fragment or ViewGroup
                //.setTitle("Error!!")
                .setMessage(msg)
                //.setDuration(Toast.LENGTH_LONG)
                //.setDuration(Toast.LENGTH_SHORT)
                .sneakError();
    }

}

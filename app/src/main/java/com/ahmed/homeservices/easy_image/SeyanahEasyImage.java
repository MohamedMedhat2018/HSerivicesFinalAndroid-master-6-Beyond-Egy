package com.ahmed.homeservices.easy_image;

import android.app.Activity;

import androidx.fragment.app.Fragment;

import pl.aprilapps.easyphotopicker.EasyImage;

public final class SeyanahEasyImage {

    static Activity activity;


    public static EasyImage easyImage(Activity activity) {
        EasyImage easyImage = new EasyImage.Builder(activity)

// Chooser only
// Will appear as a system chooser title, DEFAULT empty string
//.setChooserTitle("Pick media")
// Will tell chooser that it should show documents or gallery apps
//.setChooserType(ChooserType.CAMERA_AND_DOCUMENTS)  you can use this or the one below
//.setChooserType(ChooserType.CAMERA_AND_GALLERY)

// Setting to true will cause taken pictures to show up in the device gallery, DEFAULT false
                .setCopyImagesToPublicGalleryFolder(false)
// Sets the name for images stored if setCopyImagesToPublicGalleryFolder = true
                .setFolderName("SeyanahEasyImageFolder")

// Allow multiple picking
                .allowMultiple(false)
                .build();
        return easyImage;

    }

    public static void openCamera(Activity activity) {
        SeyanahEasyImage.activity = activity;
        SeyanahEasyImage.easyImage(activity).openCameraForImage(activity);
    }

    public static void openGallery(Activity activity) {
        SeyanahEasyImage.activity = activity;
        SeyanahEasyImage.easyImage(activity).openGallery(activity);
    }


    public static void openGallery(Activity activity, Fragment fragment) {
        EasyImage easyImage = new EasyImage.Builder(activity)
                .setCopyImagesToPublicGalleryFolder(false)
                .setFolderName("SeyanahEasyImageFolder")
                .allowMultiple(false)
                .build();
        easyImage.openGallery(fragment);
    }
}

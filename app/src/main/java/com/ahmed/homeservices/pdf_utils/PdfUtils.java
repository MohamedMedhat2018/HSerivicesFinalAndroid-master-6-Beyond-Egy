package com.ahmed.homeservices.pdf_utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.vincent.filepicker.Constant;
import com.vincent.filepicker.activity.NormalFilePickActivity;

import java.util.UUID;


public final class PdfUtils {


    private static final String TAG = "PdfUtils";

    public static void pdfFetchFile(Activity activity) {
        Intent intent = new Intent(activity, NormalFilePickActivity.class);
        intent.putExtra(Constant.MAX_NUMBER, 1);
//        intent.putExtra(NormalFilePickActivity.SUFFIX, new String[] {"xlsx", "xls", "doc", "docx", "ppt", "pptx", "pdf"});
        intent.putExtra(NormalFilePickActivity.SUFFIX, new String[]{"pdf"});
//        intent.putExtra(NormalFilePickActivity.SUFFIX, new String[]{"pdf", "png", "jpg", "jpeg"});


        activity.startActivityForResult(intent, Constant.REQUEST_CODE_PICK_FILE);


//        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//        intent.setType("application/pdf");
//        try {
//            activity.startActivityForResult(intent, Constant.REQUEST_CODE_PICK_FILE);
//        } catch (ActivityNotFoundException e) {
//            //alert user that file manager not working
////            Toast.makeText(activity, R.string.toast_pick_file_error, Toast.LENGTH_SHORT).show();
//        }


    }

//    public static class pdfFileViewer extends PDFView {
//
//        /**
//         * Construct the initial view
//         *
//         * @param context
//         * @param set
//         */
//        public pdfFileViewer(Context context, AttributeSet set, File file) {
//            super(context, set);
//            this.fromFile(file);
//        }
//
//        public pdfFileViewer(Context context, AttributeSet set, Uri uri) {
//            super(context, set);
//            this.fromUri(uri);
//        }
//
//    }


    public static void pdfUploadFile(Context context, Uri filePath, OnPdfUploadingResponse response) {


        if (filePath != null) {

            //Firebase
            FirebaseStorage storage;
            StorageReference storageReference = null;


            storage = FirebaseStorage.getInstance();
            storageReference = storage.getReference();

            final ProgressDialog progressDialog = new ProgressDialog(context);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            StorageReference ref = storageReference.child("pdf_files/" + UUID.randomUUID().toString());
            ref.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
//                            Toast.makeText(context, "Uploaded", Toast.LENGTH_SHORT).show();
                            response.onUploadSuccess("Uploaded");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            String err = "Failed " + e.getMessage();
//                            Toast.makeText(context, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            response.onUploadError(e, err);
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage("Uploaded " + (int) progress + "%");
                        }
                    });
        }


    }

    public interface OnPdfUploadingResponse {
        void onUploadSuccess(String success);

        void onUploadError(Exception e, String err);
    }

    public static void openPdfFile() {
//        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/example.pdf");
//        Intent intent = new Intent(Intent.ACTION_VIEW);
//        intent.setDataAndType(Uri.fromFile(file), "application/pdf");
//        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
//        startActivity(intent);
    }

}

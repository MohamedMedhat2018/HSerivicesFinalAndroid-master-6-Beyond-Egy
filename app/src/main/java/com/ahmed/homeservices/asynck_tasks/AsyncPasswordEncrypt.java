package com.ahmed.homeservices.asynck_tasks;

import android.os.AsyncTask;

import com.ahmed.homeservices.interfaces.password.OnPasswordEncrypted;
import com.ahmed.homeservices.utils.Utils;

public class AsyncPasswordEncrypt extends AsyncTask<String, Void, String> {

    //        String  decWorkerPassword;
    private String passwordToEncrypt;
    private OnPasswordEncrypted onPasswordEncrypted;

//    public AsyncPasswordEncrypt(String passwordToEncrypt) {
//        this.passwordToEncrypt = passwordToEncrypt;
//    }

    public AsyncPasswordEncrypt(OnPasswordEncrypted onPasswordEncrypted) {
        this.onPasswordEncrypted = onPasswordEncrypted;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... strings) {
//        try {
////            decWorkerPassword = Utils.decrypt(cmWorker.getWorkerPassword().trim());
//           return  Utils.encrypt(strings[0]);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
////        return decWorkerPassword;

        String passwordToEncrypt = "";

        try {
            passwordToEncrypt = Utils.encrypt(strings[0]);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return passwordToEncrypt;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
//        Log.e(TAG, "teeeet" + s);
//            loginAfterPasswordDecrption(s);
        onPasswordEncrypted.onPasswordEncrypted(s);
    }
}

package com.ahmed.homeservices.asynck_tasks;

import android.os.AsyncTask;

import com.ahmed.homeservices.interfaces.password.OnPasswordDecrypted;
import com.ahmed.homeservices.utils.Utils;

public class AsyncPasswordDecrypt extends AsyncTask<String, Void, String> {

    //        String  decWorkerPassword;
    private String passwordTDecrypt;
    private OnPasswordDecrypted onPasswordDecrypted;

//    public AsyncPasswordDecrypt(String passwordTDecrypt) {
//        this.passwordTDecrypt = passwordTDecrypt;
//    }

    public AsyncPasswordDecrypt(OnPasswordDecrypted onPasswordDecrypted) {
        this.onPasswordDecrypted = onPasswordDecrypted;
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

        String passwordTDecrypt = "";

        try {
            passwordTDecrypt = Utils.decrypt(strings[0]);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return passwordTDecrypt;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
//        Log.e(TAG, "teeeet" + s);
//            loginAfterPasswordDecrption(s);
        onPasswordDecrypted.onPasswordDecrypted(s);
    }
}

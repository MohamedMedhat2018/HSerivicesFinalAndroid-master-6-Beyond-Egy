package com.ahmed.homeservices.fragments;


import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.ahmed.homeservices.BuildConfig;
import com.ahmed.homeservices.R;
import com.ahmed.homeservices.activites.cutomer_or_worker.CustomerOrWorkerActivity;
import com.ahmed.homeservices.activites.lang.LanguageActivity;
import com.ahmed.homeservices.common.Common;
import com.ahmed.homeservices.constants.Constants;
import com.ahmed.homeservices.delete_account.DeleteAccount;
import com.ahmed.homeservices.interfaces.OnLoggedOut;
import com.ahmed.homeservices.utils.Utils;
import com.developer.kalert.KAlertDialog;
import com.github.rubensousa.bottomsheetbuilder.BottomSheetBuilder;
import com.github.rubensousa.bottomsheetbuilder.BottomSheetMenuDialog;
import com.pixplicity.easyprefs.library.Prefs;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class FragmentMore extends Fragment {

    private static final String TAG = "FragmentMore";
    //    @BindView(R.id.tvLogOut)
//    TextView tvLogOut;
    @BindView(R.id.upgrate_to_premium)
    LinearLayout upgrate_to_premium;
    @BindView(R.id.ll_app_policy)
    LinearLayout ll_app_policy;
    @BindView(R.id.ll_contact_us)
    LinearLayout ll_contact_us;
    @BindView(R.id.ll_share_app)
    LinearLayout ll_share_app;
    @BindView(R.id.ll_settings)
    LinearLayout ll_settings;
    @BindView(R.id.ll_log_out)
    LinearLayout ll_log_out;
    private AlertDialog spotsDialog;
    private BottomSheetMenuDialog dialog = null;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_more, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initLogOutProtocol();
    }

    private void initLogOutProtocol() {
        if (Prefs.getPreferences().contains(Constants.FIREBASE_UID)) {
            ll_log_out.setVisibility(View.VISIBLE);
            ll_log_out.setOnClickListener(view -> {

//                deleteOrLogOut();
                logout();
            });
        } else {
            ll_log_out.setVisibility(View.GONE);
        }

    }

    private void deleteOrLogOut() {
        dialog = new BottomSheetBuilder(getActivity(), null)
                .setMode(BottomSheetBuilder.MODE_LIST)
//                .setMode(BottomSheetBuilder.MODE_GRID)
                .addDividerItem()
                .expandOnStart(true)
                .setDividerBackground(R.color.grey_201)
                .setBackground(R.drawable.ripple_grey)
                .setMenu(R.menu.menu_delete_or_logout_confirmation)
                .setItemClickListener(item -> {
                    switch (item.getItemId()) {
                        case R.id.logOut:
                            logout();
                            Log.e(TAG, "logOut: logOut ");
                            break;
                        case R.id.delete:
                            KAlertDialog pDialog = new KAlertDialog(getActivity(), KAlertDialog.SUCCESS_TYPE)
                                    .setTitleText(getString(R.string.are_you_sure))
                                    .setContentText(getString(R.string.are_you_sure2))
                                    .setConfirmText(getString(R.string.yes))
                                    .setConfirmClickListener(new KAlertDialog.KAlertClickListener() {
                                        @Override
                                        public void onClick(KAlertDialog kAlertDialog) {

                                            spotsDialog = Utils.getInstance().pleaseWait(getActivity());
                                            spotsDialog.setMessage(getString(R.string.delete_account_progress));
                                            spotsDialog.setCanceledOnTouchOutside(false);
                                            spotsDialog.setCancelable(false);
                                            spotsDialog.show();

                                            DeleteAccount.cloneRefToDeletedRequests(Prefs.getString(Constants.FIREBASE_UID, ""), new DeleteAccount.OnDeletionAccountSuccess() {
                                                @Override
                                                public void onDeletedSuccess(DeleteAccount.DeletedSuccess deletedSuccess) {
                                                    spotsDialog.dismiss();
                                                    kAlertDialog.dismissWithAnimation();
                                                    Prefs.edit().remove(Constants.FIREBASE_UID).apply();
                                                    Objects.requireNonNull(getActivity()).finish();
                                                    getActivity().startActivity(new Intent().setClass(getActivity(), LanguageActivity.class));
                                                }

                                                @Override
                                                public void onDeletedFailed(Exception e) {

                                                }
                                            });
                                        }
                                    })
                                    .setCancelText(getString(R.string.no))
                                    .setCancelClickListener(new KAlertDialog.KAlertClickListener() {
                                        @Override
                                        public void onClick(KAlertDialog sDialog) {
                                            sDialog.dismissWithAnimation();
                                        }
                                    });

                            pDialog.setCanceledOnTouchOutside(true);
                            pDialog.setCancelable(true);
                            pDialog.show();

                            Log.e(TAG, "delete: ");
                            break;
//                        case R.id.removePhoto:
//                            removePhoto();
//                            break;
                    }
                })
                .createDialog();
        dialog.show();
    }

    private void logout() {
        spotsDialog = Utils.getInstance().pleaseWait(getActivity());
        spotsDialog.setMessage(getString(R.string.logging_out));
        spotsDialog.setCanceledOnTouchOutside(false);
        spotsDialog.setCancelable(false);
        spotsDialog.show();
        Common.logout(new OnLoggedOut() {
            @Override
            public void onLoggedOutSuccess() {
                spotsDialog.dismiss();
                Objects.requireNonNull(getActivity()).finish();
                startActivity(new Intent(getActivity(), CustomerOrWorkerActivity.class));
            }

            @Override
            public void onLoggedOutFailed() {

            }
        });
    }

    @OnClick(R.id.upgrate_to_premium)
    void upgrate_to_premium() {

//        Intent intent = new Intent(getContext(), ContactUsActivity.class);
//        startActivity(intent);

    }

    @OnClick(R.id.ll_share_app)
    void shareApp() {
        try {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "My application name");
            String shareMessage = "\nLet me recommend you this application\n\n";
            shareMessage = shareMessage + "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID + "\n\n";
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
            startActivity(Intent.createChooser(shareIntent, "choose one"));
        } catch (Exception e) {
            //e.toString();
        }
    }

    @OnClick(R.id.ll_app_policy)
    void appPolicy() {
        startActivity(Constants.POLICY_LINK);
    }

    @OnClick(R.id.ll_contact_us)
    void contactUs() {

        startActivity(Constants.CONTACT_US_LINK);
    }

    @OnClick(R.id.ll_about_us)
    void aboutUs() {
        startActivity(Constants.ABOUT_US_LINK);
    }

    private void startActivity(String Link) {
        String link = Link;
        if (!link.startsWith("http://") && !link.startsWith("https://")) {
            link = "http://" + link;
        }
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
        startActivity(browserIntent);
    }

    @OnClick(R.id.ll_settings)
    void settings() {

        Prefs.edit().remove(Constants.SELECTED_LANG).apply();
        Intent intentSettings = new Intent(getContext(), LanguageActivity.class);
        startActivity(intentSettings);
        getActivity().finish();
    }

}
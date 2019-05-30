package com.example.dictionary.View;

import android.Manifest;
import android.accounts.AccountManager;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.dictionary.Model.RoomDB.Entity.SearchType;
import com.example.dictionary.Model.RoomDB.Entity.Video;
import com.example.dictionary.Presenter.YoutubePresenter;
import com.example.dictionary.Presenter.YoutubePresenterImpl;
import com.example.dictionary.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;

import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

import static com.example.dictionary.Model.YoutubeDataApi.Statics.NEED_GOOGLE_AUTH;
import static com.example.dictionary.Model.YoutubeDataApi.Statics.PREF_ACCOUNT_NAME;
import static com.example.dictionary.Model.YoutubeDataApi.Statics.REQUEST_ACCOUNT_PICKER;
import static com.example.dictionary.Model.YoutubeDataApi.Statics.REQUEST_AUTHORIZATION;
import static com.example.dictionary.Model.YoutubeDataApi.Statics.REQUEST_GOOGLE_PLAY_SERVICES;
import static com.example.dictionary.Model.YoutubeDataApi.Statics.REQUEST_PERMISSION_GET_ACCOUNTS;

public class YoutubeLoginActivity extends AppCompatActivity implements YoutubePresenter.View {
    YoutubePresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_youtube_login);
        presenter = new YoutubePresenterImpl(this);
        presenter.setView(this);
        presenter.setModelPresenter();
        ((YoutubePresenterImpl)presenter).initCredential(getApplicationContext());
        ((YoutubePresenterImpl)presenter).setExternalFilesDir(getExternalFilesDir(null));
        checkCredential();
        if (((YoutubePresenterImpl)presenter).checkCredential() == true) {
            presenter.videoSearch("test", SearchType.WebOnly);
        }
    }

    private void checkCredential() {
        if (((YoutubePresenterImpl)presenter).checkCredential() == false) {
            if (isGooglePlayServicesAvailable() == false) {
                acquireGooglePlayServices();
            }
            if (((YoutubePresenterImpl)presenter).isSelectedAccountNameNull()) {
                chooseAccount();
            }
        } else {
            setResult(RESULT_OK);
            finish();
        }
    }

    private boolean isGooglePlayServicesAvailable() {
        GoogleApiAvailability apiAvailability =
                GoogleApiAvailability.getInstance();
        final int connectionStatusCode =
                apiAvailability.isGooglePlayServicesAvailable(this);
        return connectionStatusCode == ConnectionResult.SUCCESS;
    }

    private void acquireGooglePlayServices() {
        GoogleApiAvailability apiAvailability =
                GoogleApiAvailability.getInstance();
        final int connectionStatusCode =
                apiAvailability.isGooglePlayServicesAvailable(this);
        if (apiAvailability.isUserResolvableError(connectionStatusCode)) {
            showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode);
        }
    }

    public void showGooglePlayServicesAvailabilityErrorDialog(
            final int connectionStatusCode) {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        Dialog dialog = apiAvailability.getErrorDialog(
                YoutubeLoginActivity.this,
                connectionStatusCode,
                REQUEST_GOOGLE_PLAY_SERVICES);
        dialog.show();
    }

    @AfterPermissionGranted(REQUEST_PERMISSION_GET_ACCOUNTS)
    private void chooseAccount() {
        if (EasyPermissions.hasPermissions(
                this, Manifest.permission.GET_ACCOUNTS)) {
            String accountName = getPreferences(Context.MODE_PRIVATE)
                    .getString(PREF_ACCOUNT_NAME, null);
            if (accountName != null) {
                ((YoutubePresenterImpl)presenter).getCredential().setSelectedAccountName(accountName);

            } else {
                // Start a dialog from which the user can choose an account
                startActivityForResult(
                        ((YoutubePresenterImpl)presenter).getCredential().newChooseAccountIntent(),
                        REQUEST_ACCOUNT_PICKER);
            }
        } else {
            // Request the GET_ACCOUNTS permission via a user dialog
            EasyPermissions.requestPermissions(
                    this,
                    "This app needs to access your Google account (via Contacts).",
                    REQUEST_PERMISSION_GET_ACCOUNTS,
                    Manifest.permission.GET_ACCOUNTS);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case REQUEST_ACCOUNT_PICKER:
                if (resultCode == RESULT_OK && data != null &&
                        data.getExtras() != null) {
                    String accountName =
                            data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                    if (accountName != null) {
                        SharedPreferences settings =
                                getPreferences(Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString(PREF_ACCOUNT_NAME, accountName);
                        editor.apply();
                        ((YoutubePresenterImpl)presenter).getCredential()
                                .setSelectedAccountName(accountName);
                    }
                }
                checkCredential();
                break;
            case REQUEST_GOOGLE_PLAY_SERVICES:
                if (resultCode != RESULT_OK) {
                    Toast.makeText(getApplicationContext(),
                            "구글 플레이 서비스가 필요합니다.",
                            Toast.LENGTH_SHORT).show();
                }
                checkCredential();
                break;
            case REQUEST_AUTHORIZATION:
                checkCredential();
                break;
        }
    }

    //region ignore
    @Override
    public void setVideo(Video video) {
    }

    @Override
    public void pushResultMessage(String msg) {

    }

    @Override
    public void addVideoQueryResultList(List<Video> videos) {
        if (videos != null) {
            setResult(RESULT_OK);
            finish();
        }
    }
    //endregion
}

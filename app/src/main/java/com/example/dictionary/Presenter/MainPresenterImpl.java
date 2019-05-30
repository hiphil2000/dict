package com.example.dictionary.Presenter;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.preference.PreferenceManager;

import com.example.dictionary.Model.RoomDB.Entity.Video;
import com.example.dictionary.Model.RoomDB.Entity.Word;
import com.example.dictionary.Model.RoomDB.Model;
import com.example.dictionary.Model.TwinWordDict.TwinWordDictModel;
import com.example.dictionary.Model.YoutubeDataApi.YoutubeDataApiModel;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;

import java.util.ArrayList;
import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

import static com.example.dictionary.Model.YoutubeDataApi.YoutubeDataApiModel.PREF_ACCOUNT_NAME;
import static com.example.dictionary.Model.YoutubeDataApi.YoutubeDataApiModel.REQUEST_ACCOUNT_PICKER;
import static com.example.dictionary.Model.YoutubeDataApi.YoutubeDataApiModel.REQUEST_GOOGLE_PLAY_SERVICES;
import static com.example.dictionary.Model.YoutubeDataApi.YoutubeDataApiModel.REQUEST_PERMISSION_GET_ACCOUNTS;

public class MainPresenterImpl implements MainPresenter {
    private Activity activity;
    private MainPresenter.View view;
    private TwinWordDictModel dictModel;
    private YoutubeDataApiModel youtubeModel;
    private Model DBModel;
    private Handler mainHandler;

    public MainPresenterImpl(Activity activity) {
        this.activity = activity;
        this.dictModel = new TwinWordDictModel(activity.getApplicationContext());
        this.DBModel = new Model(activity.getApplicationContext());
        this.mainHandler = new Handler(activity.getMainLooper());
        this.youtubeModel = YoutubeDataApiModel.getInstance();
    }


    //region 공통
    public void getDataFromApi(String... params) {
        if (! isGooglePlayServicesAvailable()) {
            acquireGooglePlayServices(params);
            return;
        } else if (youtubeModel.getCredential().getSelectedAccountName() == null) {
            chooseAccount(params);
            return;
        } else if (! isDeviceOnline()) {
            view.pushResultMessage("No network connection available");
        } else {
            if (params != null)
                if (params.length > 0) {
                    switch(params[0]) {
                        case "queryVideo":
                            youtubeModel.queryVideo(params[1], Long.parseLong(params[2]));
                            break;
                        case "fullVideo":
                            youtubeModel.getVideoWithContents(params[1]);
                            break;
                    }
                }
        }
    }

    private boolean isGooglePlayServicesAvailable() {
        GoogleApiAvailability apiAvailability =
                GoogleApiAvailability.getInstance();
        final int connectionStatusCode =
                apiAvailability.isGooglePlayServicesAvailable(activity.getApplicationContext());
        return connectionStatusCode == ConnectionResult.SUCCESS;
    }

    private void acquireGooglePlayServices(String... params) {
        GoogleApiAvailability apiAvailability =
                GoogleApiAvailability.getInstance();
        final int connectionStatusCode =
                apiAvailability.isGooglePlayServicesAvailable(activity.getApplicationContext());
        if (apiAvailability.isUserResolvableError(connectionStatusCode)) {
            showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode);
            getDataFromApi(params);
        }
    }

    void showGooglePlayServicesAvailabilityErrorDialog(final int connectionStatusCode) {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        Dialog dialog = apiAvailability.getErrorDialog(
                activity,
                connectionStatusCode,
                REQUEST_GOOGLE_PLAY_SERVICES);
        dialog.show();
    }

    private boolean isDeviceOnline() {
        ConnectivityManager connMgr =
                (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    @AfterPermissionGranted(REQUEST_PERMISSION_GET_ACCOUNTS)
    private void chooseAccount(String... params) {
        if (EasyPermissions.hasPermissions(
                activity.getApplicationContext(),
                Manifest.permission.GET_ACCOUNTS)) {
            String accountName = activity.getPreferences(Context.MODE_PRIVATE)
                    .getString(PREF_ACCOUNT_NAME, null);
            if (accountName != null) {
                youtubeModel.getCredential().setSelectedAccountName(accountName);
                getDataFromApi(params);
            } else {
                // Start a dialog from which the user can choose an account
                activity.startActivityForResult(
                        youtubeModel.getCredential().newChooseAccountIntent(),
                        REQUEST_ACCOUNT_PICKER);
            }
        } else {
            // Request the GET_ACCOUNTS permission via a user dialog
            EasyPermissions.requestPermissions(
                    activity,
                    "This app needs to access your Google account (via Contacts).",
                    REQUEST_PERMISSION_GET_ACCOUNTS,
                    Manifest.permission.GET_ACCOUNTS);
        }
    }
    //endregion

    @Override
    public void setView(View view) {
        this.view = view;
    }

    @Override
    public void search(final String query, boolean localOnly) {
        Runnable webrun = new Runnable() {
            @Override
            public void run() {
                try {
                    List<Word> result = new ArrayList<>();
                    result.add(dictModel.DefinitionKR(query.trim()));
                    callback(result, "Search-Web");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        Runnable localrun = new Runnable() {
            @Override
            public void run() {
                try {
                    List<Word> result = new ArrayList<>();
                    result.add(DBModel.GetWord(query.trim()));
                    callback(result, "Search-Local");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        if (!localOnly)
            new Thread(webrun).start();
        new Thread(localrun).start();
    }

    @Override
    public void getViewLogs() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                callback(DBModel.GetLogs(), "Log");
            }
        }).start();
    }

    @Override
    public void getWordViewLogs(final String entryId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                callback(DBModel.GetWordLogs(entryId), "Logs");
            }
        }).start();
    }

    @Override
    public void addToNote(final Word word) {
        Runnable localrun = new Runnable() {
            @Override
            public void run() {
                try {
                    callback(DBModel.AddWord(word));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        new Thread(localrun).start();
    }

    @Override
    public void deleteFromNote(final Word word) {
        Runnable r = new Runnable() {
            @Override
            public void run() {
                try {
                    callback(DBModel.RemoveWord(word));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        new Thread(r).start();
    }

    @Override
    public void showNotes() {
        Runnable r = new Runnable() {
            @Override
            public void run() {
                try{
                    callback(DBModel.GetWords(), "Search");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        new Thread(r).start();
    }

    @Override
    public void nukeNote() {
        Runnable r = new Runnable() {
            @Override
            public void run() {
                try {
                    // DBModel.nuke_table();
                } catch(Exception e) {
                    e.printStackTrace();
                }
            }
        };
        new Thread(r).start();
    }

    @Override
    public void callback(final List<?> listCallback, String callbackType) {
        if (callbackType != null) {
            String type = callbackType.split("-")[0];
            if (type.equals("Search")) {
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        view.addQueryResultList((List<Word>) listCallback);
                    }
                });
                boolean instantDownload = PreferenceManager.getDefaultSharedPreferences(activity.getApplicationContext())
                        .getBoolean("pref_search_download_word", false);

                if (instantDownload) {
                    for (Word word : (List<Word>)listCallback) {
                        addToNote(word);
                    }
                }

            } else if (type.equals("SearchYoutube")) {
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        view.addVideoQueryResultList((List<Video>) listCallback);
                    }
                });

            } else if (type.equals("Log")) {
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        view.addViewLogResultList((List<Word>) listCallback);
                    }
                });
            }
        }
    }

    @Override
    public void callback(final String message) {
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                view.pushResultMessage(message);
            }
        });
    }

    @Override
    public GoogleAccountCredential getCredential() {
        return youtubeModel.getCredential();
    }

    public Activity getActivity() {
        return activity;
    }

    @Override
    public void dataFromModel(Video video, String callbackType) {

    }

    @Override
    public void dataFromModel(List<Video> videoList, String callbackType) {
        callback(videoList, "SearchYoutube");
    }

    @Override
    public void dialogFromModel(String type, Object... params) {

    }

}

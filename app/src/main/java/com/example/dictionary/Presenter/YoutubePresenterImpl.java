package com.example.dictionary.Presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.preference.PreferenceManager;

import com.example.dictionary.Model.RoomDB.Entity.SearchType;
import com.example.dictionary.Model.RoomDB.Entity.Video;
import com.example.dictionary.Model.RoomDB.Entity.Word;
import com.example.dictionary.Model.RoomDB.Model;
import com.example.dictionary.Model.YoutubeDataApi.YoutubeDataApiModel;
import com.example.dictionary.View.YoutubeLoginActivity;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class YoutubePresenterImpl implements YoutubePresenter {
    private YoutubePresenter.View view;
    private YoutubeDataApiModel model;
    private Model DBModel;
    private Activity activity;
    private Handler mainHandler;

    public YoutubePresenterImpl(Activity activity) {
        this.activity = activity;
        this.model = YoutubeDataApiModel.getInstance();
        this.DBModel = new Model(activity);
        this.mainHandler = new Handler(activity.getMainLooper());
    }

    public boolean checkCredential() {
        return model.checkReady();
    }

    public boolean isSelectedAccountNameNull() {
        return model.isSelectedAccountNameNull();
    }

    public void setModelPresenter() {
        model.setPresenter(this);
    }

    @Override
    public void downloadVideo(final String url) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                model.downloadFromUrl(url);
            }
        }).start();
    }

    @Override
    public void removeModelPresenter() {
        model.setPresenter(null);
    }

    @Override
    public void setView(YoutubePresenter.View view) {
        this.view = view;
    }

    @Override
    public void getFullVideo(Video video) {
        getDataFromApi("fullVideo", video.Video_ID);
    }

    @Override
    public void addVideo(final Video video) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                DBModel.AddVideo(video);
            }
        }).start();
    }

    @Override
    public void deleteVideo(Video video) {
        // TODO: 2019-05-27 delvideo 
    }

    @Override
    public void videoSearch(String query, SearchType searchType) {
        final String q = query;
        final String max_result = PreferenceManager.getDefaultSharedPreferences(activity.getApplicationContext())
                                .getString("pref_youtube_max_result", "3");

        Runnable webrun = new Runnable() {
            @Override
            public void run() {
                try {
                    getDataFromApi();
                    model.queryVideoId(q.trim(), Integer.parseInt(max_result));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        Runnable localrun = new Runnable() {
            @Override
            public void run() {
                try {
                    callback(DBModel.SearchVideo("%" + q.trim() + "%"), "SearchYoutube-Local");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        if (searchType == SearchType.LocalOnly || searchType == SearchType.Both)
            new Thread(localrun).start();
        if (searchType == SearchType.WebOnly || searchType == SearchType.Both)
            new Thread(webrun).start();

    }

    @Override
    public void showVideos() {

    }

    public GoogleAccountCredential getCredential() {
        return model.getCredential();
    }

    public void setExternalFilesDir(File dir) {
        model.setExternalFilesDir(dir);
    }

    public void initCredential(Context context) {
        model.initCredential(context);
    }

    //region 공통
    public void getDataFromApi(String... params) {
        if (params != null)
            if (params.length > 0) {
                switch(params[0]) {
                    case "queryVideo":
                        model.queryVideo(params[1], Long.parseLong(params[2]));
                        break;
                    case "fullVideo":
                        model.getVideoWithContents(params[1]);
                        break;
                }
            }
    }
    //endregion

    @Override
    public void dataFromModel(Video video, String callbackType) {
        if (callbackType.equals("idToVideo")) {
            callback(Arrays.asList(new Video[]{video}), "SearchYoutube-Web");
        } else if (callbackType.equals("fullVideo")) {
            view.setVideo(video);
        }

    }

    @Override
    public void dataFromModel(final List<Video> videoList, String callbackType) {

        if (callbackType.equals("queryVideoId")) {
            Runnable localrun = new Runnable() {
                @Override
                public void run() {
                    try {
                        List<Video> result = new ArrayList<>();
                        List<String> vids = new ArrayList<>();
                        for (Video vi_item : videoList) {
                            Video dbResult = DBModel.GetVideo(vi_item.Video_ID);
                            if (dbResult != null) {
                                result.add(dbResult);
                            } else {
                                model.getVideoById(vi_item.Video_ID);
                            }
                        }
                        callback(result, "SearchYoutube-Local");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };
            new Thread(localrun).start();
        } else {
            callback(videoList, "SearchYoutube");
        }

    }

    @Override
    public void dialogFromModel(String type, Object... params) {
        if (type.equals("showGooglePlayServicesAvailabilityErrorDialog")) {
            ((YoutubeLoginActivity)view).showGooglePlayServicesAvailabilityErrorDialog((int)params[0]);
        } else if (type.equals("startActivityForResult")) {
            ((YoutubeLoginActivity)view).startActivityForResult((Intent)params[0], (int)params[1]);
        } else if (type.equals("pushText")) {
            view.pushResultMessage((String)params[0]);
        }
    }

    @Override
    public void callback(final List<?> listCallback, String callbackType) {
        if (callbackType != null) {
            final String type = callbackType.split("-")[0];
            final String from = callbackType.split("-")[1];
            if (type.equals("SearchYoutube")) {
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        List<Video> videos = (List<Video>) listCallback;
                        if (from.equals("Local")) {
                            for(Video vid : videos) {
                                vid.IsLocal = true;
                            }
                        }
                        view.addVideoQueryResultList(videos);
                    }
                });
            }
        }
    }
}

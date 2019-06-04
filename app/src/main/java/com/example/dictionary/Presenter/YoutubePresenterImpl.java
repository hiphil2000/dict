package com.example.dictionary.Presenter;

import android.app.Activity;
import android.os.Handler;

import com.example.dictionary.Model.RoomDB.Entity.Video;
import com.example.dictionary.Model.RoomDB.Model;
import com.example.dictionary.Model.YoutubeDataApi.YoutubeModel;

import java.util.Arrays;
import java.util.List;

public class YoutubePresenterImpl implements YoutubePresenter {
    private YoutubePresenter.View view;
    private YoutubeModel model;
    private Model DBModel;
    private Activity activity;
    private Handler mainHandler;

    public YoutubePresenterImpl(Activity activity) {
        this.activity = activity;
        this.model = new YoutubeModel();
        this.DBModel = new Model(activity);
        this.mainHandler = new Handler(activity.getMainLooper());
    }

    @Override
    public void setView(YoutubePresenter.View view) {
        this.view = view;
    }

    @Override
    public void refreshData() {

    }

    @Override
    public void moreData() {

    }

    @Override
    public void searchData(String query) {
        final String q = query;
        new Thread(new Runnable() {
            @Override
            public void run() {
                callback(model.searchVideo(q), "SearchYoutube");
            }
        }).start();
    }

    @Override
    public void getDetail(String video_id) {
        final String q = video_id;
        new Thread(new Runnable() {
            @Override
            public void run() {
                callback(model.getVideoDetails(q), "DetailYoutube");
            }
        }).start();

    }

    @Override
    public void callback(final List<?> listCallback, String callbackType) {
        if (callbackType != null) {
            final String type = callbackType.split("-")[0];
            if (type.equals("SearchYoutube")) {
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        List<Video> videos = (List<Video>) listCallback;
                        view.addVideoQueryResultList(videos);
                    }
                });
            }
        }
    }
    @Override
    public void callback(final Object objCallback, String callbackType) {
        if (callbackType != null) {
            final String type = callbackType.split("-")[0];
            if (type.equals("DetailYoutube")) {
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        view.setVideo((Video)objCallback);
                    }
                });
            }
        }
    }

    // TODO: 2019-05-30 async task
}

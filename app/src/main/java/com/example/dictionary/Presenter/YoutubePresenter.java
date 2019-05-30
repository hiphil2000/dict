package com.example.dictionary.Presenter;

import com.example.dictionary.Model.RoomDB.Entity.SearchType;
import com.example.dictionary.Model.RoomDB.Entity.Video;

import java.util.List;

public interface YoutubePresenter extends YoutubeCallbackable {
    void setView(YoutubePresenter.View view);
    void getFullVideo(Video video);
    void addVideo(Video video);
    void deleteVideo(Video video);
    void videoSearch(String query, SearchType searchType);
    void showVideos();
    void callback(final List<?> listCallback, String callbackType);
    void setModelPresenter();
    void downloadVideo(String url);

    void removeModelPresenter();

    interface View {
        void setVideo(Video video);
        void pushResultMessage(String msg);
        void addVideoQueryResultList(List<Video> videos);
    }
}

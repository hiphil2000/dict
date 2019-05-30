package com.example.dictionary.Presenter;

import com.example.dictionary.Model.RoomDB.Entity.Video;

import java.util.List;

public interface YoutubeCallbackable {
    void dataFromModel(Video video, String callbackType);
    void dataFromModel(List<Video> videoList, String callbackType);
    void dialogFromModel(String type, Object... params);

}

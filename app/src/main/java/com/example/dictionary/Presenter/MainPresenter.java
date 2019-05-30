package com.example.dictionary.Presenter;

import com.example.dictionary.Model.RoomDB.Entity.Log;
import com.example.dictionary.Model.RoomDB.Entity.Video;
import com.example.dictionary.Model.RoomDB.Entity.Word;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;

import java.util.List;

public interface MainPresenter extends YoutubeCallbackable{
    void setView(MainPresenter.View view);
    void search(String query, boolean localOnly);
    void getViewLogs();
    void getWordViewLogs(String entryId);
    void addToNote(Word word);
    void deleteFromNote(Word word);
    void showNotes();
    void nukeNote();
    //void callback(List<WordNote> wordinfos);
    void callback(List<?> listCallback, String callbackType);
    void callback(String message);
    GoogleAccountCredential getCredential();

    interface View {
        void addQueryResultList(List<Word> wordInfos);
        void addVideoQueryResultList(List<Video> videos);
        void addViewLogResultList(List<Word> words);
        void setQueryResultList(List<Word> wordInfos);
        void pushResultMessage(String message);
        void showDialog(String type, Object... params);
    }
}

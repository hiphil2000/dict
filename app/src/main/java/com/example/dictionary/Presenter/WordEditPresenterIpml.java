package com.example.dictionary.Presenter;

import android.app.Activity;

import com.example.dictionary.Model.RoomDB.Entity.Word;
import com.example.dictionary.Model.RoomDB.Model;

public class WordEditPresenterIpml implements WordEditPresenter {
    private Model dbModel;
    private WordEditPresenter.View view;
    private Activity activity;

    public WordEditPresenterIpml(Activity activity) {
        this.activity = activity;
        this.dbModel = new Model(activity.getApplicationContext());
    }

    @Override
    public void setView(View view) {
        this.view = view;
    }

    @Override
    public void commitChanges(Word word) {
        final Word wd = word;
        new Thread(new Runnable() {
            @Override
            public void run() {
                callback(dbModel.CommitChanges(wd));
            }
        }).start();
    }

    @Override
    public void addWord(Word word) {
        final Word wd = word;
        new Thread(new Runnable() {
            @Override
            public void run() {
                callback(dbModel.AddWord(wd));
            }
        }).start();
    }

    @Override
    public void callback(String msg) {
        view.pushResultMessage(msg);
    }
}

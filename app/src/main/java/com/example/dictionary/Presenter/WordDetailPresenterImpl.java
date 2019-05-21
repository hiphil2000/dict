package com.example.dictionary.Presenter;

import android.app.Activity;
import android.os.Handler;

import com.example.dictionary.Model.RoomDB.Entity.Log;
import com.example.dictionary.Model.RoomDB.Entity.LogType;
import com.example.dictionary.Model.RoomDB.Entity.Word;
import com.example.dictionary.Model.RoomDB.Model;

import java.util.ArrayList;
import java.util.Date;

public class WordDetailPresenterImpl implements WordDetailPresenter {
    private WordDetailPresenter.View view;
    private Model dbModel;
    private Activity activity;
    private Handler handler;

    public WordDetailPresenterImpl(Activity activity) {
        this.activity = activity;
        this.dbModel = new Model(activity.getApplicationContext());
        this.handler = new Handler(activity.getMainLooper());
    }

    @Override
    public void setView(View view) {
        this.view = view;
    }

    @Override
    public void writeLog(String entryId, LogType logType) {
        final LogType lt = logType;
        final String entry_id = entryId;
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log log = new Log(new Date(), lt, entry_id);
                dbModel.AddLog(log);
            }
        }).start();
    }

    @Override
    public void getLog(String entryId){
        final String entry_id = entryId;
        new Thread(new Runnable() {
            @Override
            public void run() {
                //ArrayList<Log> logs = (ArrayList<Log>) dbModel.GetLogs(LogType.WordDetail, entry_id);
            }
        }).start();
    }

    @Override
    public void callback(final Word word) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                view.setWord(word);
            }
        });
    }
}

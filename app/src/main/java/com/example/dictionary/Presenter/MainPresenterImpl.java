package com.example.dictionary.Presenter;

import android.app.Activity;
import android.os.Handler;

import com.example.dictionary.Model.RoomDB.Entity.Video;
import com.example.dictionary.Model.RoomDB.Entity.Word;
import com.example.dictionary.Model.RoomDB.Model;
import com.example.dictionary.Model.TwinWordDict.TwinWordDictModel;
import com.example.dictionary.Model.YoutubeDataApi.YoutubeDataApiModel;

import java.util.ArrayList;
import java.util.List;

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
    }

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
    public void videoSearch(String query, boolean localOnly) {
        final String q = query;
        Runnable webrun = new Runnable() {
            @Override
            public void run() {
                try {
                    List<Video> result = new ArrayList<>();
                    //result.addAll(youtubeModel.Search(q.trim()));
                    callback(result, "SearchYoutube-Web");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        Runnable localrun = new Runnable() {
            @Override
            public void run() {
                try {
                    List<Video> result = new ArrayList<>();
                    result.addAll(DBModel.GetVideos(q.trim()));
                    callback(result, "SearchYoutube-Local");
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
    public void addVideo(Video video) {

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
    public void deleteVideo(Video video) {

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
    public void showVideos() {

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

    public Activity getActivity() {
        return activity;
    }
}

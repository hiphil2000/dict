package com.example.dictionary.View;

import androidx.cardview.widget.CardView;

import android.accounts.AccountManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dictionary.Model.RoomDB.Entity.Caption;
import com.example.dictionary.Model.RoomDB.Entity.Snippet;
import com.example.dictionary.Model.RoomDB.Entity.Video;
import com.example.dictionary.Model.RoomDB.Entity.VideoType;
import com.example.dictionary.Presenter.YoutubePresenter;
import com.example.dictionary.Presenter.YoutubePresenterImpl;
import com.example.dictionary.R;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

import java.util.List;

import static com.example.dictionary.Model.YoutubeDataApi.YoutubeDataApiModel.PREF_ACCOUNT_NAME;
import static com.example.dictionary.Model.YoutubeDataApi.YoutubeDataApiModel.REQUEST_ACCOUNT_PICKER;
import static com.example.dictionary.Model.YoutubeDataApi.YoutubeDataApiModel.REQUEST_AUTHORIZATION;
import static com.example.dictionary.Model.YoutubeDataApi.YoutubeDataApiModel.REQUEST_GOOGLE_PLAY_SERVICES;

public class YoutubeActivity extends YouTubeBaseActivity implements YoutubePresenter.View {
    private YouTubePlayerView ytb_view;
    private YouTubePlayer ytb_player;
    private CardView card_caption;

    private Video currentVideo;
    private captionWatcher captionWatcher;
    private YoutubePresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_youtube);

        Intent param = getIntent();
        initActivity();
        presenter = new YoutubePresenterImpl(YoutubeActivity.this);
        presenter.setView(this);
        presenter.setModelPresenter();
        Video video = (Video) param.getSerializableExtra("video");
        presenter.getFullVideo(video);
    }

    private void initActivity() {
        ytb_view = findViewById(R.id.ytbView);
        captionWatcher = new captionWatcher();
        card_caption = findViewById(R.id.card_caption);

    }

    private void initYoutubeView() {
        ytb_view.initialize(getResources().getString(R.string.api_key), init);

    }

    private YouTubePlayer.OnInitializedListener init = new YouTubePlayer.OnInitializedListener() {
        @Override
        public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
            if (currentVideo.Video_Data_Type == VideoType.HyperLink)
                youTubePlayer.loadVideo(currentVideo.Video_Data.split("v=")[1]);
            ytb_player = youTubePlayer;
            if(currentVideo.Captions.size() > 0) {
                captionWatcher.start();
            }
        }

        @Override
        public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
            // TODO: 2019-05-21 add failure view
        }
    };

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        presenter.removeModelPresenter();
        finish();
    }

    @Override
    protected void onDestroy() {
        captionWatcher.threadStop();
        super.onDestroy();
    }

    @Override
    public void setVideo(Video video) {
        currentVideo = video;
        if(currentVideo.Captions.size() > 0) {
            captionWatcher.setCaption(currentVideo.Captions.get(0));
        }
        initYoutubeView();
    }

    @Override
    public void onActivityResult(
            int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case REQUEST_GOOGLE_PLAY_SERVICES:
                if (resultCode != RESULT_OK) {
                    pushResultMessage("This app requires Google Play Services. Please install " +
                            "Google Play Services on your device and relaunch this app.");
                } else {
                    ((YoutubePresenterImpl)presenter).getDataFromApi();
                }
                break;
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
                        ((YoutubePresenterImpl)presenter).getCredential().setSelectedAccountName(accountName);
                        ((YoutubePresenterImpl)presenter).getDataFromApi();
                    }
                }
                break;
            case REQUEST_AUTHORIZATION:
                if (resultCode == RESULT_OK) {
                    ((YoutubePresenterImpl)presenter).getDataFromApi();
                }
                break;
        }
    }

    @Override
    public void pushResultMessage(final String msg) {
        Handler handler = new Handler(getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void addVideoQueryResultList(List<Video> videos) {

    }

    class captionWatcher extends Thread {
        private Caption caption;
        private int idx;
        private Snippet now;
        private Handler handler;
        private boolean isRunning;

        public captionWatcher() {
            idx = 0;
            handler = new Handler(getMainLooper());
        }

        public void setCaption(Caption caption) {
            this.caption = caption;
        }

        @Override
        public void run() {
            isRunning = true;
            now = caption.Snippets.get(0);
            while(isRunning) {
                try {
                    long nowtime = ytb_player.getCurrentTimeMillis();
                    Log.d("captionwatcher", "nt: " + nowtime + ", end: " + now.Subtitle_EndTime.getTime() + ", start: " + now.Subtitle_StartTime.getTime());
                    if (nowtime > now.Subtitle_EndTime.getTime()) {
                        idx = idx + 1 > caption.Snippets.size() ? caption.Snippets.size() - 1 : idx + 1;
                    }
                        now = caption.Snippets.get(idx);
                        postToMain();
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                }
            }
        }

        private void postToMain() {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    ((TextView)card_caption.findViewById(R.id.caption1)).setText(now.Subtitle_String);
                }
            });
        }

        public void threadStop() {
            this.isRunning = false;
        }
    }
}

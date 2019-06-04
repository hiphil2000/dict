//package com.example.dictionary.View;
//
//import androidx.annotation.NonNull;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.cardview.widget.CardView;
//import androidx.core.app.ActivityCompat;
//import androidx.core.content.ContextCompat;
//
//import android.Manifest;
//import android.app.DownloadManager;
//import android.content.BroadcastReceiver;
//import android.content.Context;
//import android.content.Intent;
//import android.content.IntentFilter;
//import android.content.pm.PackageManager;
//import android.net.Uri;
//import android.os.Bundle;
//import android.os.Handler;
//import android.preference.PreferenceManager;
//import android.util.SparseArray;
//import android.view.View;
//import android.widget.AdapterView;
//import android.widget.ArrayAdapter;
//import android.widget.ProgressBar;
//import android.widget.Spinner;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.example.dictionary.Model.RoomDB.Entity.Caption;
//import com.example.dictionary.Model.RoomDB.Entity.Snippet;
//import com.example.dictionary.Model.RoomDB.Entity.Video;
//import com.example.dictionary.Presenter.YoutubePresenter;
//import com.example.dictionary.Presenter.YoutubePresenterImpl;
//import com.example.dictionary.R;
//import com.example.dictionary.View.ExoPlayer.ExoPlayerManager;
//import com.example.dictionary.util;
//import com.google.android.exoplayer2.Player;
//import com.google.android.exoplayer2.ui.PlayerView;
//
//import java.io.File;
//import java.util.Date;
//import java.util.List;
//import java.util.Timer;
//import java.util.TimerTask;
//
//import at.huber.youtubeExtractor.VideoMeta;
//import at.huber.youtubeExtractor.YouTubeExtractor;
//import at.huber.youtubeExtractor.YtFile;
//
//public class ExoPlayerActivity extends AppCompatActivity implements YoutubePresenter.View{
//    private String YOUTUBE_VIDEO_ID;
//    private String BASE_URL = "https://www.youtube.com";
//    private String mYoutubeLink;
//
//    private Timer timer;
//    private TimeObserveTask task;
//
//    private YtFragmentedVideo fragmentedVideo;
//
//    private Video currentVideo;
//    private YoutubePresenter presenter;
//
//    private CardView cardView;
//    private TextView counter_from;
//    private TextView counter_to;
//    private TextView caption_text;
//    private ProgressBar video_progress;
//    private ProgressBar caption_progress;
//
//    private static final int ITAG_FOR_AUDIO = 140;
//    private static final int REQUEST_PERMISSION = 2000;
//
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_exo_player);
//        viewInitSequence();
//        checkPermission();
//
//        presenter = new YoutubePresenterImpl(this);
//        presenter.setView(this);
//        presenter.setModelPresenter();
//
//        timer = new Timer();
//
//        Intent intent = getIntent();
//        Video video = (Video)intent.getSerializableExtra("video");
//        if (video.IsLocal) {
//            setVideo(video);
//        } else {
//            presenter.getFullVideo(video);
//        }
//    }
//
//    //region permission
//    private void checkPermission() {
//        if (ContextCompat.checkSelfPermission(getApplicationContext(),
//                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
//            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
//                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
//
//            } else {
//                ActivityCompat.requestPermissions(this,
//                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
//                        REQUEST_PERMISSION);
//            }
//        }
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        switch(requestCode) {
//            case 2000:
//                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_DENIED) {
//                    Toast.makeText(getApplicationContext(), "데이터 쓰기 권한이 필요합니다.", Toast.LENGTH_LONG).show();
//                    finish();
//                }
//                return;
//        }
//    }
//    //endregion
//
//    private void viewInitSequence() {
//        cardView = findViewById(R.id.card_caption);
//        counter_from = findViewById(R.id.counter_from);
//        counter_to = findViewById(R.id.counter_to);
//        caption_text = findViewById(R.id.text_caption);
//        video_progress = findViewById(R.id.video_progress);
//        caption_progress = findViewById(R.id.caption_progress);
//    }
//
//    private long downloadFromUrl(String url, String youtube_video_id, final String filename) {
//        Uri uri = Uri.parse(url);
//        DownloadManager.Request request = new DownloadManager.Request(uri);
//        request.setTitle(youtube_video_id);
//        request.setDestinationInExternalPublicDir(getExternalFilesDir(null).getAbsolutePath(), filename);
//
//        DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
//        registerReceiver(new BroadcastReceiver() {
//            @Override
//            public void onReceive(Context context, Intent intent) {
//                playVideo(getExternalFilesDir(null).getAbsolutePath() + "/" + filename);
//            }
//        }, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
//        return manager.enqueue(request);
//    }
//
//    private void extractorSequence() {
//        YOUTUBE_VIDEO_ID = currentVideo.Video_ID;
//        mYoutubeLink = BASE_URL + "/watch?v=" + YOUTUBE_VIDEO_ID;
//        extractYoutubeUrl();
//    }
//
//    private void extractYoutubeUrl() {
//        new YouTubeExtractor(this) {
//            @Override
//            protected void onExtractionComplete(SparseArray<YtFile> ytFiles, VideoMeta videoMeta) {
//                video_progress.setVisibility(View.GONE);
//                caption_progress.setVisibility(View.GONE);
//                if (ytFiles == null) {
//                    caption_text.setText("영상을 불러올 수 없었습니다.");
//                    return;
//                }
//
//                int itag = 134;
//                YtFile ytFile = ytFiles.get(itag);
//                setFragmentedVideo(ytFile, ytFiles);
//
//            }
//        }.extract(mYoutubeLink, true, false);
//    }
//
//    private void setFragmentedVideo(YtFile ytFile, SparseArray<YtFile> ytFiles) {
//        int height = ytFile.getFormat().getHeight();
//
//        YtFragmentedVideo frVideo = new YtFragmentedVideo();
//        frVideo.height = height;
//        if (ytFile.getFormat().isDashContainer()) {
//            if (height > 0) {
//                frVideo.videoFile = ytFile;
//                frVideo.audioFile = ytFiles.get(ITAG_FOR_AUDIO);
//            } else {
//                frVideo.videoFile = ytFile;
//            }
//        } else {
//            frVideo.videoFile = ytFile;
//        }
//        fragmentedVideo = frVideo;
//    }
//
//    private void playVideo(String downloadUrl) {
//        final PlayerView mPlayerView = findViewById(R.id.mPlayerView);
//        final ExoPlayerManager manager = ExoPlayerManager.getInstance(this);
//        mPlayerView.setPlayer(manager.getPlayerView().getPlayer());
//        manager.setPlayerVolume(0.5f);
//
//        String filename = YOUTUBE_VIDEO_ID + ".mp4";
//        filename = filename.replaceAll("[\\\\><\"|*?%:#/]", "");
//        File file = new File(getExternalFilesDir(null).getAbsolutePath() + "/" + filename);
//        if (file.exists()) {
//            playVideo(file.getAbsolutePath());
//        } else {
//            if (fragmentedVideo.videoFile != null) {
//                downloadFromUrl(fragmentedVideo.videoFile.getUrl(),
//                        YOUTUBE_VIDEO_ID,
//                        filename);
//            }
//            if (fragmentedVideo.audioFile != null) {
//                downloadFromUrl(fragmentedVideo.audioFile.getUrl(),
//                        YOUTUBE_VIDEO_ID,
//                        filename.split(".")[0] + "." + fragmentedVideo.audioFile.getFormat().getExt());
//            }
//        }
//
//        manager.playStream(downloadUrl);
//
//        if (currentVideo.Captions != null) {
//            if (currentVideo.Captions.size() > 0) {
//                timer.scheduleAtFixedRate(new TimeObserveTask(manager.getPlayerView().getPlayer(),
//                        currentVideo.Captions.get(0)), 0, 10);
//            }
//        }
//
//    }
//
//    @Override
//    public void setVideo(Video video) {
//        currentVideo = video;
//        if (video.IsLocal) {
//            switch(video.Video_Data_Type) {
//                case LocalLink:
//                    playVideo(video.Video_Data);
//                    break;
//                case HyperLink:
//                    extractorSequence();
//                    break;
//            }
//        } else {
//            new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    presenter.addVideo(currentVideo);
//                }
//            }).start();
//
//            boolean instantDownload = PreferenceManager.getDefaultSharedPreferences(this)
//                    .getBoolean("pref_search_download_youtube", false);
//
//            if (instantDownload) {
//                presenter.addVideo(currentVideo);
//            }
//            extractorSequence();
//        }
//
//        if (currentVideo.Captions != null) {
//            if (currentVideo.Captions.size() > 0)
//                counter_to.setText(String.valueOf(currentVideo.Captions.get(0).Snippets.size()));
//            else
//                caption_text.setText("자막이 없습니다.");
//        } else {
//            caption_text.setText("자막이 없습니다.");
//        }
//    }
//
//    @Override
//    public void pushResultMessage(String msg) {
//
//    }
//
//    @Override
//    public void addVideoQueryResultList(List<Video> videos) {
//
//    }
//
//    @Override
//    protected void onDestroy() {
//        timer = new Timer();
//        super.onDestroy();
//    }
//
//    @Override
//    public void onBackPressed() {
//        timer = new Timer();
//        this.finish();
//    }
//
//    class TimeObserveTask extends TimerTask {
//        private Player player;
//        private Caption caption;
//        private Handler mainHandle;
//        private int idx;
//        private long beforeTime;
//        public boolean isRunning;
//
//        public TimeObserveTask(Player player, Caption caption) {
//            this.player = player;
//            this.caption = caption;
//            mainHandle = new Handler(getMainLooper());
//        }
//
//        public void seekTo(long time) {
//            for(int i = 0; i < caption.Snippets.size(); i++) {
//                Snippet curr = caption.Snippets.get(i);
//                if (curr.Subtitle_EndTime.getTime() >= time && curr.Subtitle_StartTime.getTime() <= time) {
//                    idx = i;
//                    return;
//                }
//            }
//        }
//
//        @Override
//        public void run() {
//            isRunning = true;
//            while(isRunning) {
//                boolean hasChange = false;
//                long currTime = player.getCurrentPosition();
//                Date currDateTime = new Date(currTime);
//                util.sysout("" + currDateTime.getTime());
//                Snippet currCapt = caption.Snippets.get(idx);
//                if (currCapt.Subtitle_EndTime.getTime() < currTime) {
//                    if (idx + 1 < caption.Snippets.size()) {
//                        idx++;
//                        hasChange = true;
//                    }
//                } else if (beforeTime > currTime && currCapt.Subtitle_StartTime.getTime() > currTime) {
//                    seekTo(currTime);
//                    hasChange = true;
//                }
//                if (hasChange) {
//                    post();
//                }
//                beforeTime = currTime;
//            }
//        }
//
//        private void post() {
//            mainHandle.post(new Runnable() {
//                @Override
//                public void run() {
//                    counter_from.setText(String.valueOf(idx + 1));
//                    caption_text.setText(caption.Snippets.get(idx).Subtitle_String);
//                }
//            });
//        }
//    }
//
//    private class YtFragmentedVideo {
//        int height;
//        YtFile audioFile;
//        YtFile videoFile;
//    }
//}

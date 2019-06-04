package com.example.dictionary.Model.YoutubeDataApi;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.view.View;

import com.example.dictionary.Model.RoomDB.Entity.Snippet;
import com.example.dictionary.Model.RoomDB.Entity.Video;
import com.example.dictionary.Model.RoomDB.Entity.VideoType;
import com.example.dictionary.Model.RoomDB.TypeConverters.TimeConverter;
import com.example.dictionary.Presenter.MainPresenter;
import com.example.dictionary.Presenter.YoutubeCallbackable;
import com.example.dictionary.Presenter.YoutubePresenter;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.googleapis.media.MediaHttpDownloader;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.YouTubeScopes;
import com.google.api.services.youtube.model.Caption;
import com.google.api.services.youtube.model.CaptionListResponse;
import com.google.api.services.youtube.model.CaptionSnippet;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import com.google.api.services.youtube.model.SearchResultSnippet;
import com.google.api.services.youtube.model.VideoContentDetails;
import com.google.api.services.youtube.model.VideoListResponse;
import com.google.api.services.youtube.model.VideoSnippet;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class YoutubeDataApiModel {
    private volatile static YoutubeDataApiModel instance;

    public static String TAG = "YoutubeDataAPI";

    private GoogleAccountCredential mCredential;
    private YoutubeCallbackable presenter;
    private File externalFilesDir;

    public static final int REQUEST_ACCOUNT_PICKER = 1000;
    public static final int REQUEST_AUTHORIZATION = 1001;
    public static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
    public static final int REQUEST_PERMISSION_GET_ACCOUNTS = 1003;
    public static final String PREF_ACCOUNT_NAME = "accountName";
    private static final String[] SCOPES = { YouTubeScopes.YOUTUBE_FORCE_SSL, YouTubeScopes.YOUTUBEPARTNER };

    public static YoutubeDataApiModel getInstance() {
        if (instance == null) {
            synchronized (YoutubeDataApiModel.class) {
                if (instance == null) {
                    instance = new YoutubeDataApiModel();
                }
            }
        }
        return instance;
    }

    public YoutubeDataApiModel() {

    }

    public boolean checkReady() {
        if (externalFilesDir == null)
            return false;
        if (mCredential == null) {
            return false;
        } else {
            if (mCredential.getSelectedAccountName() == null)
                return false;
        }
        return true;
    }

    public boolean isSelectedAccountNameNull() {
        return instance.mCredential.getSelectedAccountName() == null;
    }

    public void setPresenter(YoutubeCallbackable presenter) {
        this.presenter = presenter;
    }

    public void setExternalFilesDir(File externalFilesDir) {
        this.externalFilesDir = externalFilesDir;
    }

    public void initCredential(Context context) {
        mCredential = GoogleAccountCredential
                .usingOAuth2(context, Arrays.asList(SCOPES))
                .setBackOff(new ExponentialBackOff());
    }

    public GoogleAccountCredential getCredential() {
        return mCredential;
    }

    public void queryVideo(String query, long maxResult) {
        new MakeRequestTask(mCredential).execute("queryVideo", query, String.valueOf(maxResult));
    }

    public void queryVideoId(String query, long maxResult) {
        new MakeRequestTask(mCredential).execute("queryVideoId", query, String.valueOf(maxResult));
    }

    public void getVideoById(String vid) {
        new MakeRequestTask(mCredential).execute("idToVideo", vid);
    }

    public void getVideoWithContents(String vid) {
        new MakeRequestTask(mCredential).execute("fullVideo", vid);
    }

    private void callback(Object result, String callbackType) {
        if (callbackType.equals("queryVideo") || callbackType.equals("queryVideoId")) {
            List<Object> rm_results = ((ResultModel) result).results;
            List<Video> out_results = new ArrayList<>();
            for (Object rm_item : rm_results) {
                out_results.add((Video) rm_item);
            }
            presenter.dataFromModel(out_results, callbackType);
        } else if (callbackType.equals("fullVideo") || callbackType.equals("idToVideo")) {
            Object rm_result = ((ResultModel) result).result;
            presenter.dataFromModel((Video) rm_result, callbackType);
        }
    }

    private class MakeRequestTask extends AsyncTask<String, Void, ResultModel> {
        private com.google.api.services.youtube.YouTube mService = null;
        private Exception mLastError = null;
        private String action = "";

        MakeRequestTask(GoogleAccountCredential credential) {
            HttpTransport transport = AndroidHttp.newCompatibleTransport();
            JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
            mService = new com.google.api.services.youtube.YouTube.Builder(
                    transport, jsonFactory, credential)
                    .setApplicationName("com.example.dictionary")
                    .build();
        }

        /**
         * Background task to call YouTube Data API.
         * @param "list, download, queryVideo, queryVideoId, idToVideo, fullVideo"
         */
        @Override
        protected ResultModel doInBackground(String... params) {
            ResultModel result = new ResultModel();
            try {
                if (params.length > 0) {
                    action = params[0];
                    switch(action) {
                        case "list":
                            result = getCaptionList(params[1]);
                            break;
                        case "download":
                            result = downloadCaption(params[1]);
                            break;
                        case "queryVideo":
                            result = queryVideoList(params[1], Long.parseLong(params[2]));
                            break;
                        case "queryVideoId":
                            result = queryVideoIdList(params[1], Long.parseLong(params[2]));
                            break;
                        case "idToVideo":
                            result = getVideoById(params[1]);
                            break;
                        case "fullVideo":
                            result = getFullVideoItem(params[1]);
                            break;
                    }
                } else {
                    action = "none";
                }
            } catch (Exception e) {
                mLastError = e;
                e.printStackTrace();
                //cancel(true);
            }
            return result;
        }

        private ResultModel downloadCaption(String captionId) throws IOException {
            ResultModel rm = new ResultModel();
            List<Snippet> ttmls = new ArrayList<>();
            YouTube.Captions.Download download = mService.captions().download(captionId).setTfmt("TTML").setQuotaUser(mCredential.getSelectedAccountName());

            MediaHttpDownloader downloader = download.getMediaHttpDownloader();

            downloader.setDirectDownloadEnabled(false);

            File file = new File(externalFilesDir, captionId + ".ttml");
            OutputStream outputFile = new FileOutputStream(file);
            download.executeAndDownloadTo(outputFile);
            outputFile.close();

            InputStreamReader read = new InputStreamReader(new FileInputStream(file));
            BufferedReader bread = new BufferedReader(read);

            StringBuilder result = new StringBuilder();
            String readed;
            while((readed = bread.readLine()) != null) {
                result.append(readed);
            }
            Pattern time = Pattern.compile("([0-9\\:\\.]+)");
            Pattern text = Pattern.compile("\\>(.*)");

            for(String str : result.toString().split("\\<div\\>")[1].split("\\<\\/div\\>")[0].split("\\<\\/p\\>")) {
                Snippet ttml = new Snippet();
                str.replaceAll("&#39;", "'");
                ttml.Caption_ID = captionId;
                Matcher matcher = time.matcher(str);
                int i = 0;
                while (matcher.find()) {
                    if (i == 0)
                        ttml.Subtitle_StartTime = new Date(TimeConverter.stringToTime(matcher.group(1)).getTime() + 1);
                    else if (i == 1)
                        ttml.Subtitle_EndTime = TimeConverter.stringToTime(matcher.group(1));
                    i++;
                }
                matcher = text.matcher(str);

                if (matcher.find()) {
                    ttml.Subtitle_String = matcher.group(1);
                }
                rm.results.add(ttml);
                Log.d(TAG , ttml.Subtitle_StartTime + " ~ " + ttml.Subtitle_EndTime + " : " + ttml.Subtitle_String);
            }
            return rm;
        }

        private ResultModel getCaptionList(String vid) throws IOException {
            ResultModel rm = new ResultModel();
            CaptionListResponse response = mService.captions().list("snippet", vid).setQuotaUser(mCredential.getSelectedAccountName()).execute();
            List<Caption> captions = response.getItems();
            CaptionSnippet snippet;
            for (Caption caption : captions) {
                com.example.dictionary.Model.RoomDB.Entity.Caption capt = new com.example.dictionary.Model.RoomDB.Entity.Caption();
                snippet = caption.getSnippet();
                if (snippet.getTrackKind().equals("ASR"))
                    continue;
                capt.Caption_ID = caption.getId();
                capt.Video_ID = vid;
                capt.Caption_Name = snippet.getName();
                capt.Caption_Language = snippet.getLanguage();

                Log.d(TAG, "  - ID: " + caption.getId());
                Log.d(TAG, "  - Name: " + snippet.getName());
                Log.d(TAG, "  - Language: " + snippet.getLanguage());
                Log.d(TAG, "\n-------------------------------------------------------------\n");
                if (capt.Caption_Language.equals("en"))
                    rm.results.add(capt);
            }
            return rm;
        }

        private ResultModel getVideoById(String vid) throws IOException {
            ResultModel rm = new ResultModel();

            VideoListResponse response = mService.videos().list("snippet")
                    .setId(vid)
                    .setQuotaUser(mCredential.getSelectedAccountName())
                    .execute();

            for (com.google.api.services.youtube.model.Video result : response.getItems()) {
                Video video = new Video();
                VideoSnippet snippet = result.getSnippet();
                video.Video_ID = vid;
                video.Video_Name = snippet.getTitle();
                video.Video_Thumbnail_Data = snippet.getThumbnails().getMedium().getUrl();
                video.Video_Thumbnail_Data_Type = VideoType.HyperLink;
                video.Video_Data = "https://www.youtube.com/watch?v=" + vid;
                video.Video_Data_Type = VideoType.HyperLink;
                video.Video_Published_Date = new Date(snippet.getPublishedAt().getValue());
                rm.result = video;
            }

            return rm;
        }

        private ResultModel queryVideoIdList(String query, long count) throws IOException {
            ResultModel rm = new ResultModel();

            YouTube.Search.List search = mService.search().list("id");

            search.setQ(query);
            search.setMaxResults(count);
            search.setType("video");
            search.setQuotaUser(mCredential.getSelectedAccountName());
            search.setVideoCaption("closedCaption");
            search.setRelevanceLanguage("en");

            SearchListResponse response = search.execute();
            for (SearchResult result : response.getItems()) {
                Video video = new Video();
                video.Video_ID = result.getId().getVideoId();
                rm.results.add(video);
            }

            return rm;
        }

        private ResultModel queryVideoList(String query, long count) throws IOException {
            ResultModel rm = new ResultModel();

            YouTube.Search.List search = mService.search().list("id,snippet");

            search.setQ(query);
            search.setMaxResults(count);
            search.setType("video");
            search.setQuotaUser(mCredential.getSelectedAccountName());
            search.setVideoCaption("closedCaption");
            search.setRelevanceLanguage("en");

            SearchListResponse response = search.execute();
            SearchResultSnippet snippet;
            for (SearchResult result : response.getItems()) {
                Video vid = new Video();
                snippet = result.getSnippet();
                vid.Video_ID = result.getId().getVideoId();
                vid.Video_Name = snippet.getTitle();
                vid.Video_Published_Date = new Date(snippet.getPublishedAt().getValue() * 1000L);
                vid.Video_Thumbnail_Data = snippet.getThumbnails().getMedium().getUrl();
                vid.Video_Thumbnail_Data_Type = VideoType.HyperLink;
                vid.Video_Data = "https://www.youtube.com/watch?v=" + result.getId().getVideoId();
                vid.Video_Data_Type = VideoType.HyperLink;
                rm.results.add(vid);
            }
            return rm;
        }

        private ResultModel getFullVideoItem(String vid) throws IOException, GoogleAuthException {
            ResultModel rm = new ResultModel();

            Video video = new Video();

            VideoListResponse response = mService.videos()
                    .list("snippet,contentDetails")
                    .setId(vid)
                    .setQuotaUser(mCredential.getSelectedAccountName())
                    .setOauthToken(mCredential.getToken())
                    .execute();

            List<com.google.api.services.youtube.model.Video> results = response.getItems();
            if (results == null)
                return rm;
            if (results.size() <= 0)
                return rm;
            com.google.api.services.youtube.model.Video result = results.get(0);
            VideoContentDetails cDetail = result.getContentDetails();
            VideoSnippet snippet = result.getSnippet();
            video.Video_ID = vid;
            video.Video_Name = snippet.getTitle();
            video.Video_Thumbnail_Data = snippet.getThumbnails().getMedium().getUrl();
            video.Video_Thumbnail_Data_Type = VideoType.HyperLink;
            video.Video_Data = "https://www.youtube.com/watch?v=" + vid;
            video.Video_Data_Type = VideoType.HyperLink;
            video.Video_Published_Date = new Date(snippet.getPublishedAt().getValue());
            String duration = cDetail.getDuration().split("PT")[1];
            String M = duration.split("M")[0];
            String S = duration.split("M")[1].split("S")[0];
            Date runningTime = new Date();
            runningTime.setHours(Integer.parseInt(M)/60);
            runningTime.setMinutes(Integer.parseInt(M)%60);
            runningTime.setSeconds(Integer.parseInt(S));
            video.Video_RunningTime = runningTime;

            List<Object> capts = getCaptionList(vid).results;
            for (Object ca_item : capts) {
                com.example.dictionary.Model.RoomDB.Entity.Caption ca = (com.example.dictionary.Model.RoomDB.Entity.Caption) ca_item;
                try {
                    List<Object> snippets = downloadCaption(ca.Caption_ID).results;
                    for (Object sn_item : snippets) {
                        Snippet sn = (Snippet) sn_item;
                        ca.Snippets.add(sn);
                    }
                    video.Captions.add(ca);
                } catch(GoogleJsonResponseException e) {
                    e.printStackTrace();
                }
            }
            rm.result = video;

            return rm;
        }

        @Override
        protected void onPostExecute(ResultModel output) {
            callback(output, action);
        }

        @Override
        protected void onCancelled() {
            if (mLastError != null) {
                if (mLastError instanceof GooglePlayServicesAvailabilityIOException) {
                    presenter.dialogFromModel("showGooglePlayServicesAvailabilityErrorDialog",
                            ((GooglePlayServicesAvailabilityIOException) mLastError)
                                    .getConnectionStatusCode());
                } else if (mLastError instanceof UserRecoverableAuthIOException) {
                    presenter.dialogFromModel("startActivityForResult",
                            ((UserRecoverableAuthIOException) mLastError).getIntent(),
                            REQUEST_AUTHORIZATION);
                } else {
                    presenter.dialogFromModel("pushText",
                            "The following error occurred:\n" + mLastError.getMessage());
                }
            } else {
                presenter.dialogFromModel("pushText", "Request cancelled.");
            }
        }
    }

    public void downloadFromUrl(String url) {
        URL u = null;
        InputStream is = null;

        try {
            u = new URL(url);
            is = u.openStream();
            HttpURLConnection huc = (HttpURLConnection)u.openConnection(); //to know the size of video
            int size = huc.getContentLength();

            if(huc != null) {
                String fileName = "FILE.mp4";

                String storagePath = externalFilesDir.toString();
                File f = new File(storagePath,fileName);

                FileOutputStream fos = new FileOutputStream(f);
                byte[] buffer = new byte[1024];
                int len1 = 0;
                if(is != null) {
                    while ((len1 = is.read(buffer)) > 0) {
                        fos.write(buffer,0, len1);
                    }
                }
                if(fos != null) {
                    fos.close();
                }
            }
        } catch (MalformedURLException mue) {
            mue.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } finally {
            try {
                if(is != null) {
                    is.close();
                }
            } catch (IOException ioe) {
                // just going to ignore this one
            }
        }
    }

    public class ResultModel {
        public List<Object> results;

        public Object result;

        public ResultModel() {
            results = new ArrayList<>();
        }
    }
}

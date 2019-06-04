package com.example.dictionary.Model.YoutubeDataApi;

import android.content.Context;

import com.example.dictionary.Model.RoomDB.Entity.Caption;
import com.example.dictionary.Model.RoomDB.Entity.Snippet;
import com.example.dictionary.Model.RoomDB.Entity.Video;
import com.example.dictionary.Model.RoomDB.TypeConverters.DateConverter;
import com.example.dictionary.Model.RoomDB.TypeConverters.TimeConverter;
import com.example.dictionary.Model.RoomDB.TypeConverters.VideoTypeConverter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class YoutubeModel {

    private String api_base = "http://218.49.225.170:5082/";

    public YoutubeModel() {

    }

    public List<Video> getRecentData() {
        List<Video> result = new ArrayList<>();

        // TODO: 2019-06-03 top 환경설정 변수로 변경
        String urlStr = api_base + "video?top=" + 5;
        HttpURLConnection conn = getConnection(urlStr, "GET");

        try {
            JSONArray response = new JSONArray(getResponseData(conn));
            for (int i = 0; i < response.length(); i++) {
                Video video = new Video();
                JSONObject object = response.getJSONObject(i);
                video.Video_Data = object.getString("VideoData");
                video.Video_Data_Type = VideoTypeConverter.toVideoType(object.getInt("VideoDataType"));
                video.Video_Description = object.getString("VideoDescription");
                video.Video_ID = object.getString("VideoID");
                video.Video_Name = object.getString("VideoName");
                video.Video_Published_Date = DateConverter.stringToDate(object.getString("VideoPublishDate"));
                video.Video_Thumbnail_Data = object.getString("VideoThumbnailData");
                video.Video_Thumbnail_Data_Type = VideoTypeConverter.toVideoType(object.getInt("VideoThumbnailDataType"));
                result.add(video);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;

    }

    public List<Video> getPagnatedData(int pageSize, int pageAt) {
        List<Video> result = new ArrayList<>();
        return result;
    }

    public Video getVideoDetails(String video_id) {
        Video result = new Video();

        // TODO: 2019-06-03 top 환경설정 변수로 변경
        String urlStr = api_base + "video/" + video_id;
        HttpURLConnection conn = getConnection(urlStr, "GET");

        try {
            JSONArray response = new JSONArray(getResponseData(conn));
            String json = response.getJSONObject(0).getString("result");
            json = json.substring(1, json.length() - 1);
            JSONObject object = new JSONObject(json);
            result.Video_Data = object.getString("VideoData");
            result.Video_Data_Type = VideoTypeConverter.toVideoType(object.getInt("VideoDataType"));
            result.Video_Description = object.getString("VideoDescription");
            result.Video_ID = object.getString("VideoID");
            result.Video_Name = object.getString("VideoName");
            result.Video_Published_Date = stringToDate(object.getString("VideoPublishDate"));
            result.Video_Thumbnail_Data = object.getString("VideoThumbnailData");
            result.Video_Thumbnail_Data_Type = VideoTypeConverter.toVideoType(object.getInt("VideoThumbnailDataType"));
            JSONArray caption = object.getJSONArray("Caption");
            if (caption != null) {
                for (int i = 0; i < caption.length(); i++) {
                    JSONObject capt_json = caption.getJSONObject(i);
                    Caption capt_obj = new Caption();
                    capt_obj.Caption_ID = capt_json.getString("CaptionID");
                    capt_obj.Caption_Language = capt_json.getString("CaptionLanguage");
                    JSONArray snippet = capt_json.getJSONArray("Snippet");
                    if (snippet != null) {
                        for (int j = 0; j < snippet.length(); j++) {
                            JSONObject snip_json = snippet.getJSONObject(i);
                            Snippet snip_obj = new Snippet();
                            snip_obj.Subtitle_ID = snip_json.getInt("SnippetID");
                            snip_obj.Subtitle_StartTime = TimeConverter.stringToTime(snip_json.getString("SnippetStartTime"));
                            snip_obj.Subtitle_EndTime = TimeConverter.stringToTime(snip_json.getString("SnippetEndTime"));
                            snip_obj.Subtitle_String = snip_json.getString("SnippetText");
                            capt_obj.Snippets.add(snip_obj);
                        }
                    }
                    result.Captions.add(capt_obj);
                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;

    }

    private Date stringToDate(String date) {
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z");
        try {
            return sdf.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<Video> searchVideo(String query) {
        List<Video> result = new ArrayList<>();

        String urlStr = api_base + "video";
        if (query.length() > 0)
            urlStr += "?video_name=" + query;
        HttpURLConnection conn = getConnection(urlStr, "GET");

        try {
            JSONArray response = new JSONArray(getResponseData(conn));
            for (int i = 0; i < response.length(); i++) {
                Video video = new Video();
                JSONObject object = response.getJSONObject(i);
                video.Video_Data = object.getString("VideoData");
                video.Video_Data_Type = VideoTypeConverter.toVideoType(object.getInt("VideoDataType"));
                video.Video_Description = object.getString("VideoDescription");
                video.Video_ID = object.getString("VideoID");
                video.Video_Name = object.getString("VideoName");
                video.Video_Published_Date = stringToDate(object.getString("VideoPublishDate"));
                video.Video_Thumbnail_Data = object.getString("VideoThumbnailData");
                video.Video_Thumbnail_Data_Type = VideoTypeConverter.toVideoType(object.getInt("VideoThumbnailDataType"));
                result.add(video);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

    private HttpURLConnection getConnection(String urlstr, String method) {
        HttpURLConnection conn = null;
        try {
            URL url = new URL(urlstr);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod(method);
            conn.setUseCaches(false);
            conn.setDefaultUseCaches(false);
            conn.setConnectTimeout(15000);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return conn;
    }

    private String getResponseData(HttpURLConnection conn) {
        String output =  "";
        BufferedReader br = null;
        OutputStream os = null;
        if (conn != null) {
            try {
                int code = conn.getResponseCode();
                if (code/400 >= 1)
                    br = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                else
                    br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String temp;
                while ((temp = br.readLine()) != null) {
                    output += temp;
                }
                conn.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (os != null) {
                    try { os.close(); } catch (IOException e) { e.printStackTrace(); }
                }
                if (br != null) {
                    try { br.close(); } catch (IOException e) { e.printStackTrace(); }
                }
            }
        }

        return output;
    }
}

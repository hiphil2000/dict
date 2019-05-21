package com.example.dictionary.Model.RoomDB.DAO;

import androidx.room.Dao;
import androidx.room.Query;

import com.example.dictionary.Model.RoomDB.Entity.Subtitle;
import com.example.dictionary.Model.RoomDB.Entity.Video;
import com.example.dictionary.Model.RoomDB.Relations.FullVideo;

import java.util.List;

@Dao
public abstract class VideoDAO {
    public Video getFullVideo(String Video_ID) {
        FullVideo fullVideo = _getFullVideo(Video_ID);
        if (fullVideo == null)
            return null;

        for(Subtitle sub : fullVideo.Subtitles) {
            sub.Video_ID = Video_ID;
        }

        fullVideo.Video.Subtitles = fullVideo.Subtitles;

        return fullVideo.Video;
    }

    @Query("SELECT * FROM Video")
    public abstract List<Video> getVideos();

    @Query("SELECT * FROM Video WHERE Video_Name LIKE :Video_Name")
    public abstract List<Video> getVideos(String Video_Name);

    @Query("SELECT * FROM Video WHERE Video_ID = :Video_ID")
    public abstract Video getVideo(String Video_ID);

    @Query("SELECT * FROM Video WHERE Video_ID = :Video_ID")
    public abstract FullVideo _getFullVideo(String Video_ID);

}

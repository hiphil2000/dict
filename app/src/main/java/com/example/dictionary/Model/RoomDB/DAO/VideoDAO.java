package com.example.dictionary.Model.RoomDB.DAO;

import androidx.room.Dao;
import androidx.room.Ignore;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.dictionary.Model.RoomDB.Entity.Caption;
import com.example.dictionary.Model.RoomDB.Entity.Snippet;
import com.example.dictionary.Model.RoomDB.Entity.Video;
import com.example.dictionary.Model.RoomDB.Relations.FullCaption;
import com.example.dictionary.Model.RoomDB.Relations.FullVideo;

import java.util.List;

@Dao
public abstract class VideoDAO {
    public Video getFullVideo(String Video_ID) {
        FullVideo fullVideo = _getFullVideo(Video_ID);
        if (fullVideo == null)
            return null;

        for(FullCaption fcapt : fullVideo.Captions) {
            fcapt.Caption.Video_ID = Video_ID;
            fcapt.Caption.Snippets = fcapt.Snippets;
            for (Snippet snip : fcapt.Snippets) {
                snip.Caption_ID = fcapt.Caption.Caption_ID;
            }
            fullVideo.Video.Captions.add(fcapt.Caption);
        }

        return fullVideo.Video;
    }

    public void addVideo(Video video) {
        _addVideo(video);
        for(Caption caption : video.Captions) {
            caption.Video_ID = video.Video_ID;
            _addCaption(caption);
            for(Snippet snippet : caption.Snippets) {
                snippet.Caption_ID = caption.Caption_ID;
                _addSnippet(snippet);
            }
        }
    }

    @Query("SELECT * FROM Video")
    public abstract List<Video> getVideos();

    @Query("SELECT * FROM Video WHERE Video_Name LIKE :Video_Name")
    public abstract List<Video> getVideos(String Video_Name);

    @Query("SELECT * FROM Video WHERE Video_ID = :Video_ID")
    public abstract Video getVideo(String Video_ID);

    @Query("SELECT * FROM Video WHERE Video_ID = :Video_ID")
    public abstract FullVideo _getFullVideo(String Video_ID);

    @Query("SELECT * FROM Video WHERE Video_Name LIKE :query")
    public abstract List<Video> searchVideo(String query);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void _addVideo(Video video);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void _addCaption(Caption caption);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void _addSnippet(Snippet snippet);
}

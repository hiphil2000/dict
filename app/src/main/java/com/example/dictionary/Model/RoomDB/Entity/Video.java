package com.example.dictionary.Model.RoomDB.Entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.example.dictionary.Model.RoomDB.TypeConverters.DateConverter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity(
        indices = {
                @Index(value = "Video_ID", unique = true)
        }
)
public class Video implements Serializable {
    @NonNull
    @PrimaryKey
    public String Video_ID;

    public String Video_Thumbnail_Data;

    @TypeConverters({DateConverter.class})
    public Date Video_Published_Date;

    public String Video_Channel_Name;

    public String Video_Name;

    public int Video_RunningTime;

    public String Video_Data;

    @Ignore
    public List<Subtitle> Subtitles;

    @Ignore
    public boolean IsLocal;

    public Video() {
        Subtitles = new ArrayList<>();
    }
}
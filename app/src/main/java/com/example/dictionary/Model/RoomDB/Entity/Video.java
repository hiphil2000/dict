package com.example.dictionary.Model.RoomDB.Entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.example.dictionary.Model.RoomDB.TypeConverters.DateConverter;
import com.example.dictionary.Model.RoomDB.TypeConverters.TimeConverter;
import com.example.dictionary.Model.RoomDB.TypeConverters.VideoTypeConverter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Video implements Serializable {
    public String Video_ID;

    public String Video_Thumbnail_Data;

    public VideoType Video_Thumbnail_Data_Type;

    public Date Video_Published_Date;

    public String Video_Name;

    public Date Video_RunningTime;

    public String Video_Data;

    public VideoType Video_Data_Type;

    public String Video_Description;

    public List<Caption> Captions;

    public boolean IsLocal;

    public Video() {Captions = new ArrayList<>();
    }
}
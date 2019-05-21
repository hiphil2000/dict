package com.example.dictionary.Model.RoomDB.Entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
        indices = {
                @Index(value = "Video_ID", unique = true)
        }
)
public class Video {
    @PrimaryKey(autoGenerate = true)
    @NonNull
    public long Video_ID;

    public String Video_Name;

    public int Video_RunningTime;

    public String Video_Data;

    public Video() {}
}
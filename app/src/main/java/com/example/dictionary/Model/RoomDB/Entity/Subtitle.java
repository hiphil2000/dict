package com.example.dictionary.Model.RoomDB.Entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import static androidx.room.ForeignKey.CASCADE;

@Entity(
        foreignKeys = {
                @ForeignKey(
                        entity = Video.class,
                        parentColumns = "Video_ID",
                        childColumns = "Video_ID",
                        onDelete = CASCADE
                )
        },
        indices = {
                @Index(value = "Subtitle_ID", unique = true),
                @Index(value = "Video_ID"),
        }
)
public class Subtitle {
    @PrimaryKey(autoGenerate = true)
    @NonNull
    public long Subtitle_ID;

    @NonNull
    public String Video_ID;

    public int Subtitle_StartTime;

    public int Subtitle_EndTime;

    public String Subtitle_String;

    public Subtitle() {}
}

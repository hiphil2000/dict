package com.example.dictionary.Model.RoomDB.Entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.example.dictionary.Model.RoomDB.TypeConverters.DateConverter;
import com.example.dictionary.Model.RoomDB.TypeConverters.TimeConverter;

import java.io.Serializable;
import java.util.Date;

import static androidx.room.ForeignKey.CASCADE;

@Entity(
        foreignKeys = {
                @ForeignKey(
                        entity = Caption.class,
                        parentColumns = "Caption_ID",
                        childColumns = "Caption_ID",
                        onDelete = CASCADE
                )
        },
        indices = {
                @Index(value = "Subtitle_ID", unique = true),
                @Index(value = "Caption_ID"),
        }
)
public class Snippet implements Serializable {
    @PrimaryKey(autoGenerate = true)
    @NonNull
    public long Subtitle_ID;

    @NonNull
    public String Caption_ID;

    @TypeConverters({TimeConverter.class})
    public Date Subtitle_StartTime;

    @TypeConverters({TimeConverter.class})
    public Date Subtitle_EndTime;

    public String Subtitle_String;

    public Snippet() {}
}

package com.example.dictionary.Model.RoomDB.Entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.example.dictionary.Model.RoomDB.TypeConverters.DateConverter;
import com.example.dictionary.Model.RoomDB.TypeConverters.LogTypeConverter;

import java.io.Serializable;
import java.util.Date;

import static androidx.room.ForeignKey.CASCADE;

@Entity(
        foreignKeys = {
                @ForeignKey(
                        entity = Word.class,
                        parentColumns = "Word_String",
                        childColumns = "Word_String",
                        onDelete = CASCADE
                )
        },
        indices = {
                @Index(value = "Log_ID", unique = true),
                @Index(value = "Word_String")
        }
)
public class Log implements Serializable {
    @PrimaryKey(autoGenerate = true)
    @NonNull
    public long Log_ID;

    @TypeConverters({DateConverter.class})
    public Date Log_Date;

    @TypeConverters({LogTypeConverter.class})
    public LogType LogType;

    @NonNull
    public String Word_String;

    public String Log_Data;

    public Log(){}

    @Ignore
    public Log(Date LogDate, LogType LogType, String Word_String) {
        this.Log_Date = LogDate;
        this.LogType = LogType;
        this.Word_String = Word_String;
    }
}

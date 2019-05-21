package com.example.dictionary.Model.RoomDB.Entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.example.dictionary.Model.RoomDB.TypeConverters.DateConverter;
import com.example.dictionary.Model.RoomDB.TypeConverters.PronExtensionConverter;

import java.io.Serializable;

@Entity(
        indices = {
                @Index(value = "Pron_ID", unique = true)
        }
)
public class Pron implements Serializable {
    @PrimaryKey
    @NonNull
    public long Pron_ID;

    public String Pron_String;

    @TypeConverters({PronExtensionConverter.class})
    public PronExtension Pron_Extension;

    public String Pron_Data;

    public Pron() {}
}

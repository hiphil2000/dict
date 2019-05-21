package com.example.dictionary.Model.RoomDB.Entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.io.Serializable;

import static androidx.room.ForeignKey.CASCADE;

@Entity(
        foreignKeys = {
                @ForeignKey(
                        entity = Meaning.class,
                        parentColumns = "Meaning_ID",
                        childColumns = "Meaning_ID",
                        onDelete = CASCADE
                )
        },
        indices = {
                @Index(value = "Usage_ID", unique = true),
                @Index(value = {"Meaning_ID", "Usage_Seq"}),

        }
)
public class Usage implements Serializable {
    @PrimaryKey(autoGenerate = true)
    @NonNull
    public long Usage_ID;

    @NonNull
    public long Meaning_ID;

    @NonNull
    public int Usage_Seq;

    public String Usage_String;

    public Usage() {}
}

package com.example.dictionary.Model.RoomDB.Entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

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
                @Index(value = "Caption_ID", unique = true),
                @Index(value = "Video_ID"),
        }

)
public class Caption implements Serializable {
    @PrimaryKey
    @NonNull
    public String Caption_ID;

    @NonNull
    public String Video_ID;

    public String Caption_Name;

    public String Caption_Language;

    @Ignore
    public List<Snippet> Snippets;

    public Caption () {
        Snippets = new ArrayList<>();
    }

}

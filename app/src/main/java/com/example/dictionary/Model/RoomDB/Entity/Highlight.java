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
                        entity = Snippet.class,
                        parentColumns = "Subtitle_ID",
                        childColumns = "Subtitle_ID",
                        onDelete = CASCADE
                ),
                @ForeignKey(
                        entity = Word.class,
                        parentColumns = "Word_ID",
                        childColumns = "Word_ID",
                        onDelete = CASCADE
                )
        },
        indices = {
                @Index(value = "Highlight_ID", unique = true),
                @Index(value = "Subtitle_ID"),
                @Index(value = "Word_ID")
        }
)
public class Highlight {
    @PrimaryKey
    @NonNull
    public long Highlight_ID;

    @NonNull
    public long Subtitle_ID;

    @NonNull
    public long Word_ID;

    public Highlight(){}
}

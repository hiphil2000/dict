package com.example.dictionary.Model.RoomDB.Entity;

import androidx.annotation.NonNull;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.example.dictionary.Model.RoomDB.TypeConverters.WordTypeConverter;

import java.io.Serializable;
import java.util.List;

import static androidx.room.ForeignKey.CASCADE;

@Entity(
        foreignKeys =  {
                @ForeignKey(
                        entity = Word.class,
                        parentColumns = "Word_ID",
                        childColumns = "Word_ID",
                        onDelete = CASCADE
                )
        },
        indices = {
                @Index(value = {"Meaning_ID"}, unique = true),
                @Index(value = {"Word_ID"}),
                @Index(value = {"Meaning_Type"})
        }

)
public class Meaning implements Serializable {
    @PrimaryKey(autoGenerate = true)
    @NonNull
    public long Meaning_ID;

    @NonNull
    public long Word_ID;

    public String Meaning_Kor;

    public String Meaning_Eng;

    @Embedded(prefix = "PronUK_")
    public Pron PronUK;

    @Embedded(prefix = "PronUS_")
    public Pron PronUS;

    @NonNull
    @TypeConverters(WordTypeConverter.class)
    public WordType Meaning_Type;

    @Ignore
    public List<Usage> Usages;

    public Meaning() {}
}

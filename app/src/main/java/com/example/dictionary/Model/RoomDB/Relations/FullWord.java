package com.example.dictionary.Model.RoomDB.Relations;

import androidx.room.Embedded;
import androidx.room.Relation;

import com.example.dictionary.Model.RoomDB.Entity.Log;
import com.example.dictionary.Model.RoomDB.Entity.Meaning;
import com.example.dictionary.Model.RoomDB.Entity.Word;

import java.util.List;

public class FullWord {
    @Embedded
    public Word Word;

    @Relation(parentColumn = "Word_ID", entityColumn = "Word_ID", entity = Meaning.class)
    public List<FullMeaning> Meanings;

    @Relation(parentColumn = "Word_String", entityColumn = "Word_String", entity = Log.class)
    public List<Log> Logs;
}

package com.example.dictionary.Model.RoomDB.Relations;

import androidx.room.Embedded;
import androidx.room.Relation;

import com.example.dictionary.Model.RoomDB.Entity.Meaning;
import com.example.dictionary.Model.RoomDB.Entity.Pron;
import com.example.dictionary.Model.RoomDB.Entity.Usage;
import com.example.dictionary.Model.RoomDB.Entity.WordType;

import java.util.List;

public class FullMeaning {
    @Embedded
    public Meaning Meaning;

    @Relation(parentColumn = "Meaning_ID", entityColumn = "Meaning_ID", entity = Usage.class)
    public List<Usage> Usages;
}

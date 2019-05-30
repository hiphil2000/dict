package com.example.dictionary.Model.RoomDB.Relations;

import androidx.room.Embedded;
import androidx.room.Relation;

import com.example.dictionary.Model.RoomDB.Entity.Caption;
import com.example.dictionary.Model.RoomDB.Entity.Snippet;

import java.util.List;

public class FullCaption {
    @Embedded
    public Caption Caption;

    @Relation(parentColumn = "Caption_ID", entityColumn = "Caption_ID", entity = Snippet.class)
    public List<Snippet> Snippets;
}

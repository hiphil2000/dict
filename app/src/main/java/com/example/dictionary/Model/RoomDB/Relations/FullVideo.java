package com.example.dictionary.Model.RoomDB.Relations;

import androidx.room.Embedded;
import androidx.room.Relation;

import com.example.dictionary.Model.RoomDB.Entity.Caption;
import com.example.dictionary.Model.RoomDB.Entity.Snippet;
import com.example.dictionary.Model.RoomDB.Entity.Video;

import java.util.List;

public class FullVideo {
    @Embedded
    public Video Video;

    @Relation(parentColumn = "Video_ID", entityColumn = "Video_ID", entity = Caption.class)
    public List<FullCaption> Captions;
}

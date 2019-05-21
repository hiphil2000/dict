package com.example.dictionary.Model.RoomDB.Entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import com.example.dictionary.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity(indices = {
        @Index(value = {"Word_ID"}, unique = true),
        @Index(value = {"Word_String"}, unique = true)
})
public class Word implements Serializable {
    @PrimaryKey(autoGenerate = true)
    @NonNull
    public long Word_ID;

    @NonNull
    public String Word_String;

    @Ignore
    public List<Meaning> Meanings;

    @Ignore
    public List<Log> Logs;

    @Ignore
    public boolean IsLocal;

    public Word() {}

    @Ignore
    public Word(boolean IsLocal) {
        this.IsLocal = IsLocal;
        Word_String = "";
        Logs = new ArrayList<>();
        Meanings = new ArrayList<>();
    }

    public Log getLastestLog() {
        if (Logs != null) {
            if (Logs.size() > 0) {
                Collections.sort(Logs, new util.LogDescender());
                return Logs.get(0);
            }
        }
        return null;
    }


}

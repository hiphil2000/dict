package com.example.dictionary.Model.RoomDB;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.example.dictionary.Model.RoomDB.DAO.LogDAO;
import com.example.dictionary.Model.RoomDB.DAO.WordDAO;
import com.example.dictionary.Model.RoomDB.Entity.Caption;
import com.example.dictionary.Model.RoomDB.Entity.Highlight;
import com.example.dictionary.Model.RoomDB.Entity.Log;
import com.example.dictionary.Model.RoomDB.Entity.Meaning;
import com.example.dictionary.Model.RoomDB.Entity.Pron;
import com.example.dictionary.Model.RoomDB.Entity.Snippet;
import com.example.dictionary.Model.RoomDB.Entity.Usage;
import com.example.dictionary.Model.RoomDB.Entity.Video;
import com.example.dictionary.Model.RoomDB.Entity.Word;

@Database(
        entities = {
                Word.class,
                Meaning.class,
                Usage.class,
                Pron.class,
                Log.class
        },
        version = 5
)
public abstract class AppDatabase extends RoomDatabase {
    public abstract WordDAO getWordNoteDAO();
    public abstract LogDAO getLogDAO();

}

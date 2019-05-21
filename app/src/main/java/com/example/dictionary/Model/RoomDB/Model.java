package com.example.dictionary.Model.RoomDB;

import android.content.Context;

import androidx.room.Room;

import com.example.dictionary.Model.RoomDB.DAO.LogDAO;
import com.example.dictionary.Model.RoomDB.DAO.VideoDAO;
import com.example.dictionary.Model.RoomDB.DAO.WordDAO;
import com.example.dictionary.Model.RoomDB.Entity.Log;
import com.example.dictionary.Model.RoomDB.Entity.Video;
import com.example.dictionary.Model.RoomDB.Entity.Word;

import java.util.List;

public class Model {
    private AppDatabase db;
    private WordDAO wordDAO;
    private LogDAO logDAO;
    private VideoDAO videoDAO;

    public Model(Context context) {
        db = Room.databaseBuilder(context, AppDatabase.class, "EnglishDictDB.db")
                .fallbackToDestructiveMigration()
                .build();
        wordDAO = db.getWordNoteDAO();
        logDAO = db.getLogDAO();
        videoDAO = db.getVideoDAO();
    }

    public String AddWord(Word word) {
        Word temp = wordDAO.getFullWord(word);
        if (temp != null) {
            if (temp.Meanings.size() > 0)
                return "이미 존재하는 단어입니다.";
            else {
                wordDAO.insert(word);
                return "단어가 등록됐습니다.";
            }
        } else {
            wordDAO.insert(word);
            return "단어가 등록됐습니다.";
        }
    }

    public String RemoveWord(Word word) {
        wordDAO.Delete(word);
        return "단어가 삭제됐습니다.";
    }

    public String CommitChanges(Word word) {
        wordDAO.insert(word);
        return "\"" + word.Word_String + "\'의 변경사항이 저장됐습니다.";
    }

    public Word GetWord(String Word_String) {
        Word temp = new Word();
        temp.Word_String = Word_String;
        Word result = wordDAO.getFullWord(temp);
        if (result != null)
            result.IsLocal = true;
        return result;
    }

    public List<Word> GetWords() {
        return wordDAO.getFullWords();
    }

    public void AddLog(Log log) {
        if (GetWord(log.Word_String) == null) {
            Word word = new Word();
            word.Word_String = log.Word_String;
            AddWord(word);
        }
        logDAO.insert(log);
    }

    public List<Word> GetLogs() {
        //return logDAO.getLogs(logType, wordName);
        return wordDAO.getFullWords();
    }

    public List<Word> GetWordLogs(String Word_String) {
        //return logDAO.getLogs(logType, wordName);
        return wordDAO.getFullWords();
    }

    public Video GetVideo(String Video_ID) {
        return videoDAO.getFullVideo(Video_ID);
    }

    public List<Video> GetVideos() {
        return videoDAO.getVideos();
    }

    public List<Video> GetVideos(String query) {
        return videoDAO.getVideos(query);
    }

}

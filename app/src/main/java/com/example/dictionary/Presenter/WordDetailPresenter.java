package com.example.dictionary.Presenter;

import com.example.dictionary.Model.RoomDB.Entity.LogType;
import com.example.dictionary.Model.RoomDB.Entity.Word;

public interface WordDetailPresenter {
    void setView(WordDetailPresenter.View view);
    void writeLog(String entryId, LogType logType);
    void getLog(String entryId);
    void callback(Word word);

    interface View {
        void setWord(Word word);
    }
}

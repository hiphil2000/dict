package com.example.dictionary.Presenter;

import com.example.dictionary.Model.RoomDB.Entity.Word;

public interface WordEditPresenter {
    void setView(WordEditPresenter.View view);
    void commitChanges(Word word);
    void addWord(Word word);
    void callback(String msg);
    interface View {
        void pushResultMessage(String msg);
    }
}

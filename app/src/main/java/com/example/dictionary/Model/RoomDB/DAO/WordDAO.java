package com.example.dictionary.Model.RoomDB.DAO;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.dictionary.Model.RoomDB.Entity.Meaning;
import com.example.dictionary.Model.RoomDB.Entity.Pron;
import com.example.dictionary.Model.RoomDB.Entity.PronExtension;
import com.example.dictionary.Model.RoomDB.Entity.Usage;
import com.example.dictionary.Model.RoomDB.Entity.Word;
import com.example.dictionary.Model.RoomDB.Relations.FullMeaning;
import com.example.dictionary.Model.RoomDB.Relations.FullWord;

import java.util.ArrayList;
import java.util.List;

@Dao
public abstract class WordDAO {
    public Word getFullWord(Word word) {
        FullWord fullWord = _getFullWord(word.Word_String);
        if (fullWord == null)
            return null;

        List<Meaning> meanings = new ArrayList<>(fullWord.Meanings.size());

        for(FullMeaning fm_item : fullWord.Meanings) {
            fm_item.Meaning.Usages = fm_item.Usages;
//            fm_item.Meaning.PronUK = fm_item.PronUK;
//            fm_item.Meaning.PronUS = fm_item.PronUS;
            meanings.add(fm_item.Meaning);
        }

        fullWord.Word.Meanings = meanings;
        fullWord.Word.Logs = fullWord.Logs;
        return fullWord.Word;
    }
    public List<Word> getFullWords() {
        List<FullWord> fullWords = _getFullWords();
        List<Word> result = new ArrayList<>();
        for (FullWord fullWord : fullWords) {
            if (fullWord == null)
                return null;

            List<Meaning> meanings = new ArrayList<>(fullWord.Meanings.size());

            for(FullMeaning fm_item : fullWord.Meanings) {
                fm_item.Meaning.Usages = fm_item.Usages;
    //            fm_item.Meaning.PronUK = fm_item.PronUK;
    //            fm_item.Meaning.PronUS = fm_item.PronUS;
                meanings.add(fm_item.Meaning);
            }

            fullWord.Word.Meanings = meanings;
            fullWord.Word.Logs = fullWord.Logs;
            fullWord.Word.IsLocal = true;
            result.add(fullWord.Word);
        }
        return result;
    }

    public void insert(Word word) {
        if (word != null) {
            long Word_ID = _insert(word);

            if (word.Meanings != null) {
                for (Meaning mn_item : word.Meanings) {
                    mn_item.Word_ID = Word_ID;
                    long Meaning_ID = _insertMeaning(mn_item);
                    if (mn_item.Usages != null) {
                        for (Usage us_item : mn_item.Usages) {
                            us_item.Meaning_ID = Meaning_ID;
                            _insertUsage(us_item);
                        }
                    }
                }
            }
        }
    }

    @Delete
    public abstract void Delete(Word word);

    @Query("SELECT * FROM Word")
    public abstract List<Word> getWords();

    @Query("SELECT * FROM Word")
    public abstract List<FullWord> _getFullWords();

    @Query("SELECT * FROM Word WHERE Word_String = :Word_String")
    public abstract FullWord _getFullWord(String Word_String);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract long _insert(Word word);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract long _insertMeaning(Meaning meaning);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract long _insertUsage(Usage usage);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract long _insertPron(Pron pron);
}

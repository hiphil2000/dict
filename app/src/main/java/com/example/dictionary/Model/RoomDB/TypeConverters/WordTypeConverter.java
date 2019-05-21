package com.example.dictionary.Model.RoomDB.TypeConverters;

import androidx.room.TypeConverter;

import com.example.dictionary.Model.RoomDB.Entity.WordType;

public class WordTypeConverter {
    @TypeConverter
    public static WordType toWordType(int code) {
        if (code == WordType.Noun.getWordCode()) {
            return WordType.Noun;
        } else if (code == WordType.Verb.getWordCode()) {
            return WordType.Verb;
        } else if (code == WordType.Adjective.getWordCode()) {
            return WordType.Adjective;
        } else if (code == WordType.Adverb.getWordCode()) {
            return WordType.Adverb;
        } else if (code == WordType.Preposition.getWordCode()) {
            return WordType.Preposition;
        } else if (code == WordType.Pronoun.getWordCode()) {
            return WordType.Pronoun;
        } else if (code == WordType.Determiner.getWordCode()) {
            return WordType.Determiner;
        } else if (code == WordType.Conjunction.getWordCode()) {
            return WordType.Conjunction;
        } else if (code == WordType.Exclamation.getWordCode()) {
            return WordType.Exclamation;
        } else {
            return null;
        }
    }

    @TypeConverter
    public static Integer toInteger(WordType wordType) {
        return wordType.getWordCode();
    }
}

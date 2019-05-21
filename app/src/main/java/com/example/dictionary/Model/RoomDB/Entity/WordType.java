package com.example.dictionary.Model.RoomDB.Entity;

public enum WordType {
    Noun(0),
    Verb(1),
    Adverb(2),
    Adjective(3),
    Preposition(4),
    Pronoun(5),
    Determiner(6),
    Conjunction(7),
    Exclamation(8),
    Unknown(-1);

    private int wordCode;

    WordType(int wordCode) {
        this.wordCode = wordCode;
    }

    public int getWordCode() {
        return wordCode;
    }
}
package com.example.dictionary.Model.RoomDB.Entity;

public enum LogType {
    WordSearch(1),
    WordLocalDetail(2),
    WordWebDetail(4),
    WordCreate(8),
    WordMemorized(16),
    WordUnMemorized(32),
    All(63),
    Unknown(0);


    private int logCode;

    LogType(int logCode) {
        this.logCode = logCode;
    }

    public int getLogCode() {
        return logCode;
    }
}
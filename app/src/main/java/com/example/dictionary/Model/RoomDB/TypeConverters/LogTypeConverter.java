package com.example.dictionary.Model.RoomDB.TypeConverters;

import androidx.room.TypeConverter;

import com.example.dictionary.Model.RoomDB.Entity.LogType;

public class LogTypeConverter {
    @TypeConverter
    public static LogType toLogType(int logCode) {
        if (logCode == LogType.WordSearch.getLogCode()) {
            return LogType.WordSearch;
        } else if (logCode == LogType.WordWebDetail.getLogCode()) {
            return LogType.WordWebDetail;
        } else if (logCode == LogType.WordLocalDetail.getLogCode()) {
            return LogType.WordLocalDetail;
        } else if (logCode == LogType.WordCreate.getLogCode()) {
            return LogType.WordCreate;
        } else if (logCode == LogType.WordMemorized.getLogCode()) {
            return LogType.WordMemorized;
        } else {
            return LogType.Unknown;
        }
    }

    @TypeConverter
    public static Integer toInteger(LogType logType) {
        return logType.getLogCode();
    }
}

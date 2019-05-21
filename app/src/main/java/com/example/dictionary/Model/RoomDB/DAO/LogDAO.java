package com.example.dictionary.Model.RoomDB.DAO;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.dictionary.Model.RoomDB.Entity.Log;
import com.example.dictionary.Model.RoomDB.Entity.LogType;
import com.example.dictionary.Model.RoomDB.TypeConverters.LogTypeConverter;

import java.util.ArrayList;
import java.util.List;

@Dao
public abstract class LogDAO {
    public void insert(Log log) {
        _insertLog(log);
    }

    public List<Log> getLogs(LogType logType, String wordName) {
        List<Log> fullLogs;
        if (logType == null)
            return _getFullLogs(LogTypeConverter.toInteger(LogType.All), wordName);
        else
            return _getFullLogs(LogTypeConverter.toInteger(logType), wordName);
    }

    @Insert
    abstract void _insertLog(Log log);

    @Query("SELECT * FROM Log WHERE LogType & :LogType > 0 AND Word_String = :Word_Name")
    abstract List<Log> _getFullLogs(int LogType, String Word_Name);

}

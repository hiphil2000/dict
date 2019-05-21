package com.example.dictionary.Model.RoomDB.DAO;

import androidx.room.Dao;
import androidx.room.Query;

import com.example.dictionary.Model.RoomDB.Entity.LogType;

import java.util.List;

@Dao
public interface LogTypeDAO {
    @Query("SELECT * FROM LogType")
    List<LogType> Get_LogTypes();
}

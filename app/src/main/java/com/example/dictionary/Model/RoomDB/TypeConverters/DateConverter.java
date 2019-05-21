package com.example.dictionary.Model.RoomDB.TypeConverters;

import androidx.room.TypeConverter;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateConverter {
    static DateFormat df = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy");
    @TypeConverter
    public static Date stringToDate(String value) {
        if (value != null) {
            try {
                return df.parse(value);
            } catch(ParseException e) {
                e.printStackTrace();
            }
            return null;
        } else {
            return null;
        }
    }

    @TypeConverter
    public static String dateToString(Date date) {
        return date == null ? null : date.toString();
    }

}

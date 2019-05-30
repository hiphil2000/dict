package com.example.dictionary.Model.RoomDB.TypeConverters;

import androidx.room.TypeConverter;

import com.example.dictionary.Model.RoomDB.Entity.VideoType;

public class VideoTypeConverter {
    @TypeConverter
    public static VideoType toVideoType(int code) {
        if (code == VideoType.HyperLink.getCode()) {
            return VideoType.HyperLink;
        } else if (code == VideoType.LocalLink.getCode()) {
            return VideoType.LocalLink;
        } else if (code == VideoType.Base64.getCode()) {
            return VideoType.Base64;
        } else {
            return null;
        }
    }

    @TypeConverter
    public static Integer toInteger(VideoType videoType) {
        return videoType.getCode();
    }
}

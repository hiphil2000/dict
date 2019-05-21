package com.example.dictionary.Model.RoomDB.TypeConverters;

import androidx.room.TypeConverter;

import com.example.dictionary.Model.RoomDB.Entity.PronExtension;

public class PronExtensionConverter {
    @TypeConverter
    public static PronExtension toExtensionType(int code) {
        if (code == PronExtension.BASE64.getExtensionCode()) {
            return PronExtension.BASE64;
        } else if (code == PronExtension.HYPERLINK.getExtensionCode()) {
            return PronExtension.HYPERLINK;
        } else {
            return PronExtension.UNKNOWN;
        }
    }

    @TypeConverter
    public static int toInteger(PronExtension extension) {
        if (extension == null)
            return 0;
        return extension.getExtensionCode();
    }
}

package com.example.dictionary.Model.RoomDB.Entity;

public enum VideoType {
    HyperLink(1),
    LocalLink(2),
    Base64(3);

    private int code;

    VideoType(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}

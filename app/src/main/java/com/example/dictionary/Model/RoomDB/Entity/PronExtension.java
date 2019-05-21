package com.example.dictionary.Model.RoomDB.Entity;

public enum PronExtension {
    UNKNOWN(0),
    BASE64(1),
    HYPERLINK(2);

    private int extensionCode;

    PronExtension(int extensionCode) {
        this.extensionCode = extensionCode;
    }

    public int getExtensionCode() {
        return extensionCode;
    }
}

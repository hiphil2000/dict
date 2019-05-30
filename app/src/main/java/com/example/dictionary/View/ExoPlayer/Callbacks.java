package com.example.dictionary.View.ExoPlayer;

public interface Callbacks {
    void callbackObserver(Object obj);

    public interface playerCallback {
        void onItemClickOnItem(Integer albumId);
        void onPlayingEnd();
    }

}

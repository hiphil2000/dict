package com.example.dictionary.Model.YoutubeDataApi;

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.services.youtube.YouTubeScopes;

public class YoutubeDataApiModel {
    private GoogleAccountCredential credential;

    private static final String[] SCOPES = {YouTubeScopes.YOUTUBE_FORCE_SSL};
    private static final String PREF_ACCOUNT_NAME = "accountName";

    public void YoutubeDataApiModel() {
        // TODO: 2019-05-21 implement youtube model like as com.example.youtubeapi

    }
}

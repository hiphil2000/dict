package com.example.dictionary.View;

import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.amazonaws.util.Base64;
import com.example.dictionary.Model.RoomDB.Entity.LogType;
import com.example.dictionary.Model.RoomDB.Entity.Pron;
import com.example.dictionary.Model.RoomDB.Entity.PronExtension;
import com.example.dictionary.Model.RoomDB.Entity.PronType;
import com.example.dictionary.Model.RoomDB.Entity.Meaning;
import com.example.dictionary.Model.RoomDB.Entity.Word;
import com.example.dictionary.Presenter.WordDetailPresenter;
import com.example.dictionary.Presenter.WordDetailPresenterImpl;
import com.example.dictionary.R;
import com.example.dictionary.View.RecycleAdapter.MeaningRecyclerAdapter;
import com.example.dictionary.util;

import java.io.IOException;
import java.util.ArrayList;

public class WordDetailActivity extends AppCompatActivity implements WordDetailPresenter.View {
    private WordDetailPresenter wordDetailPresenter;
    private Word word;
    private Meaning typicalMeaning;

    private TextView text_headword;
    private TextView text_pron_us;
    private TextView text_pron_uk;
    private ImageButton button_pron_uk;
    private ImageButton button_pron_us;
    private RecyclerView list_meaning;
    private MeaningRecyclerAdapter meaningRecyclerAdapter;

    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_detail);
        Intent intent = getIntent();
        word = (Word) intent.getSerializableExtra("wordData");
        init_view();
        init_data();
        wordDetailPresenter = new WordDetailPresenterImpl(WordDetailActivity.this);
        if (word.IsLocal == true)
            wordDetailPresenter.writeLog(word.Word_String, LogType.WordLocalDetail);
        else
            wordDetailPresenter.writeLog(word.Word_String, LogType.WordWebDetail);
    }

    private void init_view() {
        text_headword = findViewById(R.id.text_headword);
        text_pron_us = findViewById(R.id.text_pron_us);
        text_pron_uk = findViewById(R.id.text_pron_uk);
        list_meaning = findViewById(R.id.list_meaning);
        button_pron_us = findViewById(R.id.button_pron_us);
        button_pron_uk = findViewById(R.id.button_pron_uk);
        button_pron_us.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                media_play(typicalMeaning.PronUS, PronType.UK);
            }
        });
        button_pron_uk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                media_play(typicalMeaning.PronUK, PronType.US);
            }
        });
    }

    private void init_data() {
        if (word.Meanings != null) {
            if (word.Meanings.size() > 0){
                this.typicalMeaning = word.Meanings.get(0);
            }
            else {
                typicalMeaning = new Meaning();
                typicalMeaning.PronUS = new Pron();
                typicalMeaning.PronUS.Pron_String = "";
                button_pron_us.setEnabled(false);
                button_pron_us.setImageResource(R.drawable.ic_action_volume_off);
                typicalMeaning.PronUK = new Pron();
                typicalMeaning.PronUK.Pron_String = "";
                button_pron_uk.setEnabled(false);
                button_pron_uk.setImageResource(R.drawable.ic_action_volume_off);
                typicalMeaning.Usages = new ArrayList<>();
            }
        }

        text_headword.setText(this.word.Word_String);
        if (typicalMeaning.PronUS != null) {
            text_pron_us.setText(this.typicalMeaning.PronUS.Pron_String);
        } else {
            button_pron_us.setEnabled(false);
            button_pron_us.setImageResource(R.drawable.ic_action_volume_off);
        }
        if (typicalMeaning.PronUK != null) {
            text_pron_uk.setText(this.typicalMeaning.PronUK.Pron_String);
        } else {
            button_pron_uk.setEnabled(false);
            button_pron_uk.setImageResource(R.drawable.ic_action_volume_off);
        }

        meaningRecyclerAdapter = new MeaningRecyclerAdapter();
        LinearLayoutManager lm = new LinearLayoutManager(this);
        lm.setOrientation(RecyclerView.VERTICAL);
        list_meaning.setLayoutManager(lm);
        list_meaning.setItemAnimator(new DefaultItemAnimator());
        list_meaning.addItemDecoration(new DividerItemDecoration(list_meaning.getContext(),
                lm.getOrientation()));
        list_meaning.setAdapter(meaningRecyclerAdapter);

        meaningRecyclerAdapter.addItems(word.Meanings);
        meaningRecyclerAdapter.notifyDataSetChanged();
    }

    @Override
    public void setWord(Word word) {
        this.word = word;
        meaningRecyclerAdapter.notifyDataSetChanged();
    }

    private void media_play(Pron pron, PronType pronType) {
        if (pron.Pron_Data == null)
            return;
        try {
            mediaPlayer = new MediaPlayer();
//            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            if (pron.Pron_Extension == PronExtension.HYPERLINK){
                // hyperlink handle
                mediaPlayer.setDataSource(pron.Pron_Data);
            } else if (pron.Pron_Extension == PronExtension.BASE64) {
                // base64 handle
                mediaPlayer.setDataSource("data:audio/mpeg;base64," + pron.Pron_Data);
            }
            mediaPlayer.prepare();
            mediaPlayer.start();
            Toast.makeText(this, this.word.Word_String + "의 " + util.pronTypeToString(pronType) + "식 발음을 재생합니다.", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

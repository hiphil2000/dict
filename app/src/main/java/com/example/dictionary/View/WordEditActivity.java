package com.example.dictionary.View;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dictionary.Model.RoomDB.Entity.Meaning;
import com.example.dictionary.Model.RoomDB.Entity.Word;
import com.example.dictionary.Presenter.WordEditPresenter;
import com.example.dictionary.Presenter.WordEditPresenterIpml;
import com.example.dictionary.R;
import com.example.dictionary.View.RecycleAdapter.WordEditAdapter;

import java.util.ArrayList;

public class WordEditActivity extends AppCompatActivity implements WordEditPresenter.View {
    private Word currentWord;
    private TextView edit_headword;
    private Button button_finish_edit;
    private ImageButton edit_add_wordinfo;
    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;

    private WordEditPresenter presenter;
    private WordEditAdapter adapter;
    private boolean isCreateMode;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_edit);
        presenter = new WordEditPresenterIpml(WordEditActivity.this);
        presenter.setView(this);
        intent = getIntent();
        this.currentWord = (Word) this.intent.getSerializableExtra("wordData");
        this.isCreateMode = this.intent.getBooleanExtra("isCreateMode", false);
        if (isCreateMode) {
            currentWord = new Word(true);
        }

        setResult(1);
        init_view();
        if (!isCreateMode)
            init_data();

    }

    private void init_view() {
        this.recyclerView = findViewById(R.id.recycle_wordinfo_list);
        this.edit_headword = findViewById(R.id.edit_headword);
        this.button_finish_edit = findViewById(R.id.button_finish_edit);
        this.button_finish_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commitAndQuit();
            }
        });
        this.edit_add_wordinfo = findViewById(R.id.edit_add_wordinfo);
        edit_add_wordinfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter.addEmptyItem();
            }
        });

        if (isCreateMode) {
            button_finish_edit.setText(R.string.button_finish_create);
        }

        if (isCreateMode)
            this.adapter = new WordEditAdapter();
        else
            this.adapter = new WordEditAdapter(currentWord);
        this.layoutManager = new LinearLayoutManager(this);
        this.layoutManager.setOrientation(RecyclerView.VERTICAL);

        this.recyclerView.setLayoutManager(this.layoutManager);
        this.recyclerView.setItemAnimator(new DefaultItemAnimator());
        this.recyclerView.addItemDecoration(new DividerItemDecoration(this.recyclerView.getContext(),
                this.layoutManager.getOrientation()));
        this.recyclerView.setAdapter(this.adapter);
    }

    private void init_data() {
        this.edit_headword.setText(currentWord.Word_String);
        //this.adapter.addItems((ArrayList<Meaning>) this.currentWord.Meanings, false);
    }

    @Override
    public void onBackPressed() {
        commitAndQuit();
    }

    private void commitAndQuit() {
        currentWord.Word_String = edit_headword.getText().toString();
        adapter.commit();
        if (adapter.isDirty()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(WordEditActivity.this);
            builder.setMessage("변경사항이 있습니다. 저장하시겠습니까?")
                    .setPositiveButton("예", ondestroy_onclick)
                    .setNegativeButton("아니오", ondestroy_onclick)
                    .show();
        }
        else
            WordEditActivity.this.finish();
    }

    private DialogInterface.OnClickListener ondestroy_onclick = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            if (which == DialogInterface.BUTTON_POSITIVE) {
                if(isCreateMode){
                    for(Meaning mn_item : adapter.getData()) {
                        mn_item.Word_ID = currentWord.Word_ID;
                        currentWord.Meanings.add(mn_item);
                    }
                    presenter.addWord(currentWord);
                }
                else {
                    currentWord.Meanings = adapter.getData();
                    presenter.commitChanges(currentWord);
                }
                WordEditActivity.this.finish();
            } else {
                WordEditActivity.this.finish();
            }
        }
    };

    @Override
    public void pushResultMessage(String message) {
        final String msg = message;
        Handler mainHandle = new Handler(Looper.getMainLooper());
        mainHandle.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
            }
        });
    }
}

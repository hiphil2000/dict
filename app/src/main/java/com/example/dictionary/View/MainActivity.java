package com.example.dictionary.View;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.dictionary.Model.RoomDB.Entity.SearchType;
import com.example.dictionary.Model.RoomDB.Entity.Video;
import com.example.dictionary.Model.RoomDB.Entity.Word;
import com.example.dictionary.Presenter.MainPresenter;
import com.example.dictionary.Presenter.MainPresenterImpl;
import com.example.dictionary.R;
import com.example.dictionary.View.Fragment.FragmentHistory;
import com.example.dictionary.View.Fragment.FragmentQuery;
import com.example.dictionary.View.Fragment.FragmentYoutube;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, MainPresenter.View {
    private MainPresenter mainPresenter;

    //private FrameLayout
    private FragmentManager fragManager;
    private FragmentQuery fragWeb;
    private FragmentQuery fragLocal;
    private FragmentYoutube fragYoutube;
    private FragmentHistory fragHistory;
    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_master);
        mainPresenter = new MainPresenterImpl(this);
        mainPresenter.setView(this);
        init_view();
    }

    private void init_view() {
        fragManager = getSupportFragmentManager();
        fragWeb = new FragmentQuery(this, SearchType.WebOnly, R.id.nav_query);
        fragLocal = new FragmentQuery(this, SearchType.LocalOnly, R.id.nav_note);
        fragYoutube = new FragmentYoutube(this, false, R.id.nav_youtube);
        fragHistory = new FragmentHistory(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fab = (FloatingActionButton) findViewById(R.id.button_add_word);
        fab.setOnClickListener(button_add_custom_word);

        BottomNavigationView nav = findViewById(R.id.nav_bottom);
        nav.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        switchFragment(1);
    }
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.nav_query:
                    switchFragment(1);
                    return true;
                case R.id.nav_note:
                    switchFragment(2);
                    return true;
                case R.id.nav_youtube:
                    switchFragment(3);
                    return true;
                case R.id.nav_history:
                    switchFragment(4);
                    return true;
            }
            return false;
        }
    };

    private void switchFragment(int fragno) {
        Fragment fr;
        if (fragno == 2) {
            fab.show();
        } else {
            fab.hide();
        }
        switch(fragno) {
            case 1:
                fr = fragWeb;
                break;
            case 2:
                fr = fragLocal;
                break;
            case 3:
                fr = fragYoutube;
                break;
            default:
            case 4:
                fr = fragHistory;
                break;
        }
        FragmentTransaction fragmentTransaction = fragManager.beginTransaction();
        fragmentTransaction.replace(R.id.nav_frame, fr);
        fragmentTransaction.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nuke_table) {

            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setMessage("단어장을 정말 비우시겠습니까?\n되돌릴 수 없는 작업입니다.")
                    .setPositiveButton("예", menu_nuke_table_onclick)
                    .setNegativeButton("아니오", menu_nuke_table_onclick)
                    .show();
            mainPresenter.nukeNote();
            return true;
        } else if (id == R.id.option) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    DialogInterface.OnClickListener menu_nuke_table_onclick = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            if (which == DialogInterface.BUTTON_POSITIVE) {
                mainPresenter.nukeNote();
                Toast.makeText(MainActivity.this, "단어장을 비웠습니다.", Toast.LENGTH_SHORT).show();
            }
        }
    };

    private View.OnClickListener button_add_custom_word = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(MainActivity.this, WordEditActivity.class);
            intent.putExtra("wordData", new Word());
            intent.putExtra("isCreateMode", true);
            startActivityForResult(intent, 1);
        }
    };

    //endregion

    @Override
    public void addQueryResultList(List<Word> wordInfos) {
    }

    @Override
    public void addVideoQueryResultList(List<Video> videos) {
        // now use here
    }

    @Override
    public void addViewLogResultList(List<Word> words) {
//        if (adapter_history != null) {
//            adapter_history.addItems(words);
//            adapter_history.notifyDataSetChanged();
//        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode / 1000 == 1) {
            fragYoutube.onActivityResult(requestCode, resultCode, data);
        }
//        if (requestCode == 1) {
//            adapter_query.clearItems();
//            mainPresenter.showNotes();
//        }
    }

    //region Override from MainPresenter.View
    @Override
    public void setQueryResultList(List<Word> bases) {
//        adapter_query.clearItems();
//        adapter_query.addItems(bases);
//        adapter_query.notifyDataSetChanged();
    }

    @Override
    public void pushResultMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void showDialog(String type, Object... params) {

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        return false;
    }
    //endregion
}

package com.example.dictionary.View;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dictionary.Model.RoomDB.Entity.Log;
import com.example.dictionary.Model.RoomDB.Entity.Word;
import com.example.dictionary.Presenter.MainPresenter;
import com.example.dictionary.Presenter.MainPresenterImpl;
import com.example.dictionary.R;
import com.example.dictionary.View.Fragment.FragmentHistory;
import com.example.dictionary.View.Fragment.FragmentQuery;
import com.example.dictionary.View.RecycleAdapter.HistoryListAdapter;
import com.example.dictionary.View.RecycleAdapter.QueryListAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, MainPresenter.View {
    private MainPresenter mainPresenter;
//    private ImageButton button_query;
//    private EditText edit_query;
//    private ProgressBar progress_query;
//
//
//    private QueryListAdapter adapter_query;
//    private HistoryListAdapter adapter_history;
//    private RecyclerView.Adapter<?> adapter_now;
//
//    private RecyclerView list_query;
//    private LinearLayoutManager layoutManager;
//
//    private boolean searchLocalOnly;
//    private int responseCounter;

    //private FrameLayout
    private FragmentManager fragManager;
    private FragmentQuery fragWeb;
    private FragmentQuery fragLocal;
    private FragmentHistory fragHistory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_master);
        mainPresenter = new MainPresenterImpl(this);
        mainPresenter.setView(this);
        init_view();
//        responseCounter = 0;
    }

    private void init_view() {
        fragManager = getSupportFragmentManager();
        fragWeb = new FragmentQuery(this, false);
        fragLocal = new FragmentQuery(this, true);
        fragHistory = new FragmentHistory(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(button_add_custom_word);

//        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
//                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
//        drawer.addDrawerListener(toggle);
//        toggle.syncState();

//        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
//        navigationView.setNavigationItemSelectedListener(this);

//        this.button_query = findViewById(R.id.button_query);
//        this.button_query.setOnClickListener(this.button_query_onclick);

//        this.edit_query = findViewById(R.id.edit_query);
//
//        this.adapter_query = new QueryListAdapter();
//        this.adapter_history = new HistoryListAdapter();
//        this.layoutManager = new LinearLayoutManager(this);
//        this.layoutManager.setOrientation(RecyclerView.VERTICAL);
//
//        this.list_query = findViewById(R.id.list_query);
//        this.list_query.setLayoutManager(this.layoutManager);
//        this.list_query.setItemAnimator(new DefaultItemAnimator());
//        this.list_query.addItemDecoration(new DividerItemDecoration(this.list_query.getContext(),
//                this.layoutManager.getOrientation()));
//        this.list_query.setAdapter(this.adapter_query);
//
//        this.progress_query = findViewById(R.id.progress_query);
//        onNavigationItemSelected(navigationView.getMenu().getItem(0));
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
                case R.id.nav_history:
                    switchFragment(3);
                    return true;
            }
            return false;
        }
    };

    private void switchFragment(int fragno) {
        Fragment fr;

        switch(fragno) {
            case 1:
                fr = fragWeb;
                break;
            case 2:
                fr = fragLocal;
                break;
            default:
            case 3:
                fr = fragHistory;
                break;
        }
        FragmentTransaction fragmentTransaction = fragManager.beginTransaction();
        fragmentTransaction.replace(R.id.nav_frame, fr);
        fragmentTransaction.commit();
    }

    //region Override from OnNavigationItemSelectedListener
//    @Override
//    public void onBackPressed() {
//        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//        if (drawer.isDrawerOpen(GravityCompat.START)) {
//            drawer.closeDrawer(GravityCompat.START);
//        } else {
//            super.onBackPressed();
//        }
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.nuke_table) {

            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setMessage("단어장을 정말 비우시겠습니까?\n되돌릴 수 없는 작업입니다.")
                    .setPositiveButton("예", menu_nuke_table_onclick)
                    .setNegativeButton("아니오", menu_nuke_table_onclick)
                    .show();
            mainPresenter.nukeNote();
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
//
//    @Override
//    public boolean onNavigationItemSelected(MenuItem item) {
//        // Handle navigation view item clicks here.
//        int id = item.getItemId();
//
//        if (id == R.id.nav_query) {
//            list_query.setAdapter(this.adapter_query);
//            adapter_now = adapter_query;
//            adapter_query.clearItems();
//            searchLocalOnly = false;
//            edit_query.setHint(R.string.nav_query);
//        } else if (id == R.id.nav_note) {
//            list_query.setAdapter(this.adapter_query);
//            adapter_now = adapter_query;
//            adapter_query.clearItems();
//            mainPresenter.showNotes();
//            searchLocalOnly = true;
//            edit_query.setHint(R.string.nav_note);
//        } else if (id == R.id.nav_history) {
//            list_query.setAdapter(this.adapter_history);
//            adapter_now = adapter_history;
//            adapter_history.clearItems();
//            mainPresenter.getViewLogs();
//            searchLocalOnly = true;
//            edit_query.setHint(R.string.nav_history);
//        }
//
//        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//        drawer.closeDrawer(GravityCompat.START);
//        return true;
//    }
    //endregion

    //region EventListeners
//    private View.OnClickListener button_query_onclick = new View.OnClickListener() {
//        @Override
//        public void onClick(View v) {
//            if (edit_query.getText().length() <= 0) {
//               return;
//            }
//            adapter_query.clearItems();
//            mainPresenter.search(edit_query.getText().toString(), searchLocalOnly);
//            progress_query.setVisibility(View.VISIBLE);
//        }
//    };

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
//        // adapter_now가 adapter_history일 때
//        if (adapter_now.getClass().isAssignableFrom(adapter_history.getClass())) {
//            if (adapter_history != null) {
//                adapter_history.addItems(wordInfos);
//                adapter_history.notifyDataSetChanged();
//            }
//        }
//        else if (adapter_now.getClass().isAssignableFrom(adapter_query.getClass())) {
//            if (adapter_query != null) {
//                adapter_query.addItems(wordInfos);
//                adapter_query.notifyDataSetChanged();
//                responseCounter++;
//                if(searchLocalOnly && responseCounter >= 1) {
//                    progress_query.setVisibility(View.GONE);
//                    responseCounter = 0;
//                } else if(!searchLocalOnly && responseCounter >= 2) {
//                    progress_query.setVisibility(View.GONE);
//                    responseCounter = 0;
//                }
//            }
//        }
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
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        return false;
    }
    //endregion
}

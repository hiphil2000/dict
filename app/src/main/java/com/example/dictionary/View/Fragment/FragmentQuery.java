package com.example.dictionary.View.Fragment;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dictionary.Model.RoomDB.Entity.Video;
import com.example.dictionary.Model.RoomDB.Entity.Word;
import com.example.dictionary.Presenter.MainPresenter;
import com.example.dictionary.Presenter.MainPresenterImpl;
import com.example.dictionary.R;
import com.example.dictionary.View.RecycleAdapter.QueryListAdapter;
import com.example.dictionary.View.RecycleAdapter.VideoQueryListAdapter;

import java.util.List;

public class FragmentQuery extends Fragment implements MainPresenter.View {
    // layout components
    private EditText edit_query;
    private ImageButton button_query;
    private ProgressBar progress_query;

    // recycler, adapter
    private RecyclerView list_query;
    private RecyclerView.Adapter<?> adapter;

    // presenter
    private MainPresenter presenter;

    private String type;
    private int nav_id;
    private boolean isLocal;
    private boolean isSearching;
    private int searchCount;

    public FragmentQuery(Activity activity, boolean isLocal, int nav_id) {
        presenter = new MainPresenterImpl(activity);
        presenter.setView(this);
        this.isLocal = isLocal;
        this.nav_id = nav_id;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_query, container, false);
        edit_query = view.findViewById(R.id.edit_query);
        String hint = "";
        switch(nav_id) {
            case R.id.nav_query:
                hint = getString(R.string.nav_query);
                break;
            case R.id.nav_note:
                hint = getString(R.string.nav_note);
                break;
            case R.id.nav_history:
                hint = getString(R.string.nav_history);
                break;
            case R.id.nav_youtube:
                hint = getString(R.string.nav_youtube);
                break;
        }
        edit_query.setHint(hint);
        button_query = view.findViewById(R.id.button_query);
        button_query.setOnClickListener(button_query_onclick);
        progress_query = view.findViewById(R.id.progress_query);
        if (nav_id == R.id.nav_youtube){
            adapter = new VideoQueryListAdapter();
            ((VideoQueryListAdapter)adapter).setPresenter(presenter);

        } else {
            adapter = new QueryListAdapter();
            ((QueryListAdapter)adapter).setPresenter(presenter);

        }
        list_query = view.findViewById(R.id.list_query);
        list_query.setLayoutManager(new LinearLayoutManager(getContext()));
        list_query.setItemAnimator(new DefaultItemAnimator());
        list_query.setAdapter(adapter);

        return view;
    }

    private View.OnClickListener button_query_onclick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (isSearching == true)
                return;
            if (nav_id == R.id.nav_youtube) {
                ((VideoQueryListAdapter)adapter).clearItems();
                presenter.videoSearch(edit_query.getText().toString().trim(), isLocal);

            } else {
                ((QueryListAdapter)adapter).clearItems();
                presenter.search(edit_query.getText().toString().trim(), isLocal);

            }
            isSearching = true;
            searchCount = 0;
            progress_query.setVisibility(View.VISIBLE);
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        if (isLocal == true) {
            if (nav_id == R.id.nav_youtube) {
                ((VideoQueryListAdapter)adapter).clearItems();
                presenter.showVideos();

            } else {
                ((QueryListAdapter)adapter).clearItems();
                presenter.showNotes();

            }
        }
    }

    @Override
    public void addQueryResultList(List<Word> words) {
        ((QueryListAdapter)adapter).addItems(words);
        adapter.notifyDataSetChanged();
        searchCount++;
        if ((isLocal == true && searchCount > 0) || (isLocal == false && searchCount > 1)) {
            progress_query.setVisibility(View.GONE);
            searchCount = 0;
            isSearching = false;
        }
    }

    @Override
    public void addVideoQueryResultList(List<Video> videos) {
        ((VideoQueryListAdapter)adapter).addItems(videos);
        adapter.notifyDataSetChanged();
        searchCount++;
        if ((isLocal == true && searchCount > 0) || (isLocal == false && searchCount > 1)) {
            progress_query.setVisibility(View.GONE);
            searchCount = 0;
            isSearching = false;
        }
    }

    @Override
    public void addViewLogResultList(List<Word> words) {
        // not using here
    }

    @Override
    public void setQueryResultList(List<Word> words) {

    }

    @Override
    public void pushResultMessage(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }
}

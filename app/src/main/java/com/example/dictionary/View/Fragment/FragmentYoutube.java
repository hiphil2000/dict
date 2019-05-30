package com.example.dictionary.View.Fragment;

import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
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

import com.example.dictionary.Model.RoomDB.Entity.SearchType;
import com.example.dictionary.Model.RoomDB.Entity.Video;
import com.example.dictionary.Model.RoomDB.Entity.Word;
import com.example.dictionary.Presenter.MainPresenter;
import com.example.dictionary.Presenter.MainPresenterImpl;
import com.example.dictionary.Presenter.YoutubePresenter;
import com.example.dictionary.Presenter.YoutubePresenterImpl;
import com.example.dictionary.R;
import com.example.dictionary.View.ExoPlayerActivity;
import com.example.dictionary.View.RecycleAdapter.VideoQueryListAdapter;
import com.example.dictionary.View.YoutubeLoginActivity;

import java.util.List;

import static android.app.Activity.RESULT_OK;
import static com.example.dictionary.Model.YoutubeDataApi.Statics.NEED_GOOGLE_AUTH;

public class FragmentYoutube extends Fragment implements YoutubePresenter.View {
    public static String TAG = "FragmentYoutube";

    // layout components
    private EditText edit_query;
    private ImageButton button_query;
    private ProgressBar progress_query;

    // recycler, adapter
    private RecyclerView list_query;
    private VideoQueryListAdapter adapter;

    // presenter
    private YoutubePresenter presenter;

    private int nav_id;
    private boolean isLocal;
    private boolean isSearching;
    private int searchCount;

    public FragmentYoutube(Activity activity, boolean isLocal, int nav_id) {
        presenter = new YoutubePresenterImpl(activity);
        presenter.setView(this);
        this.isLocal = isLocal;
        this.nav_id = nav_id;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!((YoutubePresenterImpl)presenter).checkCredential()) {
            Toast.makeText(getContext(), "구글 인증이 필요합니다.", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getContext(), YoutubeLoginActivity.class);
            getActivity().startActivityForResult(intent, NEED_GOOGLE_AUTH);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_query, container, false);
        edit_query = view.findViewById(R.id.edit_query);
        edit_query.setHint(R.string.nav_youtube);
        button_query = view.findViewById(R.id.button_query);
        button_query.setOnClickListener(button_query_onclick);
        progress_query = view.findViewById(R.id.progress_query);
        adapter = new VideoQueryListAdapter();
        adapter.setPresenter(presenter);
        list_query = view.findViewById(R.id.list_query);
        list_query.setLayoutManager(new LinearLayoutManager(getContext()));
        list_query.setItemAnimator(new DefaultItemAnimator());
        list_query.setAdapter(adapter);


        return view;
    }

    private View.OnClickListener button_query_onclick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            adapter.clearItems();
            presenter.videoSearch(edit_query.getText().toString().trim(), SearchType.Both);
            searchCount = 0;
            progress_query.setVisibility(View.VISIBLE);
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        if (isLocal == true) {
            adapter.clearItems();
            presenter.showVideos();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        presenter.setModelPresenter();
    }

    @Override
    public void addVideoQueryResultList(List<Video> videos) {
        adapter.addItems(videos);
        adapter.notifyDataSetChanged();
        searchCount++;
        if ((isLocal == true && searchCount > 0) || (isLocal == false && searchCount > 1)) {
            progress_query.setVisibility(View.GONE);
            searchCount = 0;
            isSearching = false;
        }

    }

    @Override
    public void setVideo(Video video) {

    }

    @Override
    public void pushResultMessage(String message) {
        final String msg = message;
        Handler handle = new Handler(getActivity().getMainLooper());
        handle.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
                if (isSearching) {
                    isSearching = false;
                    progress_query.setVisibility(View.GONE);
                }
            }
        });

    }

    @Override
    public void onActivityResult(
            int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case NEED_GOOGLE_AUTH:
                if (resultCode != RESULT_OK) {
                    pushResultMessage("구글 인증을 취소했습니다.");
                } else {
                    pushResultMessage("인증됐습니다.");
                    presenter.setModelPresenter();
                }
                break;
        }
    }

    //endregion
}

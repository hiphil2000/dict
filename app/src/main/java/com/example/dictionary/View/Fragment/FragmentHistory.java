package com.example.dictionary.View.Fragment;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.dictionary.Model.RoomDB.Entity.Video;
import com.example.dictionary.Model.RoomDB.Entity.Word;
import com.example.dictionary.Presenter.MainPresenter;
import com.example.dictionary.Presenter.MainPresenterImpl;
import com.example.dictionary.R;
import com.example.dictionary.View.TableAdapter.WordTableAdapter;

import java.util.ArrayList;
import java.util.List;

import de.codecrafters.tableview.SortableTableView;
import de.codecrafters.tableview.TableDataAdapter;
import de.codecrafters.tableview.TableView;
import de.codecrafters.tableview.toolkit.SimpleTableHeaderAdapter;

public class FragmentHistory extends Fragment implements MainPresenter.View {
    // layout components
    private WebView web_graph;
    private EditText edit_query;
    private ImageButton button_query;
    private Spinner history_option;
    private TableView<Word> s_table_view;

    // presenter
    private MainPresenter presenter;
    private TableDataAdapter<Word> adapter;
    private List<Word> data;

    private Activity activity;

    private final String[] HEADERS = {"단어", "종류", "날짜", "횟수"};

    public FragmentHistory(Activity activity) {
        this.activity = activity;
        presenter = new MainPresenterImpl(activity);
        presenter.setView(this);
        data = new ArrayList<>();
        adapter = new WordTableAdapter(activity.getApplicationContext(), data);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);
        this.web_graph = view.findViewById(R.id.web_graph);
        this.web_graph.getSettings().setDomStorageEnabled(true);
        this.web_graph.getSettings().setJavaScriptEnabled(true);
        this.web_graph.loadUrl("file:///android_asset/www/graph.html");
        this.web_graph.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                view.loadUrl("javascript:fn_makeGraph('bar', '', '', '');");
                super.onPageFinished(view, url);
            }
        });
        this.edit_query = view.findViewById(R.id.edit_query);
        this.button_query = view.findViewById(R.id.button_query);
        this.button_query.setOnClickListener(button_query_onclick);
        this.history_option = view.findViewById(R.id.history_option);
        this.s_table_view = view.findViewById(R.id.s_table_view);
        this.s_table_view.setDataAdapter(adapter);

        return view;
    }

    private View.OnClickListener button_query_onclick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            data.clear();
            presenter.getViewLogs();
        }
    };

    private void makeGraph(String type, List<Word> data) {

        //String.format("javascript:'%s', '%s'", type
    }

    @Override
    public void onResume() {
        super.onResume();
        data.clear();
        presenter.getViewLogs();
    }

    @Override
    public void addQueryResultList(List<Word> wordInfos) {
    }

    @Override
    public void addVideoQueryResultList(List<Video> videos) {
    }

    @Override
    public void addViewLogResultList(List<Word> words) {
        List<Word> temp = words;
        for(int i = temp.size() - 1; i>= 0; i--) {
            Word word = temp.get(i);
            if (word.getLastestLog() == null) {
                words.remove(word);
            }
        }
        data.addAll(temp);
        adapter.notifyDataSetChanged();
        s_table_view.setHeaderAdapter(new SimpleTableHeaderAdapter(activity.getApplicationContext(), HEADERS));
    }

    @Override
    public void setQueryResultList(List<Word> wordInfos) {

    }

    @Override
    public void pushResultMessage(String message) {

    }
}

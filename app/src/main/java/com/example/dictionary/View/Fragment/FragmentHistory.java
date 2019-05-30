package com.example.dictionary.View.Fragment;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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

import com.example.dictionary.Model.RoomDB.Entity.Log;
import com.example.dictionary.Model.RoomDB.Entity.LogType;
import com.example.dictionary.Model.RoomDB.Entity.Video;
import com.example.dictionary.Model.RoomDB.Entity.Word;
import com.example.dictionary.Presenter.MainPresenter;
import com.example.dictionary.Presenter.MainPresenterImpl;
import com.example.dictionary.R;
import com.example.dictionary.View.TableAdapter.WordTableAdapter;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    private final String[] HEADERS = {"단어", "날짜", "조회 횟수", "암기 횟수"};

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
        web_graph.getSettings().setDomStorageEnabled(true);
        web_graph.getSettings().setJavaScriptEnabled(true);
        web_graph.loadUrl("file:///android_asset/www/graph.html");

        this.edit_query = view.findViewById(R.id.edit_query);
        edit_query.setVisibility(View.GONE);
        this.button_query = view.findViewById(R.id.button_query);
        button_query.setVisibility(View.GONE);
        this.button_query.setOnClickListener(button_query_onclick);
        this.history_option = view.findViewById(R.id.history_option);
        history_option.setVisibility(View.GONE);
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

    private void makeGraph(final String type, List<Word> data) {

        HashMap<String, List<Log>> dataByMonth = new HashMap<>();
        for(Word word : data) {
            if (word.Logs != null) {
                for(Log log : word.Logs) {
                    Date date= log.Log_Date;
                    Date now = new Date();
                    if (date.getYear() - now.getYear() < 0 || date.getMonth() - now.getMonth() <= -3)
                        return;
                    //String key = (1900 + date.getYear()) + "" + date.getMonth();
                    String key = word.Word_String;
                    if (dataByMonth.get(key) == null)
                        dataByMonth.put(key, new ArrayList<Log>());

                    dataByMonth.get(key).add(log);
                }
            }
        }

        List<String> headerList = new ArrayList<>();
        List<Integer> detialList = new ArrayList<>();
        List<Integer> memorizeList = new ArrayList<>();

        int i = 0;
        for(Map.Entry<String, List<Log>> item : dataByMonth.entrySet()) {
            headerList.add(item.getKey());
            detialList.add(0);
            memorizeList.add(0);
            for(Log log : item.getValue()) {
                if (log.LogType == LogType.WordLocalDetail || log.LogType == LogType.WordWebDetail) {
                    detialList.set(i, detialList.get(i) + 1);
                } else if (log.LogType == LogType.WordMemorized) {
                    memorizeList.set(i, memorizeList.get(i) + 1);
                }
            }
            i++;
        }

        String headerStr = "";
        for(String str : headerList) {
            headerStr += str + ",";
        }
        if (headerStr.length() > 0)
            headerStr = headerStr.substring(0, headerStr.length() - 1);

        String detailCntStr = "";
        for(Integer integer : detialList) {
            detailCntStr += integer + ",";
        }
        if (detailCntStr.length() > 0)
            detailCntStr = detailCntStr.substring(0, detailCntStr.length() - 1);

        String memorizeStr = "";
        for(Integer integer : memorizeList) {
            memorizeStr += integer + ",";
        }
        if (memorizeStr.length() > 0)
            memorizeStr = memorizeStr.substring(0, memorizeStr.length() - 1);


        final String finalHeaderStr = headerStr;
        final String finalDetailCtnStr = detailCntStr;
        final String finalMemorizeStr = memorizeStr;
        web_graph.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                view.loadUrl(String.format("javascript:fn_makeGraph('%s', '%s', '%s', '%s');",
                        type, finalHeaderStr, finalDetailCtnStr, finalMemorizeStr));
                super.onPageFinished(view, url);
            }
        });
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
        makeGraph("bar", data);
    }

    @Override
    public void setQueryResultList(List<Word> wordInfos) {

    }

    @Override
    public void pushResultMessage(String message) {

    }

    @Override
    public void showDialog(String type, Object... params) {

    }
}

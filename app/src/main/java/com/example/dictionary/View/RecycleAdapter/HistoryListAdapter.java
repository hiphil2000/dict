package com.example.dictionary.View.RecycleAdapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dictionary.Model.RoomDB.Entity.Log;
import com.example.dictionary.Model.RoomDB.Entity.LogType;
import com.example.dictionary.Model.RoomDB.Entity.Word;
import com.example.dictionary.Presenter.MainPresenter;
import com.example.dictionary.R;
import com.example.dictionary.View.WordDetailActivity;
import com.example.dictionary.util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class HistoryListAdapter extends RecyclerView.Adapter<HistoryListAdapter.HistoryListHolder> {
    private List<Word> data;
    private MainPresenter presenter;
    private Context context;
    private SimpleDateFormat dateFormat;

    public HistoryListAdapter() {
        this.data = new ArrayList<>();
        dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    }

    public void setPresenter(MainPresenter presenter) { this.presenter = presenter; }

    public void addItem(Word item) {
        if (item != null) {
            this.data.add(item);
        }
        orderItems();
    }

    public void addItems(Collection<Word> items) {
        for(Word item : items) {
            if (item != null) {
                if (item.Logs != null)
                    if(item.Logs.size() > 0)
                        this.data.add(item);
            }
        }
        orderItems();
    }

    public void clearItems() {
        this.data.clear();
        notifyDataSetChanged();
    }

    public void orderItems() {
        Collections.sort(data, new util.WordDescender());
    }

    @NonNull
    @Override
    public HistoryListHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        this.context = parent.getContext();
        View view = LayoutInflater.from(this.context).inflate(R.layout.history_list_item, parent, false);
        HistoryListHolder holder = new HistoryListHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryListHolder holder, int position) {
        final int pos = position;
        final Word nowWord = data.get(pos);
        if (nowWord != null) {
            holder.text_headword.setText(nowWord.Word_String);
            Log lastLog = nowWord.getLastestLog();
            if (lastLog.LogType == LogType.WordLocalDetail)
                holder.text_from.setText(this.context.getString(R.string.word_from_local));
            else if (lastLog.LogType == LogType.WordWebDetail)
                holder.text_from.setText(this.context.getString(R.string.word_from_web));
            if (null != nowWord.getLastestLog())
                holder.text_seen.setText(dateFormat.format(lastLog.Log_Date));
            else
                holder.text_seen.setText("");
            holder.text_seen_count.setText(String.valueOf(nowWord.Logs.size()));
        }

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, WordDetailActivity.class);
                intent.putExtra("wordData", nowWord);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class HistoryListHolder extends RecyclerView.ViewHolder{
        protected View view;
        protected TextView text_headword;
        protected TextView text_from;
        protected TextView text_seen;
        protected TextView text_seen_count;

        public HistoryListHolder(@NonNull View itemView){
            super(itemView);
            this.view = itemView;
            this.text_headword = itemView.findViewById(R.id.text_headword);
            this.text_from = itemView.findViewById(R.id.text_from);
            this.text_seen = itemView.findViewById(R.id.text_seen);
            this.text_seen_count = itemView.findViewById(R.id.text_seen_count);
        }
    }
}

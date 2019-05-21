package com.example.dictionary.View.RecycleAdapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dictionary.Model.RoomDB.Entity.Usage;
import com.example.dictionary.R;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class UsageRecyclerAdapter extends RecyclerView.Adapter<UsageRecyclerAdapter.WordMeaningHolder> {
    private List<Usage> data;
    private Context context;

    public UsageRecyclerAdapter() {
        data = new ArrayList<>();
    }

    public void addItems(Collection<Usage> items) {
        if(items == null)
            return;
        for(Usage item : items) {
            if (item != null){
                data.add(item);
            }
        }
    }

    @NonNull
    @Override
    public WordMeaningHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d("UsageRecyclerAdapter","onCreateViewHolder");
        this.context = parent.getContext();
        View view = LayoutInflater.from(this.context).inflate(R.layout.listitem_usage, parent, false);
        WordMeaningHolder holder = new WordMeaningHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull WordMeaningHolder holder, int position) {
        Log.d("UsageRecyclerAdapter","onBindViewHolder");
        final int pos = position;
        final Usage nowUsage = data.get(pos);
        if(nowUsage != null) {
            holder.text_usage.setText(nowUsage.Usage_String);
            holder.text_usage_seq.setText("" + nowUsage.Usage_Seq);
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class WordMeaningHolder extends RecyclerView.ViewHolder {
        protected View view;
        protected TextView text_usage;
        protected TextView text_usage_seq;
        public WordMeaningHolder(@NonNull View itemView) {
            super(itemView);
            this.view = itemView;
            this.text_usage = itemView.findViewById(R.id.text_usage);
            this.text_usage_seq = itemView.findViewById(R.id.text_usage_seq);
        }
    }
}

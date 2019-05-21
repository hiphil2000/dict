package com.example.dictionary.View.RecycleAdapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dictionary.Model.RoomDB.Entity.Meaning;
import com.example.dictionary.R;
import com.example.dictionary.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class MeaningRecyclerAdapter extends RecyclerView.Adapter<MeaningRecyclerAdapter.WordDetailHolder> {
    private List<Meaning> data;
    private List<UsageRecyclerAdapter> adapters;
    private Context context;
    static String TAG = "MeaningRecyclerAdapter";

    public MeaningRecyclerAdapter() {
        data = new ArrayList<>();
        adapters = new ArrayList<>();
    }

    public void addItems(Collection<Meaning> items) {
        for(Meaning item : items) {
            if (item != null) {
                data.add(item);
                adapters.add(new UsageRecyclerAdapter());
            }
        }
    }

    @NonNull
    @Override
    public WordDetailHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        this.context = parent.getContext();
        View view = LayoutInflater.from(this.context).inflate(R.layout.listitem_meaning, parent, false);
        WordDetailHolder holder = new WordDetailHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull WordDetailHolder holder, int position) {
        Log.d("MeaningRecyclerAdapter","onBindViewHolder");
        final int pos = position;
        final Meaning nowMeaning = data.get(pos);
        if (nowMeaning != null) {
            util.sysout(holder.view.getMeasuredHeight() + "dp");
            holder.text_mean_type.setText(util.wordTypeToString(nowMeaning.Meaning_Type));
            holder.text_mean_kr.setText(nowMeaning.Meaning_Kor);
            holder.text_mean_en.setText(nowMeaning.Meaning_Eng);

            LinearLayoutManager lm = new LinearLayoutManager(this.context);
            lm.setOrientation(RecyclerView.VERTICAL);

            holder.list_word_meaning.setLayoutManager(lm);
            holder.list_word_meaning.setItemAnimator(new DefaultItemAnimator());
            holder.list_word_meaning.addItemDecoration(
                    new DividerItemDecoration(holder.list_word_meaning.getContext(),
                            lm.getOrientation()));
            UsageRecyclerAdapter adapterUsage = adapters.get(pos);
            adapterUsage.addItems(nowMeaning.Usages);
            adapterUsage.notifyDataSetChanged();
            holder.list_word_meaning.setAdapter(adapterUsage);
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class WordDetailHolder extends RecyclerView.ViewHolder {
        protected View view;
        protected RecyclerView list_word_meaning;
        protected TextView text_mean_type;
        protected TextView text_mean_kr;
        protected TextView text_mean_en;

        public WordDetailHolder(@NonNull View itemView) {
            super(itemView);
            this.view = itemView;
            this.list_word_meaning = itemView.findViewById(R.id.list_meaning);
            this.text_mean_type = itemView.findViewById(R.id.text_mean_type);
            this.text_mean_kr = itemView.findViewById(R.id.text_mean_kr);
            this.text_mean_en = itemView.findViewById(R.id.text_mean_en);
        }
    }
}

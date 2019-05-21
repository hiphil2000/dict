package com.example.dictionary.View.RecycleAdapter;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dictionary.Model.RoomDB.Entity.Meaning;
import com.example.dictionary.Model.RoomDB.Entity.Pron;
import com.example.dictionary.Model.RoomDB.Entity.Usage;
import com.example.dictionary.Model.RoomDB.Entity.Word;
import com.example.dictionary.R;
import com.example.dictionary.util;

import java.util.ArrayList;

public class WordEditAdapter extends RecyclerView.Adapter<WordEditAdapter.WordEditHolder> {
    private ArrayList<Meaning> data;
    private ArrayList<Meaning> data_old;
    private Meaning typicalData;
    private String mean_ko;
    private String mean_en;
    private Context context;
    private ArrayList<UsageEditAdapter> usageEditAdapter;
    private boolean isDirty;

    public WordEditAdapter(Word word) {
        this.data = (ArrayList<Meaning>) word.Meanings;
        this.data_old = (ArrayList<Meaning>) data.clone();
        this.typicalData = data_old.get(0);
        this.mean_ko = typicalData.Meaning_Kor;
        this.mean_en = typicalData.Meaning_Eng;
        usageEditAdapter = new ArrayList<>();
    }

    public WordEditAdapter() {
        Word word = new Word(true);
        this.data = (ArrayList<Meaning>) new Word(true).Meanings;
        this.data_old = (ArrayList<Meaning>) new Word(true).Meanings;
        this.typicalData = new Meaning();
        this.mean_ko = typicalData.Meaning_Kor;
        this.mean_en = typicalData.Meaning_Eng;
        this.usageEditAdapter = new ArrayList<>();
    }

    public ArrayList<Meaning> getData() {
        return data;
    }

    public void setData(ArrayList<Meaning> data) {
        this.data = data;
    }

    public void commit() {
        int i = 0;
        for(Meaning mn_item : data) {
            usageEditAdapter.get(i).commit();
            String wi_mean_ko = mn_item.Meaning_Kor;
            String wi_mean_en = mn_item.Meaning_Eng;
            if (!wi_mean_ko.equals(mean_ko))
                isDirty = true;
            if (!wi_mean_en.equals(mean_en))
                isDirty = true;
            mn_item.Usages = usageEditAdapter.get(i).getData();
            i++;
        }
    }

    // RecyclerView에 새로운 데이터를 보여주기 위한 ViewHolder를 생성할 때 호출
    @NonNull
    @Override
    public WordEditHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        this.context = parent.getContext();
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.edit_wordinfo, parent, false);

        WordEditHolder wordEditHolder = new WordEditHolder(view);

        return wordEditHolder;
    }

    // Adapter의 특정 위치에 있는 데이터를 보여줘야할 때 호출
    @Override
    public void onBindViewHolder(@NonNull WordEditHolder holder, int position) {
        final int pos = position;
        Meaning nowMeaning = data.get(pos);
        final UsageEditAdapter ue_adapter = new UsageEditAdapter();
        usageEditAdapter.add(ue_adapter);

        if (nowMeaning.PronUS != null)
            holder.edit_pron_us.setText(nowMeaning.PronUS.Pron_String);
        if (nowMeaning.PronUK != null)
            holder.edit_pron_uk.setText(nowMeaning.PronUK.Pron_String);
        ArrayAdapter arrayAdapter = ArrayAdapter.createFromResource(context,
                                        R.array.part_of_word,
                                        android.R.layout.simple_spinner_item);
        holder.edit_type.setAdapter(arrayAdapter);
        holder.edit_mean_kr.setText(nowMeaning.Meaning_Kor);
        holder.edit_mean_en.setText(nowMeaning.Meaning_Eng);
        if (nowMeaning.Usages != null) {
            ArrayList<Usage> usages = new ArrayList<>();
            for(Usage us_item : nowMeaning.Usages) {
                usages.add(us_item);
            }
            ue_adapter.initItems(usages);
        }

        holder.edit_mean_kr.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String text = s.toString();
                data.get(pos).Meaning_Kor = text;
                if (!text.equals(mean_ko)){
                    setDirty(true);
                }
            }
        });
        holder.edit_mean_en.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String text = s.toString();
                data.get(pos).Meaning_Eng = text;
                if (!text.equals(mean_en)){
                    setDirty(true);
                }
            }
        });

        holder.edit_add_usage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ue_adapter.addEmptyItem();
                isDirty = true;
            }
        });


        holder.edit_remove_wordinfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                util.sysout(pos + "");
                if(data.size() - 1 >= pos) {
                    data.remove(pos);
                    notifyItemRemoved(pos);
                    notifyItemRangeChanged(pos, data.size());
                    isDirty = true;
                }
            }
        });

        holder.edit_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String type = parent.getItemAtPosition(position).toString();
                data.get(pos).Meaning_Type = util.StringToWordType(type);
                if (data_old.size() < pos + 1) {
                    return;
                }
                if (data_old.get(pos).Meaning_Type.equals(type)) {
                    isDirty = true;
                } else {
                    isDirty = false;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        LinearLayoutManager lm = new LinearLayoutManager(context);
        lm.setOrientation(RecyclerView.VERTICAL);

        holder.usage_recycler.setLayoutManager(lm);
        holder.usage_recycler.setItemAnimator(new DefaultItemAnimator());
        holder.usage_recycler.setHasFixedSize(true);
        holder.usage_recycler.setAdapter(ue_adapter);

        ue_adapter.notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return (null != data ? data.size() : 0);
    }

    public void addEmptyItem() {
        Meaning mn = new Meaning();
        mn.Word_ID = typicalData.Word_ID;
        mn.PronUK = typicalData.PronUK;
        mn.PronUS = typicalData.PronUS;
        data.add(mn);
        notifyDataSetChanged();
        isDirty = true;
    }

    public void addItems(ArrayList<Meaning> datas, boolean makeDirty) {
        data.addAll(datas);
        notifyDataSetChanged();
        if(makeDirty)
            isDirty = true;
    }

    public boolean isDirty() {
        boolean result = isDirty;
        for(UsageEditAdapter ue_adapter : usageEditAdapter)
            result = result | ue_adapter.isDirty();
        return result;
    }

    public void setDirty(boolean dirty) {
        isDirty = dirty;
    }

    public class WordEditHolder extends RecyclerView.ViewHolder {
        protected ImageButton edit_remove_wordinfo;
        protected Spinner edit_type;
        protected TextView edit_pron_us;
        protected TextView edit_pron_uk;
        protected EditText edit_mean_kr;
        protected EditText edit_mean_en;
        protected ImageButton edit_add_usage;
        protected RecyclerView usage_recycler;


        public WordEditHolder(@NonNull View view) {
            super(view);
            this.edit_remove_wordinfo = view.findViewById(R.id.edit_remove_wordinfo);
            this.edit_type = view.findViewById(R.id.edit_type);
            this.edit_pron_us = view.findViewById(R.id.edit_pron_us);
            this.edit_pron_uk = view.findViewById(R.id.edit_pron_uk);
            this.edit_mean_kr = view.findViewById(R.id.edit_mean_kr);
            this.edit_mean_en = view.findViewById(R.id.edit_mean_en);
            this.edit_add_usage = view.findViewById(R.id.edit_add_usage);
            this.usage_recycler = view.findViewById(R.id.usage_recycler);
        }
    }
}

package com.example.dictionary.View.RecycleAdapter;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dictionary.Model.RoomDB.Entity.Usage;
import com.example.dictionary.R;
import com.example.dictionary.util;

import java.util.ArrayList;

public class UsageEditAdapter extends RecyclerView.Adapter<UsageEditAdapter.UsageEditHolder> {
    private ArrayList<Usage> data;
    private ArrayList<Usage> oldData;
    private Context context;
    private boolean isDirty;

    public UsageEditAdapter() {
        this.data = new ArrayList<>();
        this.oldData = new ArrayList<>();
    }

    public void addItems(ArrayList<Usage> data) {
        this.data.addAll(data);
        this.notifyDataSetChanged();
    }

    public void initItems(ArrayList<Usage> data) {
        this.data.addAll(data);
        this.oldData.addAll(data);
        this.notifyDataSetChanged();
    }

    public void addEmptyItem() {
        Usage usage = new Usage();
        usage.Usage_String = "";
        usage.Usage_Seq = data.size();
        this.data.add(usage);
        this.notifyDataSetChanged();
    }

    public boolean isDirty() {
        return isDirty;
    }

    public void setDirty(boolean dirty) {
        isDirty = dirty;
    }

    public void commit() {
    }

    @NonNull
    @Override
    public UsageEditHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        util.sysout("UsageEditAdapter OnCreateViewHolder Start");
        this.context = parent.getContext();
        View view = LayoutInflater.from(this.context)
                .inflate(R.layout.edit_usage, parent,false);
        UsageEditHolder holder = new UsageEditHolder(view);
        util.sysout("UsageEditAdapter OnCreateViewHolder End");
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull UsageEditHolder holder, int position) {
        util.sysout("UsageEditAdapter onBindViewHolder Start");
        final int pos = position;
        final UsageEditAdapter adapter_this = this;
        holder.edit_usage.setText(data.get(pos).Usage_String);
        data.get(pos).Usage_Seq = pos;
        holder.text_usage_seq.setText("" + data.get(pos).Usage_Seq);
        holder.edit_usage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String text = s.toString();
                data.get(pos).Usage_String = text;
                if (oldData.size() >= pos + 1) {
                    isDirty = !oldData.get(pos).equals(text);
                }
            }
        });
        holder.edit_remove_usage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter_this.removeItem(pos);
                adapter_this.notifyItemRemoved(pos);
            }
        });
        util.sysout("UsageEditAdapter onBindViewHolder End");
    }

    @Override
    public int getItemCount() {
        return (null != data ? data.size() : 0);
    }

    public void removeItem(int position) {
        data.remove(position);
        this.notifyItemRemoved(position);
        this.notifyItemRangeChanged(position, data.size());
        this.isDirty = true;
    }

    public class UsageEditHolder extends RecyclerView.ViewHolder {
        protected EditText edit_usage;
        protected ImageButton edit_remove_usage;
        protected TextView text_usage_seq;
        public UsageEditHolder(@NonNull View view) {
            super(view);
            this.edit_usage = view.findViewById(R.id.edit_usage);
            this.edit_remove_usage = view.findViewById(R.id.edit_remove_usage);
            this.text_usage_seq = view.findViewById(R.id.text_usage_seq);
        }
    }

    public ArrayList<Usage> getData() {
        return data;
    }

    public void setData(ArrayList<Usage> data) {
        this.data = data;
    }
}

//package com.example.dictionary.View.RecycleAdapter;
//
//import android.content.Context;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.TextView;
//
//import androidx.annotation.NonNull;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.example.dictionary.Model.RoomDB.Entities.Usage;
//import com.example.dictionary.R;
//
//import java.util.ArrayList;
//import java.util.Collection;
//import java.util.List;
//
//public class WordUsageAdapter extends RecyclerView.Adapter<WordUsageAdapter.WordUsageHolder> {
//    private List<Usage> data;
//    private Context context;
//
//    public WordUsageAdapter() {
//        data = new ArrayList<>();
//    }
//
//    public void addItems(Collection<Usage> items) {
//        for(Usage item : items) {
//            if (null != item)
//                data.add(item);
//        }
//    }
//
//    @NonNull
//    @Override
//    public WordUsageHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        this.context = parent.getContext();
//        View view = LayoutInflater.from(this.context).inflate(R.layout.simple_layout_1, parent, false);
//        WordUsageHolder holder = new WordUsageHolder(view);
//        return holder;
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull WordUsageHolder holder, int position) {
//        final int pos = position;
//        final Usage nowUsage = data.get(pos);
//        if (nowUsage != null) {
//            holder.text1.setText(nowUsage.Usage);
//        }
//    }
//
//    @Override
//    public int getItemCount() {
//        return data.size();
//    }
//
//    public class WordUsageHolder extends RecyclerView.ViewHolder {
//        protected View view;
//        protected TextView text1;
//        public WordUsageHolder(@NonNull View itemView) {
//            super(itemView);
//            this.view = itemView;
//            this.text1 = itemView.findViewById(android.R.id.text1);
//        }
//    }
//}

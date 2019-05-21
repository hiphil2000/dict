package com.example.dictionary.View.RecycleAdapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dictionary.Model.RoomDB.Entity.Meaning;
import com.example.dictionary.Model.RoomDB.Entity.Word;
import com.example.dictionary.Presenter.MainPresenter;
import com.example.dictionary.Presenter.MainPresenterImpl;
import com.example.dictionary.R;
import com.example.dictionary.View.WordDetailActivity;
import com.example.dictionary.View.WordEditActivity;
import com.example.dictionary.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class QueryListAdapter extends RecyclerView.Adapter<QueryListAdapter.QueryListHolder>  {
    private List<Word> data;
    private MainPresenter presenter;
    private Context context;
    private int delPos;

    public QueryListAdapter() {
        data = new ArrayList<>();
        delPos = -1;
    }

    public void setPresenter(MainPresenter presenter) {
        this.presenter = presenter;
    }

    public List<Word> getData() { return data; }

    public void addItem(Word item) {
        if (item != null)
            data.add(item);
    }

    public void addItems(Collection<Word> items) {
        for (Word item : items) {
            if (item != null)
                if (item.Meanings != null)
                    if (item.Meanings.size() > 0)
                        this.data.add(item);
        }
    }

    public void clearItems() {
        this.data.clear();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public QueryListHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        this.context = parent.getContext();
        View view = LayoutInflater.from(this.context).inflate(R.layout.query_list_item, parent,false);
        QueryListHolder holder = new QueryListHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull QueryListHolder holder, int position) {
        final int pos = position;
        final Word nowWord = data.get(pos);
        if (nowWord != null) {
            holder.text_query_headword.setText(nowWord.Word_String);
            holder.text_query_from.setText(nowWord.IsLocal==true?"내 단어":"웹 단어");
            if (nowWord.Meanings != null && nowWord.Meanings.size() > 0) {
                Meaning typicalMeaning = nowWord.Meanings.get(0);
                holder.text_query_type.setText(util.wordTypeToString(typicalMeaning.Meaning_Type));
                holder.text_query_mean_kr.setText(typicalMeaning.Meaning_Kor);
                holder.text_query_mean_en.setText(typicalMeaning.Meaning_Eng);
            }

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, WordDetailActivity.class);
                    intent.putExtra("wordData", nowWord);
                    context.startActivity(intent);
                }
            });

            holder.button_query_item_menu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PopupMenu pop = new PopupMenu(((MainPresenterImpl) presenter).getActivity(), v);
                    pop.getMenuInflater().inflate(R.menu.query_list_menu, pop.getMenu());
                    Menu popMenu = pop.getMenu();
                    if (nowWord.IsLocal) {
                        popMenu.findItem(R.id.add_to_note).setVisible(false);
                    } else {
                        popMenu.findItem(R.id.delete_from_note).setVisible(false);
                        popMenu.findItem(R.id.edit_note).setVisible(false);
                    }
                    pop.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch(item.getItemId()) {
                                case R.id.add_to_note:
                                    presenter.addToNote(nowWord);
                                    break;
                                case R.id.delete_from_note:
                                    delPos = pos;
                                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                    builder.setMessage("단어 \'"+nowWord.Word_String+"\' 를 삭제하시겠습니까?")
                                            .setPositiveButton("예", query_menu_delete)
                                            .setNegativeButton("아니오", query_menu_delete)
                                            .show();
                                    break;
                                case R.id.edit_note:
                                    Intent intent = new Intent(((MainPresenterImpl) presenter).getActivity(), WordEditActivity.class);
                                    intent.putExtra("wordData", nowWord);
                                    intent.putExtra("isCreateMode", !nowWord.IsLocal);
                                    ((MainPresenterImpl) presenter).getActivity().startActivityForResult(intent, 1);
                                    break;
                            }
                            return false;
                        }
                    });
                    pop.show();
                }
            });
        }
    }

    DialogInterface.OnClickListener query_menu_delete = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            if (delPos > -1 && which == DialogInterface.BUTTON_POSITIVE) {
                presenter.deleteFromNote(data.get(delPos));
                data.remove(delPos);
                notifyDataSetChanged();
                delPos = -1;
            }
        }
    };

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class QueryListHolder extends RecyclerView.ViewHolder {
        protected View view;
        protected TextView text_query_headword;
        protected TextView text_query_type;
        protected TextView text_query_from;
        protected TextView text_query_mean_kr;
        protected TextView text_query_mean_en;
        protected ImageButton button_query_item_menu;

        public QueryListHolder(@NonNull View itemView) {
            super(itemView);
            this.view = itemView;
            this.text_query_headword = itemView.findViewById(R.id.text_query_headword);
            this.text_query_type = itemView.findViewById(R.id.text_query_type);
            this.text_query_from = itemView.findViewById(R.id.text_query_from);
            this.text_query_mean_kr = itemView.findViewById(R.id.text_query_mean_kr);
            this.text_query_mean_en = itemView.findViewById(R.id.text_query_mean_en);
            this.button_query_item_menu = itemView.findViewById(R.id.button_query_item_menu);
        }
    }
}

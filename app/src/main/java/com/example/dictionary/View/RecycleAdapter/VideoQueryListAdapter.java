package com.example.dictionary.View.RecycleAdapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dictionary.Model.RoomDB.Entity.Video;
import com.example.dictionary.Presenter.MainPresenter;
import com.example.dictionary.Presenter.MainPresenterImpl;
import com.example.dictionary.R;
import com.example.dictionary.View.WordEditActivity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class VideoQueryListAdapter extends RecyclerView.Adapter<VideoQueryListAdapter.VideoQueryListHolder>  {
    private List<Video> data;
    private MainPresenter presenter;
    private Context context;
    private int delPos;

    public VideoQueryListAdapter() {
        data = new ArrayList<>();
        delPos = -1;
    }

    public void setPresenter(MainPresenter presenter) {this.presenter = presenter;}

    public List<Video> getData() {return data;}

    public void addItems(Collection<Video> videos) {
        for (Video video : videos) {
            if (video != null)
                if (video.Subtitles != null)
                    if (video.Subtitles.size() > 0)
                        this.data.add(video);
        }
    }

    public void clearItems() {
        this.data.clear();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VideoQueryListHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        this.context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.video_list_item, parent, false);
        VideoQueryListHolder holder = new VideoQueryListHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull VideoQueryListHolder holder, int position) {
        final int pos = position;
        final Video nowVideo = data.get(pos);
        if (nowVideo == null)
            return;

        holder.video_thumbnail.setImageURI(Uri.parse(nowVideo.Video_Thumbnail_Data));
        holder.video_title.setText(nowVideo.Video_Name);
        holder.video_channel_title.setText(nowVideo.Video_Channel_Name);
        holder.video_publish_date.setText(nowVideo.Video_Published_Date.toString());
        holder.video_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu pop = new PopupMenu(((MainPresenterImpl) presenter).getActivity(), v);
                pop.getMenuInflater().inflate(R.menu.query_list_menu, pop.getMenu());
                Menu popMenu = pop.getMenu();
                if (nowVideo.IsLocal) {
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
                                presenter.addVideo(nowVideo);
                                break;
                            case R.id.delete_from_note:
                                delPos = pos;
                                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                builder.setMessage("단어 \'"+nowVideo.Video_Name+"\' 를 삭제하시겠습니까?")
                                        .setPositiveButton("예", video_menu_delete)
                                        .setNegativeButton("아니오", video_menu_delete)
                                        .show();
                                break;
                            case R.id.edit_note:
                                Intent intent = new Intent(((MainPresenterImpl) presenter).getActivity(), WordEditActivity.class);
                                intent.putExtra("video", nowVideo);
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

    DialogInterface.OnClickListener video_menu_delete = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            if (delPos > -1 && which == DialogInterface.BUTTON_POSITIVE) {
                presenter.deleteVideo(data.get(delPos));
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

    public class VideoQueryListHolder extends RecyclerView.ViewHolder {
        View itemView;
        ImageView video_thumbnail;
        TextView video_title;
        TextView video_channel_title;
        TextView video_publish_date;
        TextView video_description;
        ImageButton video_menu;

        public VideoQueryListHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;
            this.video_thumbnail = this.itemView.findViewById(R.id.video_thumbnail);
            this.video_title = this.itemView.findViewById(R.id.video_title);
            this.video_channel_title = this.itemView.findViewById(R.id.video_channel_title);
            this.video_publish_date = this.itemView.findViewById(R.id.video_publish_date);
            this.video_description = this.itemView.findViewById(R.id.video_description);
            this.video_menu = this.itemView.findViewById(R.id.video_menu);
        }
    }
}

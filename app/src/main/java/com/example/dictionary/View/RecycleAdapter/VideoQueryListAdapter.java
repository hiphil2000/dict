package com.example.dictionary.View.RecycleAdapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
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
import com.example.dictionary.Presenter.MainPresenterImpl;
import com.example.dictionary.Presenter.YoutubePresenter;
import com.example.dictionary.R;
import com.example.dictionary.View.WordEditActivity;
import com.example.dictionary.View.YoutubeActivity;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class VideoQueryListAdapter extends RecyclerView.Adapter<VideoQueryListAdapter.VideoQueryListHolder>  {
    private List<Video> data;
    private YoutubePresenter presenter;
    private Context context;
    private int delPos;

    public VideoQueryListAdapter() {
        data = new ArrayList<>();
        delPos = -1;
    }

    public void setPresenter(YoutubePresenter presenter) {this.presenter = presenter;}

    public List<Video> getData() {return data;}

    public void addItems(Collection<Video> videos) {
        for (Video video : videos) {
            if (video != null) {
                if (video.Video_Data != null)
                    this.data.add(video);
            }
        }
    }
    public void addItem(Video video) {
        if (video != null)
            this.data.add(video);
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

        new DownloadImageTask(holder.video_thumbnail).execute(nowVideo.Video_Thumbnail_Data);
        holder.video_title.setText(nowVideo.Video_Name);
        holder.video_publish_date.setText(nowVideo.Video_Published_Date.toString());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, YoutubeActivity.class);
                intent.putExtra("video", nowVideo);
                context.startActivity(intent);
            }
        });
        //endregion
    }

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

        public VideoQueryListHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;
            this.video_thumbnail = this.itemView.findViewById(R.id.video_thumbnail);
            this.video_title = this.itemView.findViewById(R.id.video_title);
            this.video_channel_title = this.itemView.findViewById(R.id.video_channel_title);
            this.video_publish_date = this.itemView.findViewById(R.id.video_publish_date);
        }
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }
}

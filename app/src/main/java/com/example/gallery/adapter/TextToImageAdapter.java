package com.example.gallery.adapter;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.gallery.R;
import com.example.gallery.helper.DownLoadImage;
import com.example.gallery.object.AI_Image;

import java.util.List;

public class TextToImageAdapter extends RecyclerView.Adapter<TextToImageAdapter.ViewHolder>{
    List<AI_Image> imageList;
    Context context;

    public TextToImageAdapter(List<AI_Image> imageList, Context context) {
        this.imageList = imageList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ai_image_item,null);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Glide.with(context).load(imageList.get(position).getImageUrl())
                .transition(withCrossFade())
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, @Nullable Object model, @NonNull Target<Drawable> target, boolean isFirstResource) {
                        holder.progressBar.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(@NonNull Drawable resource, @NonNull Object model, Target<Drawable> target, @NonNull DataSource dataSource, boolean isFirstResource) {
                        holder.progressBar.setVisibility(View.GONE);
                        return false;
                    }
                })
                .into(holder.image_item);

        holder.image_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.download.setVisibility(View.VISIBLE);
                holder.download.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(context,"Link "+imageList.get(holder.getAdapterPosition()).getImageUrl(),Toast.LENGTH_SHORT).show();

                        startDownloadFile(imageList.get(holder.getAdapterPosition()).getImageUrl());
                    }
                });

            }
        });
    }
    void startDownloadFile(String ImageUrl) {
        //Asynctask to create a thread to downlaod image in the background
        Toast.makeText(context, R.string.downloading, Toast.LENGTH_SHORT).show();
        try{
            new DownLoadImage(context).execute(ImageUrl);
        }catch (Exception e){
        }

    }

    @Override
    public int getItemCount() {
        return imageList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image_item;
        ProgressBar progressBar;
        ImageButton download;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image_item = itemView.findViewById(R.id.image_item);

            progressBar = itemView.findViewById(R.id.progress);
            download = itemView.findViewById(R.id.download);

        }
    }
}

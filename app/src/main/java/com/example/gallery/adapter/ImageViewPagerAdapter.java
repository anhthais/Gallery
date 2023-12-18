package com.example.gallery.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.example.gallery.R;
import com.example.gallery.ToolbarCallbacks;
import com.example.gallery.object.Image;
import com.ortiz.touchview.TouchImageView;

import java.util.ArrayList;

public class ImageViewPagerAdapter extends RecyclerView.Adapter<ImageViewPagerAdapter.ViewHolder> {
    private ArrayList<Image> images;
    private Context context;
    private boolean isSystemUiVisible = true;
    private ToolbarCallbacks toolbarCallbacks;

    public ImageViewPagerAdapter(Context context, ArrayList<Image> images){
        this.context = context;
        this.images = images;
    }

    public void setToolbarCallbacks(ToolbarCallbacks toolbarCallbacks){
        this.toolbarCallbacks = toolbarCallbacks;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TouchImageView imageView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = (TouchImageView)itemView.findViewById(R.id.full_image_view);
        }
        public View getView(){
            return this.imageView;
        }

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.full_picture, parent, false);
        return new ViewHolder(view);
    }


    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Image image = images.get(position);
        if(image == null) return;
        Glide.with(context).load(image.getPath())
                .apply(new RequestOptions()
                        .fitCenter()
                        .format(DecodeFormat.PREFER_ARGB_8888)
                        .override(Target.SIZE_ORIGINAL))
                .into(holder.imageView);
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isSystemUiVisible = !isSystemUiVisible;
                toolbarCallbacks.showOrHideToolbars(isSystemUiVisible);
            }
        });
       holder.imageView.setOnTouchListener(new View.OnTouchListener() {
           @Override
           public boolean onTouch(View v, MotionEvent event) {
               holder.imageView.getParent().requestDisallowInterceptTouchEvent(holder.imageView.isZoomed());
               return false;
           }
       });
    }

    @Override
    public int getItemCount() {
        return images.size();
    }

}
package com.example.gallery.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.example.gallery.R;
import com.example.gallery.ToolbarCallbacks;
import com.example.gallery.object.Image;

import java.io.File;
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
        private ImageView imageView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = (ImageView)itemView.findViewById(R.id.full_image_view);
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

        // TODO: set margin while sliding
    }

    @Override
    public int getItemCount() {
        return images.size();
    }

}
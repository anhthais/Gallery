package com.example.gallery.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.gallery.R;
import com.example.gallery.object.Image;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.ArrayList;


public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {
    private ArrayList<Image> listImages;
    private Context context;

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    public static class ImageViewHolder extends RecyclerView.ViewHolder {
        private ImageView image;
        public ImageViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View
            image = (ImageView)view.findViewById(R.id.picture_item);
        }
        public ImageView getImageView(){
            return image;
        }
    }

    /**
     * Initialize the dataset of the Adapter.
     */
    public ImageAdapter(Context context, ArrayList<Image> listImages) {
        this.context = context;
        this.listImages = listImages;
    }

    // setters


    // Create new views (invoked by the layout manager)
    public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_picture, parent, false);
        return new ImageViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ImageViewHolder holder, final int position) {
        Image image = listImages.get(position);
        if (image == null) {
            return;
        }
        //set image
        File file = new File(image.getPath());
        Glide.with(context).load(file).into(holder.image);

        //TODO: set onClickListener for holder

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return listImages.size();
    }
}

package com.example.gallery.adapter;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.gallery.ImageActivity;
import com.example.gallery.R;
import com.example.gallery.object.Image;

import com.bumptech.glide.Glide;
import com.example.gallery.object.ImageGroup;

import java.io.File;
import java.util.ArrayList;


public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {
    private ArrayList<Image> listImages;
    private Context context;

    // TODO: adjust later, start ImageActivity with extended list of images
    private ArrayList<ImageGroup> listGroups = null; // contains listImages
    private int groupPos = 0; // position of listImages in listGroups

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
    public void setListGroups(ArrayList<ImageGroup> listGroups){
        this.listGroups = listGroups;
    }
    public void setGroupPos(int groupPos){
        this.groupPos = groupPos;
    }

    // Create new views (invoked by the layout manager)
    public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_picture, parent, false);
        return new ImageViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ImageViewHolder holder, int position) {
        Image image = listImages.get(position);
        if (image == null) {
            return;
        }
        //set image
        File file = new File(image.getPath());
        Glide.with(context).load(file).into(holder.image);

        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(listGroups != null){
                    ArrayList<Image> images = new ArrayList<>();
                    int newPosition = 0;
                    for(int i = 0; i < listGroups.size(); ++i){
                        images.addAll(listGroups.get(i).getList());
                        if(i < groupPos){
                            newPosition += listGroups.get(i).getList().size();
                        }
                    }
                    newPosition += position;

                    Intent intent = new Intent(context, ImageActivity.class);
                    intent.putParcelableArrayListExtra("images", images);
                    intent.putExtra("curPos", newPosition);
                    context.startActivity(intent);
                }
                else{
                    Intent intent = new Intent(context, ImageActivity.class);
                    intent.putParcelableArrayListExtra("images", listImages);
                    intent.putExtra("curPos", position);
                    context.startActivity(intent);
                }
            }
        });
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return listImages.size();
    }
}

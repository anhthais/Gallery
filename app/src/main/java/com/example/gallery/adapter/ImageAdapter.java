package com.example.gallery.adapter;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.Context;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.gallery.ImageActivity;
import com.example.gallery.MultiSelectCallbacks;
import com.example.gallery.MainActivity;
import com.example.gallery.R;
import com.example.gallery.object.Album;
import com.example.gallery.object.Image;

import com.bumptech.glide.Glide;
import com.example.gallery.object.ImageGroup;
import com.google.gson.Gson;

import java.io.File;
import java.util.ArrayList;


public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {
    private ArrayList<Image> listImages;
    private Context context;
    private SparseBooleanArray selectedItemsIds;
    private MultiSelectCallbacks multiSelectCallbacks;
    private boolean checkBoxEnable = false;
    private ArrayList<ImageGroup> listGroups = null; // contains listImages
    private int groupPos = 0; // position of listImages in listGroups

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    public class ImageViewHolder extends RecyclerView.ViewHolder {
        private ImageView image;
        private CheckBox checkBox;

        public ImageViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View
            image = (ImageView)view.findViewById(R.id.picture_item);
            checkBox=itemView.findViewById(R.id.checkBoxImageItem);
            image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(checkBoxEnable==true){
                        checkBox.setChecked(!checkBox.isChecked());
                        int position=getAdapterPosition();
                        if (checkBox.isChecked()) {
                            selectedItemsIds.put(position,true);
                        }
                        else{
                            if(selectedItemsIds.get(position)==true) {
                                selectedItemsIds.delete(position);
                            }
                        }
                    }
                    else{
                        //show all picture in album
                        //((MainActivity)context).onMsgFromFragToMain("ALBUM",listImages.get(getAdapterPosition()));
                    }
                }
            });

            image.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {

                    if(!checkBoxEnable) {
                        /*
                        AppCompatActivity ma=(AppCompatActivity) context;
                        ActionMode mode=ma.startSupportActionMode(new ActionMode.Callback() {
                            @Override
                            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                                //update navìation/action bar here
                                return true;
                            }

                            @Override
                            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                                return true;
                            }

                            @Override
                            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                                return true;
                            }

                            @Override
                            public void onDestroyActionMode(ActionMode mode) {
                                selectedItemsIds.clear();
                                notifyDataSetChanged();
                                checkBoxEnable=false;
                                notifyDataSetChanged();

                            }
                        });*/
                        checkBoxEnable = true;
                        notifyDataSetChanged();
                    }
                    return true;
                }
            });
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
        selectedItemsIds=new SparseBooleanArray();
    }

    // setters
    public void setListGroups(ArrayList<ImageGroup> listGroups){
        this.listGroups = listGroups;
    }
    public void setGroupPos(int groupPos){
        this.groupPos = groupPos;
    }

    public void setMultiSelectCallbacks(MultiSelectCallbacks multiSelectCallbacks){
        this.multiSelectCallbacks = multiSelectCallbacks;
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
        if(checkBoxEnable==true){
            holder.checkBox.setVisibility(View.VISIBLE);
        }else{
            holder.checkBox.setVisibility(View.INVISIBLE);
            holder.checkBox.setChecked(false);
        }
        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!checkBoxEnable) {
                    if (listGroups != null) {
                        ArrayList<Image> images = new ArrayList<>();
                        int newPosition = 0;
                        for (int i = 0; i < listGroups.size(); ++i) {
                            images.addAll(listGroups.get(i).getList());
                            if (i < groupPos) {
                                newPosition += listGroups.get(i).getList().size();
                            }
                        }
                        newPosition += position;
                        Intent intent = new Intent(context, ImageActivity.class);
                        intent.putParcelableArrayListExtra("images", images);
                        intent.putExtra("curPos", newPosition);
                        //đưa danh sách tên các album qua imageactivity
                        ArrayList<String> album_name = new ArrayList<String>();
                        ArrayList<String> fav_img_name=new ArrayList<String>();
                        ArrayList<Album> albums=((MainActivity)context).getAlbum_list();
                        ArrayList<Image> favImg = ((MainActivity)context).getFavourite_img_list();
                        for(int i = 0; i < albums.size(); i++){
                            album_name.add(albums.get(i).getName());
                        }
                        Gson gson = new Gson();
                        String albu_arr = gson.toJson(album_name);
                        if (favImg != null)
                        {
                            for (int i=0 ; i < favImg.size();i++)
                            {
                                fav_img_name.add(favImg.get(i).getPath());
                            }
                        }
                        String fav = gson.toJson(fav_img_name);
                        Log.d("CheckImgAl",fav);
                        intent.putExtra("ALBUM-LIST",albu_arr);
                        intent.putExtra("FAV-IMG-LIST",fav);
                        ((MainActivity) context).startActivityForResult(intent,1122);
                    } else {
                        Intent intent = new Intent(context, ImageActivity.class);
                        intent.putParcelableArrayListExtra("images", listImages);
                        intent.putExtra("curPos", position);
                        ((MainActivity) context).startActivityForResult(intent,1122);
                    }
                }
                else {
                    // nested adapter --> use callbacks implemented in parent adapter
                    if(multiSelectCallbacks != null){
                        holder.checkBox.setChecked(!holder.checkBox.isChecked());
                        multiSelectCallbacks.onItemClick(position, groupPos);
                    }
                    // no nested adapter
                    else{
                        holder.checkBox.setChecked(!holder.checkBox.isChecked());
                        if(selectedItemsIds.get(position)){
                            selectedItemsIds.put(position, true);
                        }else{
                            selectedItemsIds.delete(position);
                        }
                    }
                }
            }
        });

        holder.image.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if(multiSelectCallbacks != null){
                    multiSelectCallbacks.setMultiSelect(holder.getAdapterPosition(), groupPos);
                }else{
                    // TODO: call actionmode, like ImageGroupAdapter

                }
                holder.checkBox.setChecked(true);
                return true;
            }
        });

        holder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // nested adapter --> use callbacks implemented in parent adapter
                if(multiSelectCallbacks != null){
                    multiSelectCallbacks.onItemClick(holder.getAdapterPosition(), groupPos);
                }
                // no nested adapter
                else{
                    if(selectedItemsIds.get(position)){
                        selectedItemsIds.put(position, true);
                    }else{
                        selectedItemsIds.delete(position);
                    }
                }

            }
        });
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        if(listImages==null){
            return 0;
        }
        return listImages.size();
    }

    public boolean changeMultiMode(boolean isMultichoose)
    {
        if (true == isMultichoose)
        {
            checkBoxEnable = true;
            notifyDataSetChanged();
        }
        else {
            checkBoxEnable = false;
            notifyDataSetChanged();
        }
        return true;
    }
    public void removeFavImage()
    {
        notifyDataSetChanged();
    }
}

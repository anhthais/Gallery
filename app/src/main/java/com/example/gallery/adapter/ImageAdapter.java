package com.example.gallery.adapter;

import android.content.Context;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
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
    private SparseBooleanArray selectedItemsIds;

    private boolean checkBoxEnable=false;

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
        if(checkBoxEnable==true){
            holder.checkBox.setVisibility(View.VISIBLE);
        }else{
            holder.checkBox.setVisibility(View.INVISIBLE);
            holder.checkBox.setChecked(false);
        }
        holder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position=holder.getAdapterPosition();
                if (holder.checkBox.isChecked()) {
                    selectedItemsIds.put(position,true);
                }
                else{
                    if(selectedItemsIds.get(position)==true) {
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
}

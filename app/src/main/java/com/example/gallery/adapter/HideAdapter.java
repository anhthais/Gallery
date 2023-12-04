package com.example.gallery.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.gallery.ImageActivity;
import com.example.gallery.MainActivity;
import com.example.gallery.MultiSelectCallbacks;
import com.example.gallery.R;
import com.example.gallery.object.Image;

import java.io.File;
import java.util.ArrayList;


public class HideAdapter extends RecyclerView.Adapter<HideAdapter.HideImageViewHolder> {
    private ArrayList<Image> listImages;
    private Context context;
    private SparseBooleanArray selectedItemsIds;
    private MultiSelectCallbacks multiSelectCallbacks;
    private boolean checkBoxEnable = false;
    private int groupPos = 0; // position of listImages in listGroups

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */

    public class HideImageViewHolder extends RecyclerView.ViewHolder {
        private ImageView image;
        private CheckBox checkBox;

        public HideImageViewHolder(View view) {
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
    public HideAdapter(Context context, ArrayList<Image> listImages) {
        this.context = context;
        this.listImages = listImages;
        selectedItemsIds=new SparseBooleanArray();
    }

    // setters

    public void setMultiSelectCallbacks(MultiSelectCallbacks multiSelectCallbacks){
        this.multiSelectCallbacks = multiSelectCallbacks;
    }

    // Create new views (invoked by the layout manager)
    public HideImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_picture, parent, false);
        return new HideImageViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(HideImageViewHolder holder, int position) {
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
                Intent intent = new Intent(context, ImageActivity.class);
                intent.putParcelableArrayListExtra("images", listImages);
                intent.putExtra("curPos", position);
                intent.putExtra("TYPE","hide");
                ((MainActivity) context).startActivityForResult(intent,1123);
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
    public void removeImage(ArrayList<String> path)
    {
        for(int i=0;i<path.size();i++){
            for(int j=0;j<listImages.size();j++){
                if(listImages.get(j).getPath().equals(path.get(i))){
                    listImages.remove(j);
                    this.notifyItemRemoved(j);
                    break;
                }
            }
        }
    }
}

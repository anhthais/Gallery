package com.example.gallery.adapter;
import android.content.Context;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.gallery.MainActivity;
import com.example.gallery.R;
import com.example.gallery.object.TrashItem;


import java.io.File;
import java.util.ArrayList;

public class TrashAdapter extends RecyclerView.Adapter<TrashAdapter.ViewHolder> {
    private Context context;
    private ArrayList<TrashItem> trash_item_list;
    private SparseBooleanArray selectedItemsIds;

    private boolean checkBoxEnable=false;

    public TrashAdapter(Context context, int layoutTobeInflated, ArrayList<TrashItem> trash_item_list){
        this.trash_item_list=trash_item_list;
        this.context=context;
        selectedItemsIds=new SparseBooleanArray();
    }
    @Override
    public void onViewRecycled(@NonNull ViewHolder holder) {
        super.onViewRecycled(holder);
        int position = holder.getAdapterPosition();
        holder.checkBox.setChecked(selectedItemsIds.get(position));
    }


    @Override
    public void onViewAttachedToWindow(@NonNull ViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        int position = holder.getAdapterPosition();
        holder.checkBox.setChecked(selectedItemsIds.get(position));
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private ImageView itemImageView;
        private TextView trashNameTxtView;
        private CheckBox checkBox;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemImageView=itemView.findViewById(R.id.imageViewTrashItem);
            trashNameTxtView=itemView.findViewById(R.id.textViewTrashItem);
            checkBox=itemView.findViewById(R.id.checkBoxTrashItem);
            itemView.setOnClickListener(new View.OnClickListener() {
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
                        //((MainActivity)context).onMsgFromFragToMain("ALBUM",albums.get(getAdapterPosition()).getName());
                    }
                }
            });
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if(!checkBoxEnable) {
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
                        });
                        checkBoxEnable = true;
                        notifyDataSetChanged();

                    }
                    return true;
                }
            });
        }
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater=LayoutInflater.from(parent.getContext());
        View trashView=inflater.inflate(R.layout.trash_item,parent,false);
        return new ViewHolder(trashView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TrashItem item=trash_item_list.get(position);
        File file = new File(item.getPath());
        Glide.with(context).load(file).into(holder.itemImageView);
        holder.trashNameTxtView.setText(item.getTimeRemaining());
        if(checkBoxEnable==true){
            //holder.setIsRecyclable(false);
            holder.checkBox.setVisibility(View.VISIBLE);
        }else{
            holder.checkBox.setVisibility(View.INVISIBLE);
            holder.checkBox.setChecked(false);
        }
        //if(selectedItemsIds.get(position)){
        //    holder.checkBox.setChecked(true);
        //}
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
    public SparseBooleanArray getSelectedItemsIds(){
        return this.selectedItemsIds;
    }
    @Override
    public int getItemCount() {
        if(trash_item_list==null)
            return 0;
        return trash_item_list.size();
    }
}
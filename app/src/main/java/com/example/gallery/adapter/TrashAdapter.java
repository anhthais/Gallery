package com.example.gallery.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.gallery.MainActivity;
import com.example.gallery.R;
import com.example.gallery.TrashActivity;
import com.example.gallery.asynctask.ForceDeleteTask;
import com.example.gallery.asynctask.RestoreTask;
import com.example.gallery.helper.DateConverter;
import com.example.gallery.object.Image;
import com.example.gallery.object.TrashItem;
import com.google.android.material.bottomnavigation.BottomNavigationView;


import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class TrashAdapter extends ListAdapter<TrashItem, TrashAdapter.ViewHolder> {
    private Context context;
    private ArrayList<TrashItem> trashItems;
    private SparseBooleanArray selectedItemsIds;

    private boolean checkBoxEnable = false;
    private ActionMode multiMode = null;
    BottomNavigationView btnv;

    public TrashAdapter(Context context, ArrayList<TrashItem> trashItems) {
        super(new DiffUtil.ItemCallback<TrashItem>() {
            @Override
            public boolean areItemsTheSame(@NonNull TrashItem oldItem, @NonNull TrashItem newItem) {
                return oldItem.getDateExpires() == newItem.getDateExpires();
            }

            @Override
            public boolean areContentsTheSame(@NonNull TrashItem oldItem, @NonNull TrashItem newItem) {
                return oldItem.getDateExpires() == newItem.getDateExpires();
            }
        });
        this.context = context;
        this.trashItems = trashItems;
        selectedItemsIds = new SparseBooleanArray();
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

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView image;
        private CheckBox checkBox;
        private TextView text;

        public ViewHolder(View view) {
            super(view);
            image = (ImageView) view.findViewById(R.id.trash_item);
            checkBox = (CheckBox) view.findViewById(R.id.checkBoxTrashItem);
            text = (TextView) view.findViewById(R.id.remainingTime);
        }
    }

    @Override
    public void submitList(@Nullable List<TrashItem> list) {
        super.submitList(list);
        this.trashItems = (ArrayList<TrashItem>) list;
        this.selectedItemsIds = new SparseBooleanArray();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.trash_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TrashItem item = trashItems.get(holder.getAdapterPosition());
        File file = new File(item.getPath());
        if(!file.exists()) {
            return;
        }
        Glide.with(context).load(file).into(holder.image);
        if (checkBoxEnable) {
            holder.checkBox.setVisibility(View.VISIBLE);
        } else {
            holder.checkBox.setVisibility(View.INVISIBLE);
            holder.checkBox.setChecked(false);
        }
        holder.checkBox.setChecked(selectedItemsIds.get(holder.getAdapterPosition()));

        holder.text.setText(getRemainingTime(item));
        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!checkBoxEnable) {
                    Intent intent = new Intent(context, TrashActivity.class);
                    intent.putParcelableArrayListExtra("trashItems", trashItems);
                    intent.putExtra("curPos", holder.getAdapterPosition());
                    ((MainActivity) context).startActivityForResult(intent,2233);
                }
                else {
                    holder.checkBox.setChecked(!holder.checkBox.isChecked());
                    if(!selectedItemsIds.get(holder.getAdapterPosition())){
                        selectedItemsIds.put(holder.getAdapterPosition(), true);
                    }else{
                        selectedItemsIds.delete(holder.getAdapterPosition());
                    }
                    multiMode.setTitle(context.getString(R.string.selected) + " " + selectedItemsIds.size());
                    notifyItemChanged(holder.getAdapterPosition());
                }
            }
        });

        holder.image.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                holder.checkBox.setChecked(true);
                if(!selectedItemsIds.get(holder.getAdapterPosition())){
                    selectedItemsIds.put(holder.getAdapterPosition(), true);
                }else{
                    selectedItemsIds.delete(holder.getAdapterPosition());
                }
                changeOnMultiChooseMode();
                return true;
            }
        });

        holder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!selectedItemsIds.get(holder.getAdapterPosition())){
                    selectedItemsIds.put(holder.getAdapterPosition(), true);
                }else{
                    selectedItemsIds.delete(holder.getAdapterPosition());
                }
                multiMode.setTitle(context.getString(R.string.selected) + " " + selectedItemsIds.size());
                notifyItemChanged(holder.getAdapterPosition());
            }
        });

    }

    public void changeOnMultiChooseMode() {
        //start action mode
        AppCompatActivity ma = (AppCompatActivity) context;
        multiMode = ma.startSupportActionMode(new ActionMode.Callback() {
            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                checkBoxEnable = true;
                mode.getMenuInflater().inflate(R.menu.multi_select_menu_trash, menu);
                btnv = ((AppCompatActivity) context).findViewById(R.id.navigationBar);
                btnv.setVisibility(View.GONE);
                notifyDataSetChanged();
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return true;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                //TODO: handle action of menu
                int id = item.getItemId();
                if(id == R.id.btnSelectAllInTrash){
                    for(int i = 0; i < trashItems.size(); ++i){
                        if(!selectedItemsIds.get(i)){
                            selectedItemsIds.put(i, true);
                        }
                    }
                }
                else if(id == R.id.btnDeselectAllInTrash){
                    for(int i = 0; i < trashItems.size(); ++i){
                        if(selectedItemsIds.get(i)){
                            selectedItemsIds.delete(i);
                        }
                    }
                }
                else if(id == R.id.btnRemoveMultiFromTrash){
                    new ForceDeleteTask(context).execute(getSelectedItems());
                    mode.finish();
                }
                else if(id == R.id.btnRestoreMultiFromTrash){
                    new RestoreTask(context).execute(getSelectedItems());
                    mode.finish();
                }

                mode.setTitle(context.getString(R.string.selected) + " " + selectedItemsIds.size());
                notifyDataSetChanged();
                return true;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                checkBoxEnable = false;
                multiMode = null;
                Log.d("multiSelect", selectedItemsIds.toString());
                selectedItemsIds.clear();
                btnv.setVisibility(View.VISIBLE);
                notifyDataSetChanged();

            }
        });
    }

    @Override
    public int getItemCount() {
        if (trashItems == null)
            return 0;
        return trashItems.size();
    }

    public ArrayList<TrashItem> getSelectedItems(){
        ArrayList<TrashItem> selected = new ArrayList<>();
        for(int i = 0; i < selectedItemsIds.size(); ++i){
            selected.add(trashItems.get(selectedItemsIds.keyAt(i)));
        }
        Log.d("selected", selected.toString());
        return selected;
    }

    public String getRemainingTime(TrashItem item){
        Date curDate = Calendar.getInstance().getTime();
        Date expiredDate = new Date(item.getDateExpires());
        return context.getString(R.string.remaining) + " " + DateConverter.getDatesFromComputeDiff(curDate, expiredDate) + " " + context.getString(R.string.days);
    }
}
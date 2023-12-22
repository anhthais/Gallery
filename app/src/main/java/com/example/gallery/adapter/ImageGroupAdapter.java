package com.example.gallery.adapter;

import android.content.Context;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gallery.MainActivity;
import com.example.gallery.MultiSelectCallbacks;
import com.example.gallery.R;
import com.example.gallery.asynctask.DeleteTask;
import com.example.gallery.helper.DateConverter;
import com.example.gallery.object.Image;
import com.example.gallery.object.ImageGroup;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ImageGroupAdapter extends ListAdapter<ImageGroup, ImageGroupAdapter.ImageGroupViewHolder> implements MultiSelectCallbacks {
    private final int COL_SPAN_VIEW = 3;
    private Context context;
    private ArrayList<ImageGroup> listGroups;
    private boolean onChooseMulti = false;
    private ArrayList<SparseBooleanArray> selectedItemsIds;
    private ArrayList<ImageAdapter> imageAdapters;
    private ActionMode multiMode;
    private int selectedCount = 0;
    BottomNavigationView btnv;

    public class ImageGroupViewHolder extends RecyclerView.ViewHolder {
        private TextView txtId;
        private CheckBox checkBox;
        private RecyclerView imgList;

        public ImageGroupViewHolder(View view) {
            super(view);
            txtId = view.findViewById(R.id.gallery_fragment_item_textView);
            imgList = view.findViewById(R.id.gallery_fragment_item_image_groups);
            checkBox = view.findViewById(R.id.checkBoxGroupItem);
            imgList.setItemAnimator(new DefaultItemAnimator());
        }
    }

    public ImageGroupAdapter(Context context, ArrayList<ImageGroup> groupList) {
        super(new DiffUtil.ItemCallback<ImageGroup>() {
            @Override
            public boolean areItemsTheSame(@NonNull ImageGroup oldItem, @NonNull ImageGroup newItem) {
                return oldItem.getId().equals(newItem.getId());
            }

            @Override
            public boolean areContentsTheSame(@NonNull ImageGroup oldItem, @NonNull ImageGroup newItem) {
                for(int i = 0; i < oldItem.getList().size() && i < newItem.getList().size(); ++i){
                    if(oldItem.getList().get(i).getIdInMediaStore() != newItem.getList().get(i).getIdInMediaStore()){
                        return false;
                    }
                }

                return true;
            }
        });
        this.context = context;
        this.listGroups = groupList;
        this.imageAdapters = new ArrayList<>(listGroups.size());
        this.selectedItemsIds = new ArrayList<>();
        for(int i = 0; i < groupList.size(); ++i){
            SparseBooleanArray idsInGroup = new SparseBooleanArray();;
            selectedItemsIds.add(idsInGroup);
        }
    }

    @Override
    public void submitList(@Nullable List<ImageGroup> list) {
        super.submitList(list);
        this.listGroups = (ArrayList<ImageGroup>) list;
        this.selectedItemsIds = new ArrayList<>();
        for(int i = 0; i < listGroups.size(); ++i){
            SparseBooleanArray idsInGroup = new SparseBooleanArray();
            selectedItemsIds.add(idsInGroup);
        }
    }

    @Override
    public ImageGroupViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.gallery_fragment_item, parent, false);
        return new ImageGroupViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ImageGroupViewHolder holder, int position) {
        ImageGroup group = listGroups.get(holder.getAdapterPosition());
        if (group == null)
            return;

        holder.txtId.setText(group.getId());
        holder.imgList.setLayoutManager(new GridLayoutManager(context, COL_SPAN_VIEW));

        ImageAdapter imgList = new ImageAdapter(context, group.getList());
        imgList.setListGroups(listGroups);
        imgList.setGroupPos(holder.getAdapterPosition());
        imgList.setMultiSelectCallbacks(this);

        imgList.changeMultiMode(onChooseMulti);
        holder.imgList.setAdapter(imgList);

        if(onChooseMulti){
            holder.checkBox.setVisibility(View.VISIBLE);
            holder.checkBox.setChecked(selectedItemsIds.get(holder.getAdapterPosition()).size() == listGroups.get(holder.getAdapterPosition()).getList().size());
        }else{
            holder.checkBox.setVisibility(View.INVISIBLE);
            holder.checkBox.setChecked(false);
        }

        holder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.checkBox.isChecked()){
                    for(int i = 0; i < listGroups.get(holder.getAdapterPosition()).getList().size(); ++i){
                        if(!selectedItemsIds.get(holder.getAdapterPosition()).get(i)){
                            selectedItemsIds.get(holder.getAdapterPosition()).put(i, true);
                            selectedCount++;
                        }
                    }
                }
                else{
                    for(int i = 0; i < listGroups.get(holder.getAdapterPosition()).getList().size(); ++i){
                        if(selectedItemsIds.get(holder.getAdapterPosition()).get(i)){
                            selectedItemsIds.get(holder.getAdapterPosition()).delete(i);
                            selectedCount--;
                        }
                    }
                }
                multiMode.setTitle(context.getString(R.string.selected) + " " + selectedCount);
                notifyItemChanged(holder.getAdapterPosition());
            }
        });
      
        if(holder.imgList.getAdapter().getItemCount()==0){
            holder.txtId.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        if (listGroups != null) {
            return listGroups.size();
        }
        return 0;
    }

    public void changeOnMultiChooseMode() {
        //start action mode
        AppCompatActivity ma = (AppCompatActivity) context;
        multiMode = ma.startSupportActionMode(new ActionMode.Callback() {
            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                selectedCount = 0;
                onChooseMulti = true;
                mode.getMenuInflater().inflate(R.menu.multi_select_menu_gallery, menu);
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
                if(id == R.id.btnSelectAll){
                    for(int i = 0; i < listGroups.size(); ++i){
                        for(int j = 0; j < listGroups.get(i).getList().size(); ++j){
                            if(!selectedItemsIds.get(i).get(j)){
                                selectedItemsIds.get(i).put(j, true);
                                selectedCount++;
                            }
                        }
                    }
                }
                else if(id == R.id.btnDeselectAll){
                    for(int i = 0; i < listGroups.size(); ++i){
                        for(int j = 0; j < listGroups.get(i).getList().size(); ++j){
                            if(selectedItemsIds.get(i).get(j)){
                                selectedItemsIds.get(i).delete(j);
                                selectedCount--;
                            }
                        }
                    }
                }
                else if(id == R.id.btnAddMultiFromGalleryToAlbum){
                    if (getSelectedItems().size()==0)
                    {
                        Toast.makeText(context, R.string.no_images, Toast.LENGTH_SHORT).show();
                    }
                    else {
                        ArrayList<String> selectedPaths = new ArrayList<>();
                        for(int i = 0; i < getSelectedItems().size(); ++i){
                            selectedPaths.add(getSelectedItems().get(i).getPath());
                        }
                        Gson gson = new Gson();
                        ((MainActivity)context).onMsgFromFragToMain("ADD-TO-ALBUM", gson.toJson(selectedPaths));
                        mode.finish();
                    }
                    
                }
                else if(id == R.id.btnDeleteMultiFromGallery){
                    if (getSelectedItems().size()==0)
                    {
                        Toast.makeText(context, R.string.no_images, Toast.LENGTH_SHORT).show();
                    }
                    else{
                        new DeleteTask(context).execute(getSelectedItems());
                        mode.finish();
                    }

                }

                notifyDataSetChanged();
                mode.setTitle(context.getString(R.string.selected) + " " + selectedCount);
                return true;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                onChooseMulti = false;
                multiMode = null;
                for(int i = 0; i < listGroups.size(); ++i){
                    selectedItemsIds.get(i).clear();
                }
                btnv.setVisibility(View.VISIBLE);
                notifyDataSetChanged();

            }
        });
    }

    @Override
    public void setMultiSelect(int imagePos, int groupPos) {
        changeOnMultiChooseMode();
        onItemClick(imagePos, groupPos);
    }

    @Override
    public void onItemClick(int imagePos, int groupPos) {
        int oldPos = selectedItemsIds.get(groupPos).size();

        if(!selectedItemsIds.get(groupPos).get(imagePos)){
            selectedItemsIds.get(groupPos).put(imagePos, true);
            selectedCount++;
        } else{
            selectedItemsIds.get(groupPos).delete(imagePos);
            selectedCount--;
        }

        if(selectedItemsIds.get(groupPos).size() == listGroups.get(groupPos).getList().size() || oldPos == listGroups.get(groupPos).getList().size()){
            notifyItemChanged(groupPos);
        }

        multiMode.setTitle(context.getString(R.string.selected) + " " + selectedCount);
    }

    @Override
    public boolean isSelectedItem(int imagePos, int groupPos){
        return selectedItemsIds.get(groupPos).get(imagePos);
    }

    public ArrayList<Image> getSelectedItems(){
        ArrayList<Image> selected = new ArrayList<>();
        for(int i = 0; i < selectedItemsIds.size(); ++i){
            for(int j = 0; j < selectedItemsIds.get(i).size(); ++j){
                selected.add(listGroups.get(i).getList().get(selectedItemsIds.get(i).keyAt(j)));
            }
        }

        Log.d("selected", selected.toString());
        return selected;
    }

}

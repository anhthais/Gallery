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

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gallery.MultiSelectCallbacks;
import com.example.gallery.R;
import com.example.gallery.object.Image;
import com.example.gallery.object.ImageGroup;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;

public class ImageGroupAdapter extends RecyclerView.Adapter<ImageGroupAdapter.ImageGroupViewHolder> implements MultiSelectCallbacks {
    private final int COL_SPAN_VIEW = 3;
    private Context context;
    private ArrayList<ImageGroup> listGroups;
    private boolean onChooseMulti = false;
    private ArrayList<SparseBooleanArray> selectedItemsIds;
    private ActionMode mode;
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
        }
    }

    public ImageGroupAdapter(Context context, ArrayList<ImageGroup> groupList) {
        this.context = context;
        this.listGroups = groupList;
        this.selectedItemsIds = new ArrayList<>();
        for(int i = 0; i < groupList.size(); ++i){
            SparseBooleanArray idsInGroup = new SparseBooleanArray();;
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
        ImageGroup group = listGroups.get(position);
        if (group == null)
            return;

        holder.txtId.setText(group.getId());
        holder.imgList.setLayoutManager(new GridLayoutManager(context, COL_SPAN_VIEW));

        ImageAdapter imgList = new ImageAdapter(context, group.getList());
        imgList.setListGroups(listGroups);
        imgList.setGroupPos(position);
        imgList.setMultiSelectCallbacks(this);

        imgList.changeMultiMode(onChooseMulti);
        holder.imgList.setAdapter(imgList);

        if(onChooseMulti){
            holder.checkBox.setVisibility(View.VISIBLE);
            if(selectedItemsIds.get(position).size() == listGroups.get(position).getList().size()){
                holder.checkBox.setChecked(true);
            } else {
                holder.checkBox.setChecked(false);
            }
        }else{
            holder.checkBox.setVisibility(View.INVISIBLE);
            holder.checkBox.setChecked(false);
        }

        holder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                holder.checkBox.setChecked(!holder.checkBox.isChecked());
                if(holder.checkBox.isChecked()){
                    for(int i = 0; i < listGroups.get(position).getList().size(); ++i){
                        if(!selectedItemsIds.get(position).get(i)){
                            selectedItemsIds.get(position).put(i, true);
                        }
                    }
                }
                else{
                    for(int i = 0; i < listGroups.get(position).getList().size(); ++i){
                        if(selectedItemsIds.get(position).get(i)){
                            selectedItemsIds.get(position).delete(i);
                        }
                    }
                }

                updateActionTitle();
                notifyDataSetChanged();
            }
        });
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
        mode = ma.startSupportActionMode(new ActionMode.Callback() {
            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
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
                            }
                        }
                    }
                }
                else if(id == R.id.btnAddMultiFromGalleryToAlbum){

                }
                else if(id == R.id.btnDeleteMultiFromGallery){

                }

                updateActionTitle();
                notifyDataSetChanged();
                return true;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                onChooseMulti = false;
                mode = null;
                Log.d("multiSelect", selectedItemsIds.toString());
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
        } else{
            selectedItemsIds.get(groupPos).delete(imagePos);
        }

        if(selectedItemsIds.get(groupPos).size() == listGroups.get(groupPos).getList().size() || oldPos == listGroups.get(groupPos).getList().size()){
            notifyDataSetChanged();
        }

        updateActionTitle();
    }

    @Override
    public boolean isSelectedItem(int imagePos, int groupPos){
        if(selectedItemsIds.get(groupPos).get(imagePos)){
            return true;
        }
        return false;
    }

    public void updateActionTitle(){
        int totalSelected = 0;
        for(int i = 0; i < selectedItemsIds.size(); ++i){
            totalSelected += selectedItemsIds.get(i).size();
        }
        mode.setTitle("Đã chọn " + totalSelected);
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

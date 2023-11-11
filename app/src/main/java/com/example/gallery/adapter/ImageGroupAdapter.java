package com.example.gallery.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gallery.R;
import com.example.gallery.object.ImageGroup;

import java.util.ArrayList;

public class ImageGroupAdapter extends RecyclerView.Adapter<ImageGroupAdapter.ImageGroupViewHolder> {
    private final int COL_SPAN_VIEW = 3;
    private Context context;
    private ArrayList<ImageGroup> listGroups;
    private boolean onChooseMulti = false;

    public class ImageGroupViewHolder extends RecyclerView.ViewHolder{
        private TextView txtId;
        private RecyclerView imgList;

        public ImageGroupViewHolder(View view) {
            super(view);
            txtId = view.findViewById(R.id.gallery_fragment_item_textView);
            imgList = view.findViewById(R.id.gallery_fragment_item_image_groups);
        }
    }

    public ImageGroupAdapter(Context context, ArrayList<ImageGroup> groupList) {
        this.context = context;
        this.listGroups = groupList;
    }

    // setters
//    public void setData(ArrayList<ImageGroup> groups){
//        this.listGroups = groups;
//        notifyDataSetChanged();
//    }

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

        imgList.changeMultiMode(onChooseMulti);
        holder.imgList.setAdapter(imgList);
    }

    @Override
    public int getItemCount() {
        if (listGroups != null){
            return listGroups.size();
        }
        return 0;
    }
    public void changeOnMultiChooseMode()
    {
        this.onChooseMulti = true;

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
                onChooseMulti = false;
                notifyDataSetChanged();
            }
        });
        notifyDataSetChanged();
    }
}

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

import com.example.gallery.MainActivity;
import com.example.gallery.R;
import com.example.gallery.object.Album;

import java.util.ArrayList;

public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.ViewHolder> {
    private Context context;
    private ArrayList<Album> albums;
    private SparseBooleanArray selectedItemsIds;

    private boolean checkBoxEnable=false;

    public AlbumAdapter(Context context,ArrayList<Album> albums_list){
        this.albums=albums_list;
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
        private TextView albumNameTxtView;
        private CheckBox checkBox;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemImageView=itemView.findViewById(R.id.imageAlbumItem);
            albumNameTxtView=itemView.findViewById(R.id.albumNameTextView);
            checkBox=itemView.findViewById(R.id.checkBoxAlbumItem);
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
                        ((MainActivity)context).onMsgFromFragToMain("ALBUM",albums.get(getAdapterPosition()).getName());
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
        View albumView=inflater.inflate(R.layout.album_item,parent,false);
        return new ViewHolder(albumView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Album album=albums.get(position);

        holder.itemImageView.setImageResource(R.drawable.ic_folder_24);
        holder.albumNameTxtView.setText(album.getName());
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
        if(albums==null)
            return 0;
        return albums.size();
    }
}

package com.example.gallery;

import android.app.Activity;
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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.appcompat.view.SupportActionModeWrapper;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.ViewHolder> {
    private Context context;
    private ArrayList<Album> albums;
    private SparseBooleanArray selectedItemsIds;

    private boolean checkBoxEnable=false;

    public AlbumAdapter(Context context,ArrayList<Album> albums_list ){
        this.albums=albums_list;
        this.context=context;
        selectedItemsIds=new SparseBooleanArray();
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
                        Toast.makeText(context,"aa"+position+selectedItemsIds.size(),Toast.LENGTH_SHORT).show();

                    }
                    else{
                        //show all picture in album
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
        holder.itemImageView.setImageResource(R.drawable.meomeo);
        holder.albumNameTxtView.setText(album.getName());
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
    public SparseBooleanArray getSelectedItemsIds(){
        return this.selectedItemsIds;
    }
    @Override
    public int getItemCount() {
        return albums.size();
    }
}

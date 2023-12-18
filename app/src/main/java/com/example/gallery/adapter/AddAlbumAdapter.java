package com.example.gallery.adapter;

import static java.nio.file.StandardCopyOption.ATOMIC_MOVE;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gallery.ImageActivity;
import com.example.gallery.MainActivity;
import com.example.gallery.R;
import com.example.gallery.asynctask.CopyTask;
import com.example.gallery.object.Album;
import com.example.gallery.object.Image;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

public class AddAlbumAdapter extends RecyclerView.Adapter<AddAlbumAdapter.ViewHolder> {
    private Context context;
    private ArrayList<Album> albums;
    private ArrayList<String> paths;

    public AddAlbumAdapter(Context context,ArrayList<Album> albums_list, ArrayList<String> paths){
        this.albums=albums_list;
        this.context=context;
        this.paths=paths;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private ImageView itemImageView;
        private TextView albumNameTxtView,albumImageAmountTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemImageView=itemView.findViewById(R.id.imageAlbumItem);
            albumNameTxtView=itemView.findViewById(R.id.albumNameTextView);
            albumImageAmountTextView=itemView.findViewById(R.id.albumImageAmount);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new CopyTask(context, albums.get(getAdapterPosition())).execute(paths);
                    if(context instanceof ImageActivity){
                        ((ImageActivity) context).getSupportFragmentManager().popBackStack();
                    }
                    else if(context instanceof MainActivity){
                        ((MainActivity) context).getSupportFragmentManager().popBackStack();
                    }
                }
            });

        }
    }
    @NonNull
    @Override
    public AddAlbumAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater=LayoutInflater.from(parent.getContext());
        View albumView=inflater.inflate(R.layout.album_item,parent,false);
        return new AddAlbumAdapter.ViewHolder(albumView);
    }

    @Override
    public void onBindViewHolder(@NonNull AddAlbumAdapter.ViewHolder holder, int position) {
        Album album=albums.get(position);
        if(album.getThumbnail()!=null && !album.getThumbnail().isEmpty()){
            Bitmap bitmap= BitmapFactory.decodeFile(album.getThumbnail());
            holder.itemImageView.setImageBitmap(bitmap);
        }else{
            holder.itemImageView.setImageResource(R.drawable.logo_app);
        }

        holder.albumNameTxtView.setText(album.getName());
        holder.albumImageAmountTextView.setVisibility(View.INVISIBLE);

    }
    @Override
    public int getItemCount() {
        if(albums==null)
            return 0;
        return albums.size();
    }
}
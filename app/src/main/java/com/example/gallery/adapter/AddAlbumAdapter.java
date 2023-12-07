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
import com.example.gallery.object.Album;

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
    private String path;

    public AddAlbumAdapter(Context context,ArrayList<Album> albums_list, String path){
        this.albums=albums_list;
        this.context=context;
        this.path=path;
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
                    //handle add to album
                    Path source=Paths.get(path);
                    Path dest=Paths.get(albums.get(getAdapterPosition()).getPath(),
                            path.substring(path.lastIndexOf("/")+1));

                    File file=new File(dest.toString());
                    try {
                        file.createNewFile();
                    } catch (IOException e) {
                        Toast.makeText(context, R.string.cannot_add, Toast.LENGTH_SHORT).show();
                    }
                    try {
                        Files.copy(source,dest, StandardCopyOption.REPLACE_EXISTING);
                    } catch (IOException e) {
                        Toast.makeText(context, R.string.existed_cannot_add, Toast.LENGTH_SHORT).show();
                        file.delete();
                    }
                    ((ImageActivity)context).getSupportFragmentManager().popBackStackImmediate();
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
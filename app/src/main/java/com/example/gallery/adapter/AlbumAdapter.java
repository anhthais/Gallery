package com.example.gallery.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import androidx.recyclerview.widget.RecyclerView;

import com.example.gallery.MainActivity;
import com.example.gallery.R;
import com.example.gallery.object.Album;

import java.util.ArrayList;

public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.ViewHolder> {
    private Context context;
    private ArrayList<Album> albums;


    public AlbumAdapter(Context context,ArrayList<Album> albums_list){
        this.albums=albums_list;
        this.context=context;
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

                    //show all picture in album
                    Toast.makeText(context, ""+albums.get(getAdapterPosition()).getAll_album_pictures().size(), Toast.LENGTH_SHORT).show();
                    ((MainActivity)context).onMsgFromFragToMain("ALBUM",albums.get(getAdapterPosition()).getPath());

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
        if(albums.get(position).getAll_album_pictures().size()>0){
            Bitmap bitmap=BitmapFactory.decodeFile(albums.get(position).getAll_album_pictures().get(0).getPath());
            holder.itemImageView.setImageBitmap(bitmap);
        }
        else{
            holder.itemImageView.setImageResource(R.drawable.logo_app);
        }
        holder.albumNameTxtView.setText(album.getName());
        holder.albumImageAmountTextView.setText(album.getAll_album_pictures().size()+"");

    }
    @Override
    public int getItemCount() {
        if(albums==null)
            return 0;
        return albums.size();
    }
}
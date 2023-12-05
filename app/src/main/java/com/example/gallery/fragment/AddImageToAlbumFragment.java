package com.example.gallery.fragment;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gallery.R;
import com.example.gallery.adapter.AddAlbumAdapter;
import com.example.gallery.object.Album;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

public class AddImageToAlbumFragment extends Fragment{
    private Context context;
    private AddAlbumAdapter album_adapter;
    private ArrayList<Album> albums;
    public String path;
    private RecyclerView album_recyclerView;
    private boolean isDel = false;
    private int index = -1;
    public AddImageToAlbumFragment(Context context, ArrayList<Album> albums,String path){
        this.context=context;
        this.albums=albums;
        this.path=path;
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            context = getActivity(); // use this reference to invoke main callbacks
        } catch (IllegalStateException e) {
            throw new IllegalStateException("MainActivity must implement callbacks");
        }

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View AddImageToAlbumFragment=inflater.inflate(R.layout.album_fragment,container,false);
        album_adapter=new AddAlbumAdapter(context,albums,path);
        album_recyclerView=AddImageToAlbumFragment.findViewById(R.id.albumFragmentRecyclerView);
        album_recyclerView.setAdapter(album_adapter);
        album_recyclerView.setLayoutManager(new GridLayoutManager(context,2));
        return AddImageToAlbumFragment;
    }
}

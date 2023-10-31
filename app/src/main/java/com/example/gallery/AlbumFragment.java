package com.example.gallery;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class AlbumFragment extends Fragment implements FragmentCallBacks {
    private Context context;
    private AlbumAdapter album_adapter;
    private ArrayList<Album> albums;
    private RecyclerView album_recyclerView;
    public AlbumFragment(Context context, ArrayList<Album> album_list){
        this.context=context;
        this.albums=album_list;
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View albumFragment=inflater.inflate(R.layout.album_fragment,container,false);
        album_adapter=new AlbumAdapter(context,albums);
        album_recyclerView.setAdapter(album_adapter);
        album_recyclerView.setLayoutManager(new GridLayoutManager(context,2));
        return albumFragment;
    }


    @Override
    public void onMsgFromMainToFragment(String strValue) {

    }
}

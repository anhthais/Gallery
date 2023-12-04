package com.example.gallery.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gallery.MainActivity;
import com.example.gallery.R;
import com.example.gallery.adapter.HideAdapter;
import com.example.gallery.object.Album;
import com.example.gallery.object.Image;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

public class HideFragment extends Fragment {
    private HideAdapter image_adapter;
    private Context context;
    private RecyclerView recyclerView;
    private Album album;
    public static HideFragment getInstance(){
        return new HideFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.context=getContext();

        //((MainActivity)context).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((MainActivity)context).getSupportActionBar().setHomeButtonEnabled(true);

    }
    public void loadHideAlbum(){
        album = new Album("Hide", Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath()+"/.nomedia");
        SharedPreferences hidePref=context.getSharedPreferences("GALLERY",context.MODE_PRIVATE);
        Gson gson = new Gson();
        String hideCurrentJSON=hidePref.getString("HIDE-CURRENT",null);
        if(hideCurrentJSON!=null && !hideCurrentJSON.isEmpty()){
            ArrayList<String> all_hide_path=gson.fromJson(hideCurrentJSON,new TypeToken<ArrayList<String>>(){}.getType());
            for(int i=0;i<all_hide_path.size();i++){
                album.addImageToAlbum(new Image(all_hide_path.get(i)));
            }
        }
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        loadHideAlbum();
        ((MainActivity)context).getSupportActionBar().setTitle(album.getName());
        View view=inflater.inflate(R.layout.hide_fragment,container,false);
        image_adapter=new HideAdapter(context,album.getAll_album_pictures());
        recyclerView=view.findViewById(R.id.hideFragmentRecyclerView);
        recyclerView.setAdapter(image_adapter);
        recyclerView.setLayoutManager(new GridLayoutManager(context,3));
        return view;
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ((MainActivity)context).getSupportActionBar().setTitle("Gallery");
        ((MainActivity)context).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        ((MainActivity)context).getMenu().findItem(R.id.btnRenameAlbum).setVisible(false);
        ((MainActivity)context).getMenu().findItem(R.id.btnAddNewAlbum).setVisible(true);
    }
    public void removeImage(ArrayList<String>path){
        image_adapter.removeImage(path);
    }


}


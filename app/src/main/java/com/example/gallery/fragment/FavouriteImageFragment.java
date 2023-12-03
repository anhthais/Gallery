package com.example.gallery.fragment;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gallery.FragmentCallBacks;
import com.example.gallery.MainActivity;
import com.example.gallery.MultiSelectModeCallbacks;
import com.example.gallery.R;
import com.example.gallery.adapter.ImageAdapter;
import com.example.gallery.object.Image;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

public class FavouriteImageFragment extends Fragment implements FragmentCallBacks, MultiSelectModeCallbacks {
    private Context context;
    private ImageAdapter imageAdapter;
    private ArrayList<Image> favImages = null;
    private ArrayList<Image> allImages = null;
    private RecyclerView recyclerView;
    private MainActivity main;
    public static FavouriteImageFragment getInstance(){
        return new FavouriteImageFragment();
    }
    @Nullable
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            context = getActivity(); // use this reference to invoke main callbacks
            main = (MainActivity) getActivity();
        } catch (IllegalStateException e) {
            throw new IllegalStateException("MainActivity must implement callbacks");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.favourite_image_fragment,container,false);
        recyclerView = view.findViewById(R.id.recycleFavouriteImages);
        recyclerView.setLayoutManager(new GridLayoutManager(context,3));
//        allImages = ((MainActivity)context).allImages;
//        favImages = new ArrayList<>();
//        for(int i = 0 ; i < allImages.size(); ++i){
//            if(allImages.get(i).isFavorite()){
//                favImages.add(allImages.get(i));
//            }
//        }
//        imageAdapter = new ImageAdapter(context, favImages);
//        recyclerView.setAdapter(new ImageAdapter(context, favImages));

        ((MainActivity)context).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((MainActivity)context).getSupportActionBar().setHomeButtonEnabled(true);
        ((MainActivity)context).getSupportActionBar().setTitle("Yêu thích");

        return view;
    }
    @Override
    public void onResume(){
        super.onResume();
        allImages = ((MainActivity)context).allImages;
        favImages = new ArrayList<>();
        for(int i = 0 ; i < allImages.size(); ++i){
            if(allImages.get(i).isFavorite()){
                favImages.add(allImages.get(i));
            }
        }
        imageAdapter = new ImageAdapter(context, favImages);
        recyclerView.setAdapter(imageAdapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ((MainActivity)context).getSupportActionBar().setTitle("Gallery");
        ((MainActivity)context).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        ((MainActivity)context).getMenu().findItem(R.id.btnRenameAlbum).setVisible(false);
        ((MainActivity)context).getMenu().findItem(R.id.btnAddNewAlbum).setVisible(true);
    }

    @Override
    public void onMsgFromMainToFragment(String strValue) {

    }

    @Override
    public void changeOnMultiChooseMode(){
        imageAdapter.changeOnMultiChooseMode();
    }
}

package com.example.gallery.fragment;

import android.app.AlarmManager;
import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gallery.ImageActivity;
import com.example.gallery.MainActivity;
import com.example.gallery.MultiSelectModeCallbacks;
import com.example.gallery.R;
import com.example.gallery.SlideShowActivity;
import com.example.gallery.adapter.ImageAdapter;
import com.example.gallery.object.Album;
import com.example.gallery.object.Image;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class ImageFragment extends Fragment implements MultiSelectModeCallbacks {
    private ImageAdapter image_adapter;
    private Context context;
    private RecyclerView recyclerView;
    private Album album;
    private MainActivity main;
    public static ImageFragment getInstance(){
        return new ImageFragment();
    }
    public ImageFragment(){

    }
    public ImageFragment(Context context, Album album){
        this.context = context;
        this.album = album;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        main = (MainActivity) getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.image_fragment,container,false);
        recyclerView = view.findViewById(R.id.image_fragment_list);
        if(main != null && main.album_list != null && main.album_list.get(main.curIdxAlbum) != null){
            album = main.album_list.get(main.curIdxAlbum);
            image_adapter = new ImageAdapter(context, album.getAll_album_pictures());
        } else {
            image_adapter = new ImageAdapter(context, new ArrayList<>());
        }
        recyclerView.setAdapter(image_adapter);
        recyclerView.setLayoutManager(new WrapContentGridLayoutManager(context,3));

        ((MainActivity)context).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((MainActivity)context).getSupportActionBar().setHomeButtonEnabled(true);
        ((MainActivity)context).getSupportActionBar().setTitle(album.getName());

        return view;
    }
    @Override
    public void onResume(){
        super.onResume();
    }

    public void updateView(){
        if(main.album_list.get(main.curIdxAlbum) != null){
            album = main.album_list.get(main.curIdxAlbum);
            image_adapter.submitList(album.getAll_album_pictures());
        } else {
            image_adapter.submitList(new ArrayList<>());
        }
    }

    public void RenameAlbum(){
        Dialog addDialog=new Dialog(context);
        addDialog.setContentView(R.layout.add_album_dialog);
        EditText editText=addDialog.findViewById(R.id.addNewAlbumEditText);
        TextView textView=addDialog.findViewById(R.id.addNewAlbumTextView);
        Button ok=addDialog.findViewById(R.id.btnOKAddAlbum);
        Button cancel=addDialog.findViewById(R.id.btnCancelAddAlbum);
        textView.setText("Đổi tên Album");
        addDialog.create();
        addDialog.show();
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(editText.getText().toString().length()==0){
                    Toast.makeText(context, "Nhập tên Album", Toast.LENGTH_SHORT).show();
                }
                else{
                    album.Rename(editText.getText().toString());
                    ((MainActivity)context).getSupportActionBar().setTitle(album.getName());
                    addDialog.cancel();
                }
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addDialog.cancel();
            }
        });

    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ((MainActivity)context).getSupportActionBar().setTitle(R.string.app_name);
        ((MainActivity)context).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        ((MainActivity)context).getMenu().findItem(R.id.btnAddNewAlbum).setVisible(true);
    }
    public void beginSlideShow(){
        if(album.getAll_album_pictures()==null||album.getAll_album_pictures().size()==0){
            Toast.makeText(context, R.string.nothing_to_slide_show, Toast.LENGTH_SHORT).show();
            return;
        }

        Dialog addDialog=new Dialog(context);
        addDialog.setContentView(R.layout.slide_show_dialog);
        EditText editText=addDialog.findViewById(R.id.slideshowEditText);
        Button ok=addDialog.findViewById(R.id.btnOKSlideShow);
        Button cancel=addDialog.findViewById(R.id.btnCancelSlideShow);
        TextView message=addDialog.findViewById(R.id.slideshowmessage);
        addDialog.create();
        addDialog.show();
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(editText.getText().toString().length()==0){
                    Toast.makeText(context, R.string.enter_time, Toast.LENGTH_SHORT).show();
                }
                else{
                    try{
                        int time=Integer.parseInt(editText.getText().toString());
                        Intent intent = new Intent(getActivity(), SlideShowActivity.class);
                        intent.putExtra("time",time);
                        intent.putParcelableArrayListExtra("images", album.getAll_album_pictures());
                        startActivity(intent);
                        addDialog.cancel();
                    }
                    catch (Exception e){
                        message.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addDialog.cancel();
            }
        });
    }

    @Override
    public void changeOnMultiChooseMode(){
        image_adapter.changeOnMultiChooseMode();
    }

    public class WrapContentGridLayoutManager extends GridLayoutManager {
        public WrapContentGridLayoutManager(Context context, int count){
            super(context, count);
        }
        @Override
        public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
            try {
                super.onLayoutChildren(recycler, state);
            } catch (IndexOutOfBoundsException e) {
                Log.e("TAG", "meet a IOOBE in RecyclerView");
            }
        }
    }
}


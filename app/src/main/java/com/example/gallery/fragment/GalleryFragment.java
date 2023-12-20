package com.example.gallery.fragment;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.example.gallery.FragmentCallBacks;
import com.example.gallery.MainActivity;
import com.example.gallery.MainCallBackObjectData;
import com.example.gallery.MultiSelectModeCallbacks;
import com.example.gallery.R;
import com.example.gallery.adapter.ImageGroupAdapter;
import com.example.gallery.helper.DateConverter;
import com.example.gallery.helper.LocalStorageReader;
import com.example.gallery.object.Image;
import com.example.gallery.object.ImageGroup;
import com.example.gallery.object.Statistic;
import com.example.gallery.object.TrashItem;
import com.example.gallery.helper.DownLoadImage;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.ArrayList;

public class GalleryFragment extends Fragment implements FragmentCallBacks, MultiSelectModeCallbacks {
    private RecyclerView recyclerView;
    private ImageGroupAdapter imageGroupAdapter;
    private ArrayList<ImageGroup> groupList;
    private ArrayList<Statistic> statisticList;
    private Context context;
    private MainActivity main;
    private FloatingActionButton addImageFromLink;

    // TODO: adjust to singleton
    public static GalleryFragment getInstance(){
        return new GalleryFragment();
    }
    private MainCallBackObjectData callback;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof MainCallBackObjectData) {
            callback = (MainCallBackObjectData) context;
        } else {
            throw new ClassCastException(context.toString() + " must implement MyCallback");
        }
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            context = getActivity(); // use this reference to invoke main callbacks
            main = (MainActivity) getActivity();
        } catch (IllegalStateException e) {
            throw new IllegalStateException("MainActivity must implement callbacks");
        }
    }

    public void updateView(){
        if(main != null){
            groupList = main.imageGroupsByDate;
            imageGroupAdapter.submitList(groupList);
            statisticList = getStatisticGroupList(groupList);
            passObjectToActivity(statisticList);
        }
    }

    public void forceUpdateView(){
        if(main != null){
            groupList = main.imageGroupsByDate;
            imageGroupAdapter = new ImageGroupAdapter(context, groupList);
            recyclerView.setAdapter(imageGroupAdapter);
            statisticList = getStatisticGroupList(groupList);
            passObjectToActivity(statisticList);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.gallery_fragment, container, false);
        recyclerView = view.findViewById(R.id.recycleImages);
        addImageFromLink = view.findViewById(R.id.btnAddImage);
        groupList = main.imageGroupsByDate;
        if(groupList == null) groupList = new ArrayList<>();
        imageGroupAdapter = new ImageGroupAdapter(context, groupList);
        recyclerView.setAdapter(imageGroupAdapter);
        recyclerView.setLayoutManager(new WrapContentLinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        // Tính dung lượng và số lượng của mỗi group list,
        statisticList = getStatisticGroupList(groupList);
        passObjectToActivity(statisticList);
        addImageFromLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog addDialog=new Dialog(context);
                addDialog.setContentView(R.layout.add_image_dialog);
                EditText editText=addDialog.findViewById(R.id.addNewAlbumEditText);
                Button ok=addDialog.findViewById(R.id.btnOKAddAlbum);
                Button cancel=addDialog.findViewById(R.id.btnCancelAddAlbum);
                Button camera = addDialog.findViewById(R.id.btnAddImgFromCamera);
                addDialog.create();
                addDialog.show();

                ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(editText.getText().toString().length()==0){
                            Toast.makeText(context, R.string.enter_link, Toast.LENGTH_SHORT).show();
                        }
                        else{
                            DownloadImageFromLink(editText.getText().toString());

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
                camera.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        addDialog.cancel();


                        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                        File f = new File(android.os.Environment.getExternalStorageDirectory(), "imagename.jpg");
//                        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
                        ((MainActivity) context).startActivityForResult(cameraIntent, 1125);

                    }
                });
            }
        });
        return view;
    }

    // TODO: delete and adjust the code below: statistic and delete
    private  ArrayList<Statistic> getStatisticGroupList(ArrayList<ImageGroup> groupList) {
        ArrayList<Statistic> result = new ArrayList<Statistic>();
        int count=0;
        if(groupList!=null){
            count=groupList.size();
        }
        for(int i=0;i< count;i++){
            Statistic temp = new Statistic();
            Float sizeInDisk = 0f;
            temp.setId(groupList.get(i).getId());
            temp.setCount(groupList.get(i).getList().size());

            // Tinh dung luong cua tung anh trong list
            for(int j =0;j<groupList.get(i).getList().size();j++){
                sizeInDisk += calculateImageSize(groupList.get(i).getList().get(j).getPath());
            }
            String formatSizeDisk  = String.format("%.2f MB",sizeInDisk);
            temp.setWeight(formatSizeDisk);
            result.add((temp));
        }
        return result;
    }

    private float calculateImageSize(String path) {
        File file = new File(path);
        // file.length() return Byte
        float fileSizeInKB = file.length()/(1024f * 1024f);
        return fileSizeInKB;
    }
    private void passObjectToActivity(ArrayList<Statistic> statistic) {
        if (callback != null) {
            callback.onObjectPassed(statistic);
        }
    }

    void DownloadImageFromLink(String ImageUrl) {
        //Asynctask to create a thread to download image in the background
        Toast.makeText(context, R.string.downloading, Toast.LENGTH_SHORT).show();
        try{
            new DownLoadImage(context).execute(ImageUrl);
        }catch (Exception e){
        }
        
    }
    @Override
    public void onMsgFromMainToFragment(String strValue) {
    }

    @Override
    public void changeOnMultiChooseMode()
    {
        imageGroupAdapter.changeOnMultiChooseMode();
    }

    public class WrapContentLinearLayoutManager extends LinearLayoutManager {
        public WrapContentLinearLayoutManager(Context context, int orientation, boolean reverserLayout){
            super(context, orientation, reverserLayout);
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


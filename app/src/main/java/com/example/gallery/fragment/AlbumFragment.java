package com.example.gallery.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gallery.FragmentCallBacks;
import com.example.gallery.MainActivity;
import com.example.gallery.R;
import com.example.gallery.adapter.AlbumAdapter;
import com.example.gallery.adapter.ImageGroupAdapter;
import com.example.gallery.object.Album;
import com.google.gson.Gson;

import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class AlbumFragment extends Fragment implements FragmentCallBacks {
    private Context context;
    private AlbumAdapter album_adapter;
    private ArrayList<Album> albums;
    private RecyclerView album_recyclerView;
    private boolean isDel = false;
    private int index = -1;
    private MainActivity main;
    public static AlbumFragment getInstance(){
        return new AlbumFragment();
    }
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
        View albumFragment=inflater.inflate(R.layout.album_fragment,container,false);
        album_recyclerView = albumFragment.findViewById(R.id.albumFragmentRecyclerView);
        albums = main.album_list;
        if(albums == null) albums = new ArrayList<>();
        album_adapter = new AlbumAdapter(context, albums);
        album_recyclerView.setAdapter(album_adapter);
        album_recyclerView.setLayoutManager(new GridLayoutManager(context,2));
        return albumFragment;
    }

    public void updateView(){
        if(main != null){
            albums = main.album_list;
            album_adapter = new AlbumAdapter(context, albums);
            album_recyclerView.setAdapter(album_adapter);
        }
    }

    public void addNewAlbum(){
        Dialog addDialog=new Dialog(context);
        addDialog.setContentView(R.layout.add_album_dialog);
        EditText editText=addDialog.findViewById(R.id.addNewAlbumEditText);
        Button ok=addDialog.findViewById(R.id.btnOKAddAlbum);
        Button cancel=addDialog.findViewById(R.id.btnCancelAddAlbum);
        addDialog.create();
        addDialog.show();
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(editText.getText().toString().length()==0){
                    Toast.makeText(context, R.string.enter_album_name, Toast.LENGTH_SHORT).show();
                }
                else{
                    Gson gson=new Gson();
                    String dcimPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath();
                    Path albumPath= Paths.get(dcimPath,editText.getText().toString());
                    if(Files.exists(albumPath)){
                        Toast.makeText(context, R.string.album_existed, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    else {
                        File file=new File(albumPath.toString());
                        file.mkdir();
                        Album a=new Album(editText.getText().toString(),albumPath.toString());
                        albums.add(a);
                        album_adapter.notifyItemInserted(albums.size()-1);
                    }

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

    public boolean deleteAlbum(String strValue)
    {
        for(int i=0;i<albums.size();i++){
            if(albums.get(i).getName().equals(strValue)){
                index=i;
                break;
            }
        }
        Dialog addDialog=new Dialog(context);
        addDialog.setContentView(R.layout.delete_album_dialog);
        Button ok=addDialog.findViewById(R.id.btnOKDeleteAlbum);
        Button cancel=addDialog.findViewById(R.id.btnCancelDeleteAlbum);
        addDialog.create();
        addDialog.show();
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                if(editText.getText().toString().length()==0){

                    Toast.makeText(context, "Nhập tên Album", Toast.LENGTH_SHORT).show();
                }
                else{
                    Album a=new Album(editText.getText().toString());
                    albums.add(a);
                    addDialog.cancel();

                }*/
//                String album_deleted=albums.get(index).getName();
//                albums.remove(albums.get(index));
//                isDel = true;
//                addDialog.cancel();
//                //save in local
//                Gson gson=new Gson();
//                SharedPreferences albumPref= context.getSharedPreferences("GALLERY", Context.MODE_PRIVATE);
//                SharedPreferences.Editor editor=albumPref.edit();
//                ArrayList<String> album_save=new ArrayList<>();
//                for(int i=0;i<albums.size();i++){
//                    album_save.add(albums.get(i).getName());
//                }
//                String albumjson=gson.toJson(album_save);
//                editor.putString("ALBUM",albumjson);
//                editor.putString(album_deleted,"");
//                editor.commit();
                ((MainActivity)context).onMsgFromFragToMain("DELETE-ALBUM", "yes");
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // addDialog.cancel();
                isDel= false;
                addDialog.cancel();
            }

        });

        return this.isDel;
    };



    @Override
    public void onMsgFromMainToFragment(String strValue) {

    }
}

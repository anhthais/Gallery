package com.example.gallery;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.Nullable;

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
        album_recyclerView=albumFragment.findViewById(R.id.albumFragmentRecyclerView);
        album_recyclerView.setAdapter(album_adapter);
        album_recyclerView.setLayoutManager(new GridLayoutManager(context,2));
        return albumFragment;
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
                    Toast.makeText(context, "Nhập tên Album", Toast.LENGTH_SHORT).show();
                }
                else{
                    Album a=new Album(editText.getText().toString());
                    albums.add(a);
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
    public void onMsgFromMainToFragment(String strValue) {

    }
}

package com.example.gallery.fragment;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gallery.FragmentCallBacks;
import com.example.gallery.MainActivity;
import com.example.gallery.R;
import com.example.gallery.adapter.AlbumAdapter;
import com.example.gallery.adapter.TrashAdapter;
import com.example.gallery.object.Album;
import com.example.gallery.object.TrashItem;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

public class TrashFragment extends Fragment implements FragmentCallBacks {
    private Context context;
    private TrashAdapter trash_adapter;
    private ArrayList<TrashItem> trash_items_list;
    private RecyclerView trash_RecyclerView;
    private boolean isDel = false;
    private int index = -1;
    public static TrashFragment getInstance(){
        return new TrashFragment();
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            context = getActivity(); // use this reference to invoke main callbacks
            trash_items_list=((MainActivity)context).getTrashItem_list();
        } catch (IllegalStateException e) {
            throw new IllegalStateException("MainActivity must implement callbacks");
        }

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View TrashFragment=inflater.inflate(R.layout.trash,container,false);
        trash_adapter= new TrashAdapter(context, R.layout.trash_item , trash_items_list);
        trash_RecyclerView=TrashFragment.findViewById(R.id.trashFragmentRecyclerView);
        trash_RecyclerView.setAdapter(trash_adapter);
        trash_RecyclerView.setLayoutManager(new GridLayoutManager(context,4));
        return TrashFragment;
    }
   /* public void addNewAlbum(){
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
    }*/

  /* public boolean deleteAlbum(String strValue)
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

                }
                albums.remove(albums.get(index));
                isDel = true;
                addDialog.cancel();
                ((MainActivity)context).onMsgFromFragToMain("DELETE-ALBUM", "yes");
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // addDialog.cancel();
                isDel= false;
                addDialog.cancel();
                //((MainActivity)context).onMsgFromFragToMain("DELETE-ALBUM", "no");


            }

        });

        return this.isDel;
    };*/


    @Override
    public void onMsgFromMainToFragment(String strValue) {

    }
}
package com.example.gallery.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.FileObserver;
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
import com.example.gallery.adapter.TrashAdapter;
import com.example.gallery.object.TrashItem;

import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.ArrayList;

public class TrashFragment extends Fragment implements FragmentCallBacks, MultiSelectModeCallbacks {
    private Context context;
    private TrashAdapter trash_adapter;
    private ArrayList<TrashItem> trashItems;
    private RecyclerView trash_RecyclerView;
    private MainActivity main;

    public static TrashFragment getInstance() {
        return new TrashFragment();
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
        View TrashFragment = inflater.inflate(R.layout.trash_fragment, container, false);
        trash_RecyclerView = TrashFragment.findViewById(R.id.trashFragmentRecyclerView);
        trashItems = new ArrayList<>();
        if (main != null && main.trashItems != null) {
            trashItems = main.trashItems;
        }
        trash_adapter = new TrashAdapter(context, trashItems);
        trash_RecyclerView.setLayoutManager(new WrapContentGridLayoutManager(context, 3));
        trash_RecyclerView.setAdapter(trash_adapter);

        ((MainActivity) context).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((MainActivity) context).getSupportActionBar().setHomeButtonEnabled(true);
        ((MainActivity) context).getSupportActionBar().setTitle(R.string.trash_bin);

        return TrashFragment;
    }

    public void onResume(){
        super.onResume();
        if(main != null && main.trashItems != null){
            trashItems = main.trashItems;
            trash_adapter.submitList(trashItems);
        }
    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ((MainActivity)context).getSupportActionBar().setTitle(R.string.app_name);
        ((MainActivity)context).getSupportActionBar().setDisplayHomeAsUpEnabled(false);

    }
    @Override
    public void onMsgFromMainToFragment(String strValue) {

    }

    @Override
    public void changeOnMultiChooseMode(){
        trash_adapter.changeOnMultiChooseMode();
    }

    public static class WrapContentGridLayoutManager extends GridLayoutManager {
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
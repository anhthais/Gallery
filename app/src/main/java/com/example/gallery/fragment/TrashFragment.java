package com.example.gallery.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gallery.FragmentCallBacks;
import com.example.gallery.MainActivity;
import com.example.gallery.R;
import com.example.gallery.adapter.TrashAdapter;
import com.example.gallery.object.TrashItem;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

public class TrashFragment extends Fragment implements FragmentCallBacks {
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
            trashItems = main.trashItems;
        } catch (IllegalStateException e) {
            throw new IllegalStateException("MainActivity must implement callbacks");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View TrashFragment = inflater.inflate(R.layout.trash_fragment, container, false);
        trash_adapter = new TrashAdapter(context, trashItems);
        trash_RecyclerView = TrashFragment.findViewById(R.id.trashFragmentRecyclerView);
        trash_RecyclerView.setAdapter(trash_adapter);
        trash_RecyclerView.setLayoutManager(new GridLayoutManager(context, 3));
        return TrashFragment;
    }

    @Override
    public void onMsgFromMainToFragment(String strValue) {

    }
}
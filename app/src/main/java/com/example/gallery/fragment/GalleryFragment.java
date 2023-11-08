package com.example.gallery.fragment;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gallery.MainActivity;
import com.example.gallery.R;
import com.example.gallery.adapter.ImageGroupAdapter;
import com.example.gallery.helper.LocalStorageReader;
import com.example.gallery.object.Image;
import com.example.gallery.object.ImageGroup;

import java.util.ArrayList;

public class GalleryFragment extends Fragment {
    private RecyclerView recyclerView;
    private ImageGroupAdapter imageGroupAdapter;
    private ArrayList<ImageGroup> groupList;
    private Context context;
    private MainActivity main;


    public static GalleryFragment getInstance(){
        return new GalleryFragment();
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.gallery_fragment, container, false);
        recyclerView = view.findViewById(R.id.recycleImages);

        groupList = getListImageGroup();
        imageGroupAdapter = new ImageGroupAdapter(context, groupList);

        recyclerView.setAdapter(imageGroupAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));


        return view;
    }

    private ArrayList<ImageGroup> getListImageGroup() {
        ArrayList<ImageGroup> groupList = new ArrayList<>();
        int count = 0;
        ArrayList<Image> imageList = LocalStorageReader.getImagesFromLocal(getContext());

        try {
            // group images by taken date, imageList contains images ordered by date DESC
            groupList.add(new ImageGroup(imageList.get(0).getDate(), new ArrayList<>()));

            groupList.get(count).addImg(imageList.get(0));

            for (int i = 1; i < imageList.size(); ++i) {
                if (!imageList.get(i).getDate().equals(imageList.get(i - 1).getDate())) {
                    groupList.add(new ImageGroup(imageList.get(i).getDate(), new ArrayList<>()));
                    count++;
                }
                groupList.get(count).addImg(imageList.get(i));
            }

            return groupList;

        } catch (Exception e) {
            Log.e("getListImageGroup", e.toString());
            return null;
        }

    }
    public void changeOnMultiChooseMode()
    {
        imageGroupAdapter.changeOnMultiChooseMode();
    }

}
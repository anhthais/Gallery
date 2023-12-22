package com.example.gallery.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gallery.MainActivity;
import com.example.gallery.R;
import com.example.gallery.adapter.ImageAdapter;
import com.example.gallery.object.Image;

import java.util.ArrayList;

public class SearchingFragment extends Fragment{
    private ImageAdapter adapter;
    private Context context;
    private RecyclerView recyclerView;
    private ArrayList<Image> img_list;
    private TextView textViewNoResults;
    private MainActivity main;
    public static SearchingFragment getInstance(){
        return new SearchingFragment();
    }
    public SearchingFragment(){

    }
    public SearchingFragment(Context context, ArrayList<Image> imgs){
        this.context = context;
        this.img_list = imgs;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        main = (MainActivity) getActivity();
        context = getActivity();

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.searching_fragment,container,false);
        recyclerView = view.findViewById(R.id.searchFragmentRecyclerView);
        textViewNoResults = view.findViewById(R.id.no_result);
        recyclerView.setLayoutManager(new GridLayoutManager(context,3));
        if (!((MainActivity) context).getMenu().findItem(R.id.Search).isVisible()) {
            ((MainActivity) context).getMenu().findItem(R.id.Search).setVisible(true);
        }
        ((MainActivity)context).getMenu().findItem(R.id.btnFind).setVisible(false);
        ((MainActivity)context).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((MainActivity)context).getSupportActionBar().setHomeButtonEnabled(true);
        ((MainActivity)context).getSupportActionBar().setTitle("");
        return view;
    }
    @Override
    public void onResume(){
        super.onResume();
        img_list = main.search_result_list;
        // Neu chua search ( Search bar empty ) thi se ko show chu " no result "
        if (img_list.isEmpty() && !main.isSearchBarEmpty  ) {
            textViewNoResults.setVisibility(View.VISIBLE);
        } else {
            textViewNoResults.setVisibility(View.GONE);
        }
        adapter = new ImageAdapter(context, img_list);
        recyclerView.setAdapter(adapter);
    }

    public void updateData()
    {
        if (main != null){
            img_list = main.search_result_list;
        }
        else {
            return;
        }
        adapter = new ImageAdapter(context, img_list);
        if (img_list.isEmpty() && !main.isSearchBarEmpty  ) {
            textViewNoResults.setVisibility(View.VISIBLE);
        } else {
            textViewNoResults.setVisibility(View.GONE);
        }
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ((MainActivity)context).getSupportActionBar().setTitle("Gallery");
        ((MainActivity)context).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        ((MainActivity)context).getMenu().findItem(R.id.btnChooseMulti).setVisible(true);
     //   ((MainActivity)context).getMenu().findItem(R.id.btnAddImageAcTion).setVisible(true);
      //  ((MainActivity)context).getMenu().findItem(R.id.btnStatistic).setVisible(true);
        ((MainActivity)context).getMenu().findItem(R.id.btnFind).setVisible(true);
        ((MainActivity)context).getMenu().findItem(R.id.Search).setVisible(false);
        main.search_result_list = new ArrayList<>() ;
    }
}


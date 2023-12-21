package com.example.gallery.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.gallery.R;
import com.example.gallery.helper.DownLoadImage;
import com.example.gallery.object.BackupImage;

import java.util.ArrayList;

public class BackupImageAdapter extends BaseAdapter {
    private ArrayList<BackupImage> dataList;
    private Context context;
    LayoutInflater layoutInflater;
    ImageButton download;

    public BackupImageAdapter(ArrayList<BackupImage> dataList, Context context) {
        this.dataList = dataList;
        this.context = context;
    }


    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        if(layoutInflater == null){
            layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        }
        if(view == null){
            view = layoutInflater.inflate(R.layout.grid_backup_item,null);
        }
        ImageView gridImage = view.findViewById(R.id.gridImage);
        TextView gridCaption = view.findViewById(R.id.gridCaption);
        download = view.findViewById(R.id.btn_download);
        Glide.with(context).load(dataList.get(position).getImageURL()).into(gridImage);
        gridCaption.setText(dataList.get(position).getCaption());



        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Start downloading .. ",Toast.LENGTH_SHORT).show();
                startDownloadFile(dataList.get(position).getImageURL());
            }
        });

        return view;
    }

    void startDownloadFile(String ImageUrl) {
        //Asynctask to create a thread to downlaod image in the background
        Toast.makeText(context, R.string.downloading, Toast.LENGTH_SHORT).show();
        try{
            new DownLoadImage(context).execute(ImageUrl);
        }catch (Exception e){
        }

    }
}

package com.example.gallery.fragment;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gallery.FragmentCallBacks;
import com.example.gallery.MainActivity;
import com.example.gallery.MainCallBackObjectData;
import com.example.gallery.R;
import com.example.gallery.adapter.ImageGroupAdapter;
import com.example.gallery.helper.LocalStorageReader;
import com.example.gallery.object.Image;
import com.example.gallery.object.ImageGroup;
import com.example.gallery.object.Statistic;
import com.example.gallery.object.TrashItem;
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

public class GalleryFragment extends Fragment implements FragmentCallBacks {
    private RecyclerView recyclerView;
    private ImageGroupAdapter imageGroupAdapter;
    private ArrayList<ImageGroup> groupList;
    private  ArrayList<Statistic> statisticList;
    private WatchService watchService;
    private Path imageFolderPath;
    private  ArrayList<String> listMediaFolderImage;

    private Context context;
    private MainActivity main;



    // TODO: adjust to singleton
    public static GalleryFragment getInstance(){
        return new GalleryFragment();
    }

    final Integer REQUEST_CODE = 1;
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
            groupList = main.imageGroupsByDate;
        } catch (IllegalStateException e) {
            throw new IllegalStateException("MainActivity must implement callbacks");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.gallery_fragment, container, false);
        recyclerView = view.findViewById(R.id.recycleImages);
        //groupList = getListImageGroup();
        imageGroupAdapter = new ImageGroupAdapter(context, groupList);

        recyclerView.setAdapter(imageGroupAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        listMediaFolderImage = findMediaStoreImageFolder(groupList);
        listenerUpdateMediaStore(listMediaFolderImage);
        // Tính dung lượng và số lượng của mỗi group list,
        statisticList = getStatisticGroupList(groupList);
        passObjectToActivity(statisticList);

        return view;
    }
    private ArrayList<String> findMediaStoreImageFolder(ArrayList<ImageGroup> groupList){
        ArrayList<String> result = new ArrayList<String>();
        for(int i=0;i<groupList.size();i++){
            for(int j=0;j<groupList.get(i).getList().size();j++){
                String path = groupList.get(i).getList().get(j).getPath();
                String pathWithoutFilename = path.substring(0, path.lastIndexOf("/"));
                if(result.isEmpty()){
                    result.add(pathWithoutFilename);
                }
                else if(!result.contains(pathWithoutFilename)){
                    result.add(pathWithoutFilename);
                }
            }
        }
        return result;

    }
    private void listenerUpdateMediaStore(ArrayList<String> listPaths){

        for(int i=0;i<listPaths.size();i++) {

            String path = listPaths.get(i);
            imageFolderPath = Paths.get(path);

            try {
                watchService = FileSystems.getDefault().newWatchService();
                imageFolderPath.register(watchService, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
            } catch (IOException e) {
                e.printStackTrace();
            }

            new Thread(() -> {
                while (true) {
                    WatchKey watchKey = null;
                    try {
                        watchKey = watchService.take();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    for (WatchEvent event : watchKey.pollEvents()) {
                        Path watchedPath = ((WatchEvent<Path>) event).context();
                        Path childPath = imageFolderPath.resolve(watchedPath);

                        WatchEvent.Kind kind = event.kind();
                        if (kind.equals(ENTRY_CREATE)) {
                            updateGroupListAndRecyclerView(childPath);
                        } else if (kind.equals(ENTRY_DELETE)) {
                            updateGroupListAndRecyclerView(childPath);
                        } else if (kind.equals(ENTRY_MODIFY)) {
                            updateGroupListAndRecyclerView(childPath);
                        }
                    }

                    if (!watchKey.reset()) {
                        break;
                    }
                }
            }).start();
        }
    }
    private void updateGroupListAndRecyclerView(Path childPath) {
        getActivity().runOnUiThread(() -> {
            groupList = getListImageGroup();
            imageGroupAdapter = new ImageGroupAdapter(context, groupList);
            recyclerView.setAdapter(imageGroupAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        });
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

    private ArrayList<ImageGroup> getListImageGroup() {
        ArrayList<ImageGroup> groupList = new ArrayList<>();
        int count = 0;
        ArrayList<Image> imageList = LocalStorageReader.getImagesFromLocal(getContext());
        //lấy ảnh từ thùng rác tránh hiển thị ở galery fragment
        Gson gson=new Gson();
        SharedPreferences albumPref= context.getSharedPreferences("GALLERY", Context.MODE_PRIVATE);
        String allTrash=albumPref.getString("TRASH","");
        ArrayList<String>allTrashPath=gson.fromJson(allTrash,new TypeToken<ArrayList<String>>(){}.getType());
        ArrayList<TrashItem>trash_list=new ArrayList<>();
        if(allTrashPath!=null){
//            for(int i=0;i<allTrashPath.size();i++){
//                trash_list.add(new TrashItem(allTrashPath.get(i)));
//            }
        }
        for(int i=0;i<trash_list.size();i++){
            for(int j=0;j<imageList.size();j++)
            {
                if(imageList.get(j).getPath().equals(trash_list.get(i).getPath())){
                    imageList.remove(j);
                    break;
                }
            }
        }
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
            return new ArrayList<>();
        }

    }

    //xoá 1 ảnh gallery fragment---> image adapter--->image group
    public void deleteImage(String path){
        imageGroupAdapter.deleteImage(path);
    }
    public void changeOnMultiChooseMode()
    {
        imageGroupAdapter.changeOnMultiChooseMode();
    }

    public Image findImageByPath(String path){
        File file = new File(path);
        if(file.exists()){
            for(int i=0;i<groupList.size();i++){
                Image image=groupList.get(i).findImageByPath(path);
                if(image!=null){
                    return image;
                }
            }
        }
        return null;
    }

    @Override
    public void onMsgFromMainToFragment(String strValue) {
        groupList = main.imageGroupsByDate;
        getActivity().getSupportFragmentManager().beginTransaction().detach(this).attach(this).commit();
    }
}
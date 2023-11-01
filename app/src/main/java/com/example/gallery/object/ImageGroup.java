package com.example.gallery.object;

import java.util.ArrayList;

public class ImageGroup {
    private String id;
    private ArrayList<Image> imgList;

    public ImageGroup(String id, ArrayList<Image> imgs){
        this.id = id;
        this.imgList = imgs;
    }

    public String getId(){ return this.id; }
    public void setId(String id){ this.id = id; }

    public ArrayList<Image> getList(){ return this.imgList; }
    public void setList(ArrayList<Image> list){ this.imgList = list; }
    public void addImg(Image img){
        imgList.add(img);
    }
}
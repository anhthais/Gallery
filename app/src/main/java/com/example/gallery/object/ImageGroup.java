package com.example.gallery.object;

import java.util.ArrayList;

// TODO: change id to groupName (for correct meaning)
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
    //delete Image by path
    public boolean deleteImage(String path){
        for(int i=0;i<imgList.size();i++){
            if(imgList.get(i).getPath().equals(path)){
                imgList.remove(i);
                return true;
            }
        }
        return false;
    }
    //find image by path
    public Image findImageByPath(String path){
        for(int i=0;i<imgList.size();i++){
            if(imgList.get(i).getPath().equals(path))
                return imgList.get(i);
        }
        return null;
    }
}

package com.example.gallery.object;

import java.util.ArrayList;

public class Album {
    private String name;
    private ArrayList<Image> all_album_pictures;
    public Album(String name){
        this.name=name;
        all_album_pictures=new ArrayList<Image>();
    }
    public String getName(){
        return name;
    }

    public ArrayList<Image> getAll_album_pictures() {
        return all_album_pictures;
    }

    public void loadAllImage(){

    }
    //remove an item from Album list (set Album=null in database)
    public void deleteImageFromAlbum(String path){
        for(int i=0;i<all_album_pictures.size();i++){
            if(all_album_pictures.get(i).getPath().equals(path)){
                all_album_pictures.remove(i);
                return;
            }
        }
    }
    //add an image to database (set Album=Album name)
    public void addImageToAlbum(Image image){
        for(int i = 0; i < all_album_pictures.size(); i++){
            if(all_album_pictures.get(i).getPath().equals(image.getPath()))
                return;
        }
        all_album_pictures.add(image);
    }
    public void removeImageFromAlbum(String path){
        for(int i=0;i<all_album_pictures.size();i++){
            if(all_album_pictures.get(i).getPath().equals(path)){
                all_album_pictures.remove(i);
                return;
            }
        }
    }
    public void Rename(String name){
        this.name=name;
        //notify();
    }
}

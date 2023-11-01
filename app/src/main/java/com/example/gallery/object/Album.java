package com.example.gallery.object;

import java.util.ArrayList;

public class Album {
    private String name;
    private ArrayList<String> all_album_pictures;
    public Album(String name){
        this.name=name;
    }
    public String getName(){
        return name;
    }

    public ArrayList<String> getAll_album_pictures() {
        return all_album_pictures;
    }

    public void loadAllImage(){

    }
    //remove an item from Album list (set Album=null in database)
    public void deleteImageFromAlbum(){

    }
    //add an image to database (set Album=Album name)
    public void addImageToAlbum(){

    }
}

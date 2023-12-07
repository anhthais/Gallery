package com.example.gallery.object;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.ArrayList;

public class Album implements Parcelable {
    private String name;
    private String path;
    private String thumbnail;
    private ArrayList<Image> all_album_pictures;
    public Album(String name){
        this.name = name;
        this.all_album_pictures = new ArrayList<>();
    }
    public Album(String name, String path){
        this.name=name;
        this.path=path;
        all_album_pictures=new ArrayList<Image>();
    }

    protected Album(Parcel in) {
        all_album_pictures=new ArrayList<>();
        name = in.readString();
        path = in.readString();
        thumbnail = in.readString();
    }

    public static final Creator<Album> CREATOR = new Creator<Album>() {
        @Override
        public Album createFromParcel(Parcel in) {
            return new Album(in);
        }

        @Override
        public Album[] newArray(int size) {
            return new Album[size];
        }
    };


    public String getThumbnail(){
        return this.thumbnail;
    }

    public String getPath(){return this.path;}
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
        for(int i=0;i<all_album_pictures.size();i++){
            if(all_album_pictures.get(i).getPath().equals(image.getPath()))
                return;
        }
        all_album_pictures.add(image);
    }
    public boolean removeImageFromAlbum(String path){
        for(int i=0;i<all_album_pictures.size();i++){
            if(all_album_pictures.get(i).getPath().equals(path)){
                all_album_pictures.remove(i);
                return true;
            }
        }
        return false;
    }
    public void Rename(String name){
        this.name=name;
        //notify();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        if(all_album_pictures.size()>0){
            this.thumbnail=all_album_pictures.get(0).getPath();
        } else{
            this.thumbnail=null;
        }
        dest.writeString(this.name);
        dest.writeString(this.path);
        dest.writeString(this.thumbnail);
    }
}

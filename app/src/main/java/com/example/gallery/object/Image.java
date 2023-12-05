package com.example.gallery.object;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

public class Image implements Parcelable {
    private long idInMediaStore;
    private String path;
    private String dateAdded;
    private boolean isFavorite;
    private String tags;

    public Image() {
    }
    public Image(String path, String dateAdded, long idInMediaStore) {
        this.path = path;
        this.dateAdded = dateAdded;
        this.idInMediaStore = idInMediaStore;
        Log.e("E",""+path);
    }
    public Image(String path, String dateAdded) {
        this.path = path;
        this.dateAdded = dateAdded;
    }
    public Image(String path) {
        this.path = path;
    }

    protected Image(Parcel in) {
        idInMediaStore = in.readLong();
        path = in.readString();
        dateAdded = in.readString();
        isFavorite = in.readByte() != 0;
        tags = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(idInMediaStore);
        dest.writeString(path);
        dest.writeString(dateAdded);
        dest.writeByte((byte) (isFavorite ? 1 : 0));
        dest.writeString(tags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Image> CREATOR = new Creator<Image>() {
        @Override
        public Image createFromParcel(Parcel in) {
            return new Image(in);
        }

        @Override
        public Image[] newArray(int size) {
            return new Image[size];
        }
    };

    public long getIdInMediaStore(){
        return this.idInMediaStore;
    }
    public void setIdInMediaStore(long id){
        this.idInMediaStore = id;
    }

    public String getPath() {
        return path;
    }
    public void setPath(String path) {
        this.path = path;
    }

    public String getDate() {
        return dateAdded;
    }
    public void setDate(String date) {
        this.dateAdded = date;
    }

    public boolean isFavorite(){
        return this.isFavorite;
    }
    public void setFavorite(boolean isFavorite){
        this.isFavorite = isFavorite;
    }

    public String getTags() {
        return tags;
    }
    public void setTags(String tags) {
        this.tags = tags;
    }
}

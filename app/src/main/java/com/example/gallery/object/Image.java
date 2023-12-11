package com.example.gallery.object;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

public class Image implements Parcelable {
    private long idInMediaStore;
    private String path;
    private String dateAdded;
    private boolean isFavorite;
    private String tags;
    private LatLng location;
    private Double latitude;
    private Double longitude;
    private Boolean haveLocation ;

    public Image() {
    }
    public Image(String path, String dateAdded, long idInMediaStore) {
        this.path = path;
        this.dateAdded = dateAdded;
        this.idInMediaStore = idInMediaStore;
        this.location= null;
        this.haveLocation = false;
    }
    public Image(String path, String dateAdded) {
        this.path = path;
        this.dateAdded = dateAdded;
        this.location= null;
        this.haveLocation = false;
    }
    public Image(String path) {
        this.path = path;
        this.location= null;
        this.haveLocation = false;
    }

    protected Image(Parcel in) {
        idInMediaStore = in.readLong();
        path = in.readString();
        dateAdded = in.readString();
        isFavorite = in.readByte() != 0;
        tags = in.readString();
        haveLocation = in.readByte()!=0;
        if (haveLocation)
        {
            latitude = in.readDouble();
            longitude = in.readDouble();
            location = new LatLng(latitude,longitude);
        }

    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(idInMediaStore);
        dest.writeString(path);
        dest.writeString(dateAdded);
        dest.writeByte((byte) (isFavorite ? 1 : 0));
        dest.writeString(tags);
        dest.writeByte((byte) (haveLocation ? 1 : 0));
        if (haveLocation==true)
        {
            dest.writeDouble(latitude);
            dest.writeDouble(longitude);
        }

        // dest.writeString(String.valueOf(location));
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

    public LatLng getLocation() {
        return location;
    }

    public void setLocation(LatLng location) {
        if (location ==null)
        {
            this.location = null;
            this.latitude = null;
            this.longitude = null;
            this.haveLocation = false;
        }
        else
        {
            this.location = location;
            this.latitude = location.latitude;
            this.longitude = location.longitude;
            this.haveLocation = true;
        }

    }
}

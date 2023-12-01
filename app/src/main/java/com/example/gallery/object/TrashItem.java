package com.example.gallery.object;


import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class TrashItem implements Parcelable {
    private String path;
    private String prevPath;
    private Long dateExpires;

    public TrashItem(String path, String prevPath, long dateExpires){
        this.path = path;
        this.prevPath = prevPath;
        this.dateExpires = dateExpires;
    }

    protected TrashItem(Parcel in) {
        path = in.readString();
        prevPath = in.readString();
        if (in.readByte() == 0) {
            dateExpires = null;
        } else {
            dateExpires = in.readLong();
        }
    }

    public static final Creator<TrashItem> CREATOR = new Creator<TrashItem>() {
        @Override
        public TrashItem createFromParcel(Parcel in) {
            return new TrashItem(in);
        }

        @Override
        public TrashItem[] newArray(int size) {
            return new TrashItem[size];
        }
    };

    public String getPath() {
        return path;
    }
    public void setPath(String path) {
        this.path = path;
    }
    public String getPrevPath() {
        return prevPath;
    }
    public void setPrevPath(String prevPath) {
        this.prevPath = prevPath;
    }
    public long getDateExpires() {
        return dateExpires;
    }
    public void setDateExpires(long dateExpires) {
        this.dateExpires = dateExpires;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(path);
        dest.writeString(prevPath);
        if (dateExpires == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(dateExpires);
        }
    }
}
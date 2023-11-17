package com.example.gallery.object;

import android.os.Parcel;
import android.os.Parcelable;

public class Image implements Parcelable {
    private String path;
    private String takenDate;

    public Image() {
    }
    public Image(String path) {
        this.path=path;
    }
    public Image(String path, String takenDate) {
        this.path = path;
        this.takenDate = takenDate;
    }

    protected Image(Parcel in) {
        path = in.readString();
        takenDate = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(path);
        dest.writeString(takenDate);
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

    public String getPath() {
        return path;
    }
    public void setPath(String path) {
        this.path = path;
    }

    public String getDate() {
        return takenDate;
    }
    public void setDate(String date) {
        this.takenDate = date;
    }

}

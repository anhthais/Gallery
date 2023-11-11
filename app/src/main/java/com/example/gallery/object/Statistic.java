package com.example.gallery.object;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;


// Class holder info static of each date ( id - date, count - number of image in date, weight -size in disk )
public class Statistic implements Parcelable {
    String id;
    Integer count;
    String weight;

    public Statistic(){
        this.id = "";
        this.count = 0;
        this.weight = "";
    }
    public Statistic(String id, Integer count, String weight){
        this.id = id;
        this.count = count;
        this.weight = weight;
    }

    protected Statistic(Parcel in) {
        id = in.readString();
        if (in.readByte() == 0) {
            count = null;
        } else {
            count = in.readInt();
        }
        weight = in.readString();
    }

    public static final Creator<Statistic> CREATOR = new Creator<Statistic>() {
        @Override
        public Statistic createFromParcel(Parcel in) {
            return new Statistic(in);
        }

        @Override
        public Statistic[] newArray(int size) {
            return new Statistic[size];
        }
    };

    public String getId(){ return this.id;}
    public Integer getCount(){return this.count;}
    public String getWeight(){return weight;}


    public void setId(String id){ this.id = id ;}
    public void setCount(Integer count){this.count = count;}
    public void setWeight(String weight){this.weight = weight;}


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeInt(count);
        parcel.writeString(weight);
    }
}

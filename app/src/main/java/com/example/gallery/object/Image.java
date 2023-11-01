package com.example.gallery.object;

public class Image {
    private String path;
    private String takenDate;

    public Image() {
    }
    public Image(String path, String takenDate) {
        this.path = path;
        this.takenDate = takenDate;
    }

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

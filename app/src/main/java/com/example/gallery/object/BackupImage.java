package com.example.gallery.object;

public class BackupImage {
    private String imageURL,caption;

    public BackupImage(){};
    public BackupImage(String imageURL, String caption) {
        this.imageURL = imageURL;
        this.caption = caption;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }
}

package com.example.gallery.object;




public class TrashItem {
    private String path;
    private String timeRemaining;

    public TrashItem(){

    }
    public TrashItem(String path , String timeRemaining){
        this.timeRemaining=timeRemaining;
        this.path=path;
    }
    public TrashItem(String path){
        this.timeRemaining="30 Days";
        this.path=path;
    }
    public String getPath() {
        return this.path;
    }
    public String getTimeRemaining(){
        return this.timeRemaining;
    }
}
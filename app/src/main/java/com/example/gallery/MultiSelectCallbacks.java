package com.example.gallery;

public interface MultiSelectCallbacks {
    public void setMultiSelect(int childPos, int parentPos);
    public void onItemClick(int childPos, int parentPos);
    public boolean isSelectedItem(int childPos, int parentPos);
}

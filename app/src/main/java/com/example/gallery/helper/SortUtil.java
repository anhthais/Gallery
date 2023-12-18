package com.example.gallery.helper;

import com.example.gallery.object.Image;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class SortUtil {
    public static int CriterionDateAdded = 0;
    public static int CriterionName = 1;
    public static int CriterionFileSize = 2;
    public static int TypeASC = 0;
    public static int TypeDESC = 1;

    public static ArrayList<Image> sort(ArrayList<Image> images, int criterion, int type) {
        Comparator<Image> sortComparator = null;
        if (criterion == CriterionDateAdded) {
            sortComparator = new SortByAddedDateComparator();
        } else if (criterion == CriterionName) {
            sortComparator = new SortByNameComparator();
        } else if (criterion == CriterionFileSize) {
            sortComparator = new SortByFileSizeComparator();
        }
        if(type == TypeDESC){
            sortComparator = Collections.reverseOrder(sortComparator);
        }
        if(sortComparator != null){
            Collections.sort(images, sortComparator);
        }
        return images;
    }

    public static class SortByAddedDateComparator implements Comparator<Image> {
        @Override
        public int compare(Image a, Image b)
        {
            return Long.compare(a.getDate(), b.getDate());
        }
    }

    public static class SortByNameComparator implements Comparator<Image> {
        @Override
        public int compare(Image a, Image b)
        {
            File fileA = new File(a.getPath());
            File fileB = new File(b.getPath());
            return fileA.getName().compareTo(fileB.getName());
        }
    }

    public static class SortByFileSizeComparator implements Comparator<Image> {
        @Override
        public int compare(Image a, Image b)
        {
            File fileA = new File(a.getPath());
            File fileB = new File(b.getPath());
            return Long.compare(fileA.length(), fileB.length());
        }
    }
}




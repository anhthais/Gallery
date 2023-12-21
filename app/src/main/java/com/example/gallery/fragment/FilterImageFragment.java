package com.example.gallery.fragment;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toolbar;

import com.example.gallery.MainCallBacks;
import com.example.gallery.R;

import java.io.ByteArrayOutputStream;


public class FilterImageFragment extends Fragment {
    Context context;
    Bitmap currBitmap;
    ImageView imageView;
    ImageView filter1;
    ImageView filter2;
    ImageView filter3;
    ImageView filter4;
    ImageView filter5;
    ImageView filter6;
    ImageView filter7;
    ImageView filter8;
    ImageView filter9;
    ImageView filter10;
    ImageButton filterReturn;
    ImageButton saveFilter;
    private MainCallBacks callback;

    public FilterImageFragment(Context context, Bitmap currBitmap) {
        this.context = context;
        this.currBitmap = currBitmap;

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof MainCallBacks) {
            callback = (MainCallBacks) context;
        } else {
            throw new ClassCastException(context.toString() + " must implement MainCallBack");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_filter_image, container, false);
        filterReturn = view.findViewById(R.id.filterReturn);
        saveFilter = view.findViewById(R.id.saveFilter);
        imageView = view.findViewById(R.id.image);
        filter1 = view.findViewById(R.id.filter1);
        filter2 = view.findViewById(R.id.filter2);
        filter3 = view.findViewById(R.id.filter3);
        filter4 = view.findViewById(R.id.filter4);
        filter5 = view.findViewById(R.id.filter5);
        filter6 = view.findViewById(R.id.filter6);
        filter7 = view.findViewById(R.id.filter7);
        filter8 = view.findViewById(R.id.filter8);
        filter9 = view.findViewById(R.id.filter9);
        filter10 = view.findViewById(R.id.filter10);

        imageView.setImageURI(null);
        imageView.setImageBitmap(currBitmap);

        filter1.setImageBitmap(currBitmap);
        filter2.setImageBitmap(currBitmap);
        filter3.setImageBitmap(currBitmap);
        filter4.setImageBitmap(currBitmap);
        filter5.setImageBitmap(currBitmap);

        filterColor("GREY",filter2);
        filterColor("RED",filter3);
        filterColor("GREEN",filter4);
        filterColor("BLUE",filter5);
        filterColor("RED-GREEN",filter6);
        filterColor("RED-BLUE",filter7);
        filterColor("GREEN-BLUE",filter8);
        filterColor("SEPIA",filter9);
        filterColor("BINARY",filter10);
        filters();

        return view;
    }

    private void filters() {
        filter1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageView.setImageBitmap(currBitmap);
            }
        });
        filter2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //filter photo to grey scale
                filterColor("GREY",imageView);
            }
        });
        filter3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //filter photo to grey scale
                filterColor("RED",imageView);
            }
        });
        filter4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //filter photo to grey scale
                filterColor("GREEN",imageView);
            }
        });
        filter5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //filter photo to grey scale
                filterColor("BLUE",imageView);
            }
        });
        filter6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //filter photo to grey scale
                filterColor("RED-GREEN",imageView);
            }
        });

        filter7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //filter photo to grey scale
                filterColor("RED-BLUE",imageView);
            }
        });
        filter8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //filter photo to grey scale
                filterColor("GREEN-BLUE",imageView);
            }
        });
        filter9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //filter photo to grey scale
                filterColor("SEPIA",imageView);
            }
        });

        filter10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //filter photo to grey scale
                filterColor("BINARY",imageView);
            }
        });
        filterReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (callback != null) {
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    currBitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                    byte[] byteArray = byteArrayOutputStream .toByteArray();
                    String encodedBitmap = Base64.encodeToString(byteArray, Base64.DEFAULT);

                    callback.onMsgFromFragToMain("FILTER-RETURN",encodedBitmap);
                }
            }
        });
        saveFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (callback != null) {
                    currBitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    currBitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                    byte[] byteArray = byteArrayOutputStream .toByteArray();
                    String encodedBitmap = Base64.encodeToString(byteArray, Base64.DEFAULT);

                    callback.onMsgFromFragToMain("FILTER-RETURN",encodedBitmap);
                }
            }
        });
    }

    private void filterColor(String color,ImageView imv) {
        Bitmap outputBitmap = Bitmap.createScaledBitmap(currBitmap,currBitmap.getWidth(),currBitmap.getHeight(),false).copy(Bitmap.Config.ARGB_8888,true);
        //define a paint for styling and coloring bitmaps

        Paint paint = new Paint();

        Canvas canvas = new Canvas(outputBitmap);
        if(color.equals("GREY")){
            //color maxtrix to filter to grey
            ColorMatrix colorMatrix = new ColorMatrix();
            colorMatrix.setSaturation(0);
            ColorMatrixColorFilter colorFilter = new ColorMatrixColorFilter(colorMatrix);
            paint.setColorFilter(colorFilter);

            canvas.drawBitmap(outputBitmap,0,0,paint);

        }
        if(color.equals("RED")){
            final int mul = 0XFF0000; //max red
            final int add = 0X000000; // constant at 0
            ColorFilter colorFilter = new LightingColorFilter(mul,add);
            paint.setColorFilter(colorFilter);
            canvas.drawBitmap(outputBitmap,0,0,paint);
        }
        if(color.equals("GREEN")){
            final int mul = 0X00FF00; //max green
            final int add = 0X000000; // constant at 0
            ColorFilter colorFilter = new LightingColorFilter(mul,add);
            paint.setColorFilter(colorFilter);
            canvas.drawBitmap(outputBitmap,0,0,paint);
        }
        if(color.equals("BLUE")){
            final int mul = 0X0000FF; //max blue
            final int add = 0X000000; // constant at 0
            ColorFilter colorFilter = new LightingColorFilter(mul,add);
            paint.setColorFilter(colorFilter);
            canvas.drawBitmap(outputBitmap,0,0,paint);
        }
        if(color.equals("RED-GREEN")){
            final int mul = 0XFFFF00; //max green, red
            final int add = 0X000000; // constant at 0
            ColorFilter colorFilter = new LightingColorFilter(mul,add);
            paint.setColorFilter(colorFilter);
            canvas.drawBitmap(outputBitmap,0,0,paint);
        }
        if(color.equals("RED-BLUE")){
            final int mul = 0XFF00FF; //max  red, blue
            final int add = 0X000000; // constant at 0
            ColorFilter colorFilter = new LightingColorFilter(mul,add);
            paint.setColorFilter(colorFilter);
            canvas.drawBitmap(outputBitmap,0,0,paint);
        }
        if(color.equals("GREEN-BLUE")){
            final int mul = 0X00FFFF; //max  red, blue
            final int add = 0X000000; // constant at 0
            ColorFilter colorFilter = new LightingColorFilter(mul,add);
            paint.setColorFilter(colorFilter);
            canvas.drawBitmap(outputBitmap,0,0,paint);
        }
        if(color.equals("SEPIA")){
            ColorMatrix colorMatrix= new ColorMatrix();
            colorMatrix.setSaturation(0);
            ColorMatrix colorScale = new ColorMatrix();
            colorScale.setScale(1,1,0.8f,1);

            colorMatrix.postConcat(colorScale);
            ColorMatrixColorFilter colorFilter = new ColorMatrixColorFilter(colorMatrix);
            paint.setColorFilter(colorFilter);
            canvas.drawBitmap(outputBitmap,0,0,paint);
        }
        if(color.equals("BINARY")){
            ColorMatrix colorMatrix= new ColorMatrix();
            colorMatrix.setSaturation(0);
            float m = 255f;
            float t = -255*128f;
            ColorMatrix threshold = new ColorMatrix(new float[]{
                    m,0,0,1,t,
                    0,m,0,1,t,
                    0,0,m,1,t,
                    0,0,0,1,0
            });
            colorMatrix.postConcat(threshold);
            ColorMatrixColorFilter colorFilter = new ColorMatrixColorFilter(colorMatrix);
            paint.setColorFilter(colorFilter);
            canvas.drawBitmap(outputBitmap,0,0,paint);
        }
        imv.setImageBitmap(outputBitmap);
    }

}

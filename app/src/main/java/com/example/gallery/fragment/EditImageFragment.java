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
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;

import com.example.gallery.MainCallBacks;
import com.example.gallery.R;
import com.example.gallery.object.Tool;
import com.google.android.material.bottomnavigation.BottomNavigationView;


public class EditImageFragment extends Fragment {
    Uri uri;
    Context context;
    ImageView imageView;

    private Toolbar topBar;
    private BottomNavigationView btnv;

    private MainCallBacks callback;

    private Integer curPos;

    private SeekBar seekBarBrightness;
    private SeekBar seekBarConstract;

    private  Bitmap currenBitmap;

    public EditImageFragment(Context context,String imageUri, Integer curPos) {
        this.context = context;
        uri = Uri.parse(imageUri);
        this.curPos = curPos;
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
        View view = inflater.inflate(R.layout.fragment_edit_image, container, false);

        imageView = view.findViewById(R.id.imageViewEditPicture);
        btnv = view.findViewById(R.id.navigationBarEditPicture);
        topBar = view.findViewById(R.id.topAppBar);
        seekBarBrightness = view.findViewById(R.id.seekBarBrightness);
        seekBarConstract = view.findViewById(R.id.seekBarConstract);

        imageView.setImageURI(null);
        imageView.setImageURI(uri);

        currenBitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();

        btnv.setOnNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if(id == R.id.btnCutPicture){
                if (callback != null) {
                    callback.onMsgFromFragToMain("CUT-ROTATE",curPos.toString());
                }
            }
            else if (id == R.id.btnLightUp){
                btnv.setVisibility(View.INVISIBLE);
                seekBarBrightness.setVisibility(view.VISIBLE);


            }
            else if (id == R.id.btnConstract){
                btnv.setVisibility(View.INVISIBLE);
                seekBarConstract.setVisibility(view.VISIBLE);
            }

            else if (id == R.id.btnFilter){

            }
            else if (id == R.id.btnDraw){

            }


            return true;
        });

        // handle topBar
        topBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (callback != null) {
                    callback.onMsgFromFragToMain("RETURN-IMAGE-VIEW",curPos.toString());
                }
            }
        });

        seekBarBrightness.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                float brightness = (float) progress * 255 / seekBarBrightness.getMax();
                adjustBrightness(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        seekBarConstract.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                adjustConstract(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        return view;
    }

    private void adjustConstract(int value) {
        // Lấy bitmap từ ImageView
        Bitmap bitmap = currenBitmap;

        // mul values
        String initialHex =Tool.hexScale()[value];
        String initialMul ="0X" + initialHex +initialHex+initialHex;
        int mul = Integer.decode(initialMul);
        int add = 0X000000;

        // output bitmap from above bimap
        Bitmap outputBitmap = Bitmap.createScaledBitmap(bitmap,bitmap.getWidth(),bitmap.getHeight(),false).copy(Bitmap.Config.ARGB_8888,true);
        // paint
        Paint paint = new Paint();
        ColorFilter colorFilter = new LightingColorFilter(mul,add);
        paint.setColorFilter(colorFilter);
        // canvas
        Canvas canvas = new Canvas(outputBitmap);
        canvas.drawBitmap(outputBitmap,0,0,paint);

        // Set the output Bitmap to ImageView
        imageView.setImageBitmap(outputBitmap);

    }

    private void adjustBrightness(int value) {
        // Lấy bitmap từ ImageView
        Bitmap bitmap = currenBitmap;

        // define a mul
        final int mul = 0XFFFFFF;

        // define an add
        String initialHex = Tool.hexScale()[value];
        String initialAdd = "0X"+initialHex +initialHex+initialHex;
        int add = Integer.decode(initialAdd);

        //generate bitmap from above bitmap
        Bitmap outputBitmap = Bitmap.createScaledBitmap(bitmap,bitmap.getWidth(),bitmap.getHeight(),false).copy(Bitmap.Config.ARGB_8888,true);

        //paint
        Paint paint = new Paint();
        ColorFilter colorFilter = new LightingColorFilter(mul,add);
        paint.setColorFilter(colorFilter);

        // canvas
        Canvas canvas =  new Canvas(outputBitmap);
        canvas.drawBitmap(outputBitmap,0,0,paint);

        // Set the output Bitmap to ImageView
        imageView.setImageBitmap(outputBitmap);
    }

    private void toggleSeekBarVisibility() {
        // Toggle visibility of the SeekBar
        int visibility = seekBarBrightness.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE;
        seekBarBrightness.setVisibility(visibility);
    }




}
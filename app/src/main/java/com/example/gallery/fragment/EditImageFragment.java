package com.example.gallery.fragment;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Toast;

import com.example.gallery.FragmentCallBacks;
import com.example.gallery.MainCallBacks;
import com.example.gallery.R;
import com.example.gallery.object.Tool;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.ByteArrayOutputStream;


public class EditImageFragment extends Fragment {
    Bitmap uCropEditedImage;
    Context context;
    ImageView imageView;


    private BottomNavigationView btnv;

    private MainCallBacks callback;

    private Integer curPos;

    private SeekBar seekBarBrightness;
    private SeekBar seekBarConstract;

    LinearLayout editBrightnessBottomNav;
    LinearLayout editConstractBottomNav;
    Toolbar editTopBar;
    Toolbar topBar;
    ImageButton img_btn_save;
    ImageButton img_btn_saveEdit;
    private  Bitmap currenBitmap;

    private  Bitmap editFinal;

    public EditImageFragment(Context context,String encodedBitmap, Integer curPos) {
        this.context = context;
        byte[] decodedString = Base64.decode(encodedBitmap, Base64.DEFAULT);
        uCropEditedImage = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
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
        seekBarBrightness = view.findViewById(R.id.seekBarBrightness);
        seekBarConstract = view.findViewById(R.id.seekBarConstract);

        editTopBar = view.findViewById(R.id.topEditBar);
        topBar = view.findViewById(R.id.topAppBar);

        editBrightnessBottomNav = view.findViewById(R.id.layout_edit_brightness);
        editConstractBottomNav = view.findViewById(R.id.layout_edit_constract);

        img_btn_save = view.findViewById(R.id.check);
        img_btn_save.setClickable(false);

        img_btn_saveEdit = view.findViewById(R.id.checkEdit);

        imageView.setImageURI(null);
        imageView.setImageBitmap(uCropEditedImage);

        currenBitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();

        btnv.setOnNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if(id == R.id.btnCutPicture){
                if (callback != null) {
                    currenBitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    currenBitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                    byte[] byteArray = byteArrayOutputStream .toByteArray();
                    String encodedBitmap = Base64.encodeToString(byteArray, Base64.DEFAULT);

                    callback.onMsgFromFragToMain("CUT-ROTATE",encodedBitmap);
                }
            }
            else if (id == R.id.btnLightUp){
                btnv.setVisibility(View.INVISIBLE);
                editBrightnessBottomNav.setVisibility(View.VISIBLE);
                img_btn_save.setClickable(true);
                topBar.setVisibility(View.INVISIBLE);
                editTopBar.setVisibility(View.VISIBLE);

            }
            else if (id == R.id.btnConstract){
                btnv.setVisibility(View.INVISIBLE);
                editConstractBottomNav.setVisibility(View.VISIBLE);

                img_btn_save.setClickable(true);

                topBar.setVisibility(View.INVISIBLE);
                editTopBar.setVisibility(View.VISIBLE);


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
        img_btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context,R.string.save_imahe_edit,Toast.LENGTH_SHORT).show();
                currenBitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                currenBitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                byte[] byteArray = byteArrayOutputStream .toByteArray();
                String encodedBitmap = Base64.encodeToString(byteArray, Base64.DEFAULT);

                if (callback != null) {
                    callback.onMsgFromFragToMain("SAVE-EDITED-IMAGE",encodedBitmap);
                }

            }
        });

        img_btn_saveEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editBrightnessBottomNav.setVisibility(View.INVISIBLE);
                editConstractBottomNav.setVisibility(View.INVISIBLE);
                editTopBar.setVisibility(View.INVISIBLE);
                btnv.setVisibility(View.VISIBLE);
                topBar.setVisibility(View.VISIBLE);
                imageView.setImageBitmap(editFinal);
            }
        });
        editTopBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnv.setVisibility(View.VISIBLE);
                editBrightnessBottomNav.setVisibility(View.INVISIBLE);
                editConstractBottomNav.setVisibility(View.INVISIBLE);
                topBar.setVisibility(View.VISIBLE);
                editTopBar.setVisibility(View.INVISIBLE);
                imageView.setImageBitmap(currenBitmap);
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
        editFinal = outputBitmap;

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
        editFinal = outputBitmap;
    }

    private void toggleSeekBarVisibility() {
        // Toggle visibility of the SeekBar
        int visibility = seekBarBrightness.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE;
        seekBarBrightness.setVisibility(visibility);
    }



    public void onMsgFromMainToFragment(String strValue) {
        byte [] decodedString = Base64.decode(strValue,Base64.DEFAULT);
        editFinal = BitmapFactory.decodeByteArray(decodedString,0,decodedString.length);
        imageView.setImageBitmap(editFinal);
    }
}
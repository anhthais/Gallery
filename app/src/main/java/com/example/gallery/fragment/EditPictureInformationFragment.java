package com.example.gallery.fragment;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.ExifInterface;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.text.InputFilter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gallery.GetLocationActivity;
import com.example.gallery.ImageActivity;
import com.example.gallery.MainActivity;
import com.example.gallery.R;
import com.example.gallery.ToolbarCallbacks;
import com.example.gallery.object.Image;
import com.google.android.gms.maps.model.LatLng;
import java.io.File;
import java.io.IOException;


public class EditPictureInformationFragment extends Fragment {
    private Toolbar topBar;
    private Image img;
    private TextView locationText;
    private Context context;
    private int curPos;
    public EditPictureInformationFragment(Image img ) {
        this.img = img ;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        context=getActivity();
        View view = inflater.inflate(R.layout.details_fragment, container, false);
        topBar = view.findViewById(R.id.topAppBar);
        EditText edt_name = view.findViewById(R.id.edittextName);
        EditText edtDescription = view.findViewById(R.id.edittextDescription);
        Button btn_change = view.findViewById(R.id.btnSavePictureInfor);

        edtDescription.setFilters(new InputFilter[] { new InputFilter.LengthFilter(1000) });
        edt_name.setEnabled(false); // TODO: updating the changing name feature

        SharedPreferences myPref = context.getSharedPreferences("DESCRIPTION", Activity.MODE_PRIVATE);
        String oldDescription = myPref.getString(String.valueOf(img.getIdInMediaStore()), "");
        edtDescription.setText(oldDescription);
        File imageFile = new File(img.getPath());
        edt_name.setText(imageFile.getName());

        topBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((ImageActivity) context).getSupportFragmentManager().popBackStack();
            }
        });

        btn_change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = myPref.edit();
                editor.putString(String.valueOf(img.getIdInMediaStore()), edtDescription.getText().toString());
                editor.apply();
                Toast.makeText(context, R.string.save_description_success, Toast.LENGTH_SHORT).show();
                edtDescription.setFocusable(false);
            }
        });

        return view;
    }
}

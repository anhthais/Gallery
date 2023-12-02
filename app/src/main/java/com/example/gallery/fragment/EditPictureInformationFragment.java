package com.example.gallery.fragment;


import android.media.ExifInterface;
import android.os.Bundle;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.gallery.R;
import com.example.gallery.ToolbarCallbacks;
import com.example.gallery.object.Image;

import java.io.File;


public class EditPictureInformationFragment extends Fragment implements ToolbarCallbacks {

    private Toolbar topBar;
    private Image img;
    public EditPictureInformationFragment(Image img )
    {
        this.img = img ;
    }
    @Override
    public void showOrHideToolbars(boolean show) {
        if (show) {
            topBar.setVisibility(View.VISIBLE);
        } else {
            topBar.setVisibility(View.INVISIBLE);


        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.details_fragment, container, false);
        topBar = view.findViewById(R.id.topAppBar);
        TextView edt_name = view.findViewById(R.id.name);
        TextView tv_path = view.findViewById(R.id.path);
        TextView tv_date = view.findViewById(R.id.date);
        TextView tv_size = view.findViewById(R.id.size);
        EditText edtDescription = view.findViewById(R.id.description);
        Button btn_change = view.findViewById(R.id.btnSavePictureInfor);
        TextView tv_capacity = view.findViewById(R.id.capacity);


        try {
            ExifInterface exif = new ExifInterface(img.getPath());
            File imageFile = new File(img.getPath());

            String fileName = imageFile.getName();
            long fileSizeInBytes = imageFile.length();


            long fileSizeInMB = fileSizeInBytes / (1024 ); // Dung lượng ảnh trong kilobytes
            tv_capacity.setText(fileSizeInMB+ " MB");

            edt_name.setText(fileName);
            // Lấy đường dẫn của ảnh (nếu có)
            String imagePath = exif.getAttribute(ExifInterface.TAG_FILE_SOURCE);

            String date = exif.getAttribute(ExifInterface.TAG_DATETIME);
            tv_date.setText(date);

            String width = exif.getAttribute(ExifInterface.TAG_IMAGE_WIDTH);
            String length = exif.getAttribute(ExifInterface.TAG_IMAGE_LENGTH);
            tv_size.setText(width + "x" + length );


        } catch (Exception e) {
            e.printStackTrace();
        }



        tv_path.setText(img.getPath().substring(0,img.getPath().lastIndexOf("/")-1));






        topBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finish();
            }
        });

        btn_change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                edt_name.setEnabled(true);
                edtDescription.setEnabled(true);
            }
        });



        return view;
    }
}

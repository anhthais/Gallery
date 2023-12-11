package com.example.gallery.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.app.WallpaperManager;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Html;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;
import androidx.appcompat.widget.Toolbar;

import com.example.gallery.Animation.ViewPagerTransformAnimation;
import com.example.gallery.Database.DatabaseHelper;
import com.example.gallery.GetLocationActivity;
import com.example.gallery.ImageActivity;
import com.example.gallery.MainActivity;
import com.example.gallery.MainCallBackObjectData;
import com.example.gallery.MainCallBacks;
import com.example.gallery.R;
import com.example.gallery.TextResultImageActivity;
import com.example.gallery.ToolbarCallbacks;
import com.example.gallery.adapter.ImageViewPagerAdapter;
import com.example.gallery.helper.DateConverter;
import com.example.gallery.helper.FileManager;
import com.example.gallery.object.Album;
import com.example.gallery.object.Image;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class ImageViewFragment extends Fragment implements ToolbarCallbacks {
    private Context context;
    public ViewPager2 imageViewPager2;
    public ImageViewPagerAdapter viewPagerAdapter;
    private BottomNavigationView btnv;
    public ArrayList<Album> album_list;
    public ArrayList<Long> addFav;
    public ArrayList<Long> removeFav;
    public ArrayList<Long> deletePos;
    public ArrayList<Long> deleteTime;
    public ArrayList<String> newDeletedImagePath;
    private Toolbar topBar;
    public ArrayList<Image> images;
    public int curPos = 0;
    private MainCallBacks callback;
    public ImageActivity main;
    public String oldPath, newPath;

    public ImageViewFragment(Context context, ArrayList<Image> images, ArrayList<Album> album_names, int curPos) {
        this.context = context;
        this.album_list = album_names;
        this.images = images;
        this.curPos = curPos;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        removeFav = new ArrayList<>();
        addFav = new ArrayList<>();
        deletePos = new ArrayList<>();
        deleteTime = new ArrayList<>();
        newDeletedImagePath = new ArrayList<>();
        main = (ImageActivity) getActivity();
        main.imageViewFragment = this;
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // get views
        View view = inflater.inflate(R.layout.picture_fragment, container, false);
        imageViewPager2 = view.findViewById(R.id.view_pager);
        btnv = view.findViewById(R.id.navigation_bar_picture);
        topBar = view.findViewById(R.id.topAppBar);

        // set views data
        viewPagerAdapter = new ImageViewPagerAdapter(context, images);
        viewPagerAdapter.setToolbarCallbacks(this);
        imageViewPager2.setAdapter(viewPagerAdapter);
        imageViewPager2.setCurrentItem(curPos, false);
        imageViewPager2.setPageTransformer(new ViewPagerTransformAnimation());

        imageViewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if(position == 0 && positionOffset == 0) return;
                Image image = images.get(position);
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
                if (image.isFavorite()) {
                    btnv.getMenu().getItem(0).setIcon(R.drawable.baseline_favorite_24);
                } else {
                    btnv.getMenu().getItem(0).setIcon(R.drawable.baseline_favorite_border_24);
                }
            }
        });

        // handle topBar
        topBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finish();
            }
        });
        topBar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.btnSetAsWall) {

                    Bitmap bitmap = BitmapFactory.decodeFile(images.get(imageViewPager2.getCurrentItem()).getPath());
                    WallpaperManager wallpaperManager = WallpaperManager.getInstance(context);

                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setIcon(R.drawable.logo_app);
                    builder.setTitle(R.string.set_as_wall);
                    builder.setMessage(R.string.select_screen);

                    RadioButton homeScreenRadioButton = new RadioButton(getActivity());
                    homeScreenRadioButton.setText(R.string.main_screen);

                    RadioButton lockScreenRadioButton = new RadioButton(getActivity());
                    lockScreenRadioButton.setText(R.string.lock_screen);
                    // Create a ColorStateList for the background tint
                    int[][] states = new int[][]{
                            new int[]{android.R.attr.state_checked},
                            new int[]{-android.R.attr.state_checked}
                    };

                    int[] colors = new int[]{
                            ContextCompat.getColor(context, R.color.black),
                            ContextCompat.getColor(context, R.color.black)
                    };
                    ColorStateList colorStateList = new ColorStateList(states, colors);

                    // Apply the  ColorStateList to the background tint
                    homeScreenRadioButton.setButtonTintList(colorStateList);
                    lockScreenRadioButton.setButtonTintList(colorStateList);

                    RadioGroup radioGroup = new RadioGroup(getActivity());
                    radioGroup.addView(homeScreenRadioButton);
                    radioGroup.addView(lockScreenRadioButton);

                    builder.setView(radioGroup);
                    builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (homeScreenRadioButton.isChecked()) {
                                try {
                                    wallpaperManager.setBitmap(bitmap, null, true, WallpaperManager.FLAG_SYSTEM);
                                    Toast.makeText(context, R.string.home_screen_success, Toast.LENGTH_SHORT).show();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                    Toast.makeText(context, R.string.home_screen_fail, Toast.LENGTH_SHORT).show();
                                }
                            } else if (lockScreenRadioButton.isChecked()) {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                    try {
                                        wallpaperManager.setBitmap(bitmap, null, true, WallpaperManager.FLAG_LOCK);
                                    } catch (IOException e) {
                                        throw new RuntimeException(e);
                                    }
                                    Toast.makeText(context, R.string.lock_screen_success, Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(context, R.string.lock_screen_fail, Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    });
                    builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    Toast.makeText(context, R.string.cancel, Toast.LENGTH_SHORT).show();
                                }
                            }
                    );

                    AlertDialog dialog = builder.create();
                    dialog.show();
                    Button buttonPositive = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
                    buttonPositive.setTextColor(ContextCompat.getColor(context, R.color.black));
                    Button buttonNegative = dialog.getButton(DialogInterface.BUTTON_NEGATIVE);
                    buttonNegative.setTextColor(ContextCompat.getColor(context, R.color.black));

                } else if (id == R.id.btnAddToAlbum) {
                    if (album_list == null || album_list.size() == 0) {
                        Toast.makeText(context, R.string.no_album_found, Toast.LENGTH_SHORT).show();
                    } else {
                        ((ImageActivity)context).onMsgFromFragToMain("ADD-TO-ALBUM",images.get(imageViewPager2.getCurrentItem()).getPath());
                    }
                } else if (id == R.id.btnReadTextInImage) {
                    Uri uriToImage = getUriFromPath(getContext(), new File(images.get(imageViewPager2.getCurrentItem()).getPath()));
                    Intent intent = new Intent(context, TextResultImageActivity.class);
                    intent.putExtra("uriTextImage", uriToImage.toString());
                    ((ImageActivity) context).startActivity(intent);

                }else if(id==R.id.btnHide) {
                    String dcimPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath();
                    String imagepath = images.get(imageViewPager2.getCurrentItem()).getPath();
                    String filextension = imagepath.substring(imagepath.lastIndexOf("."));
                    dcimPath += "/.nomedia";
                    File dcimNomedia = new File(dcimPath);
                    if (!dcimNomedia.exists()) {
                        dcimNomedia.mkdir();
                    }
                    dcimPath += ("/" + String.valueOf(System.currentTimeMillis()) + filextension);
                    if (FileManager.moveFile(getActivity(), imagepath, dcimPath, context)) {
                        SharedPreferences hidePref = context.getSharedPreferences("GALLERY", Context.MODE_PRIVATE);
                        Gson gson = new Gson();
                        String HideCurrentJSON = hidePref.getString("HIDE-CURRENT", null);
                        String HideBeforeJSON = hidePref.getString("HIDE-BEFORE", null);
                        ArrayList<String> hide_current = null;
                        ArrayList<String> hide_before = null;
                        //put in intent on activity rsult
                        Intent intent = ((ImageActivity) context).getIntent();
                        ArrayList<String> deletePaths = null;
                        String deleteJSON = intent.getStringExtra("Trash");
                        if (deleteJSON == null) {
                            deletePaths = new ArrayList<>();
                        } else {
                            deletePaths = gson.fromJson(deleteJSON, new TypeToken<ArrayList<String>>() {
                            }.getType());
                        }
                        deletePaths.add(imagepath);
                        intent.putExtra("Trash", gson.toJson(deletePaths));
                        ((ImageActivity) context).setResult(AppCompatActivity.RESULT_OK, intent);

                        if (HideCurrentJSON == null || HideCurrentJSON.isEmpty()) {
                            hide_before = new ArrayList<>();
                            hide_current = new ArrayList<>();
                        } else {
                            hide_current = gson.fromJson(HideCurrentJSON, new TypeToken<ArrayList<String>>() {}.getType());
                            hide_before = gson.fromJson(HideBeforeJSON, new TypeToken<ArrayList<String>>() {}.getType());
                        }
                        hide_before.add(imagepath);
                        hide_current.add(dcimPath);
                        SharedPreferences.Editor editor = hidePref.edit();
                        editor.putString("HIDE-BEFORE", gson.toJson(hide_before));
                        editor.putString("HIDE-CURRENT", gson.toJson(hide_current));
                        editor.commit();
                        int removeindex = imageViewPager2.getCurrentItem();
                        images.remove(imageViewPager2.getCurrentItem());
                        imageViewPager2.getAdapter().notifyItemRemoved(removeindex);

                    } else {
                        Toast.makeText(context, R.string.cannot_hide, Toast.LENGTH_SHORT).show();
                    }
                }
                else if (id == R.id.btnViewInfor) {

                    Image currentImage = images.get(imageViewPager2.getCurrentItem());
                    EditPictureInformationFragment editFragment = new EditPictureInformationFragment(currentImage);

                    Bundle bundle = new Bundle();
                    bundle.putString("key", "value");
                    editFragment.setArguments(bundle);
                    FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                    FragmentTransaction transaction = fragmentManager.beginTransaction();
                    transaction.replace(R.id.pictureFragment, editFragment);
                    transaction.addToBackStack(null);
                    transaction.commit();
                }
                else if (id==R.id.btnAddLocation)
                {
                    Toast.makeText(context, R.string.loadGGMap, Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getContext(), GetLocationActivity.class);
                    Image currentImage = images.get(imageViewPager2.getCurrentItem());
                    if (currentImage.getLocation()!=null)
                    {
                        intent.putExtra("latitude",currentImage.getLocation().latitude);
                        intent.putExtra("longitude",currentImage.getLocation().longitude);
                    }
                    int position = imageViewPager2.getCurrentItem();

                    intent.putExtra("curPos",position);
                    ((ImageActivity)context).startActivityForResult(intent,4123);
                }
                return true;
            }
        });

        // handle bottom navigation bar
        btnv.setOnNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.btnLovePicture) {
                int position = imageViewPager2.getCurrentItem();
                Image image = images.get(position);

                if (image.isFavorite()) {
                    removeFav.add(image.getIdInMediaStore());
                    addFav.remove(Long.valueOf(image.getIdInMediaStore()));
                    image.setFavorite(false);
                    btnv.getMenu().getItem(0).setIcon(R.drawable.baseline_favorite_border_24);
                } else {
                    addFav.add(image.getIdInMediaStore());
                    removeFav.remove(Long.valueOf(image.getIdInMediaStore()));
                    image.setFavorite(true);
                    btnv.getMenu().getItem(0).setIcon(R.drawable.baseline_favorite_24);
                }

                Intent intent = ((ImageActivity) context).getIntent();
                Gson gson = new Gson();
                intent.putExtra("removeFav", gson.toJson(removeFav));
                intent.putExtra("addFav", gson.toJson(addFav));
                ((ImageActivity) context).setResult(AppCompatActivity.RESULT_OK, intent);

            } else if (id == R.id.btnEditPicture) {

                if (callback != null) {
                    callback.onMsgFromFragToMain("EDIT-PHOTO", String.valueOf(imageViewPager2.getCurrentItem()));
                }

            } else if (id == R.id.btnSharePicture) {
                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                Uri uriToImage = Uri.parse(new File(images.get(imageViewPager2.getCurrentItem()).getPath()).toString());
                shareIntent.putExtra(Intent.EXTRA_STREAM, uriToImage);
                shareIntent.setType("image/jpeg");
                startActivity(Intent.createChooser(shareIntent, null));

            } else if (id == R.id.btnDeletePicture) {
                if(Build.VERSION.SDK_INT <= Build.VERSION_CODES.P){
                    AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.MyDialogTheme);
                    builder.setTitle(R.string.move_to_trash);
                    builder.setCancelable(true);
                    builder.setPositiveButton(R.string.move, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            deleteImage();
                        }

                    });
                    builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            //  Cancel
                            dialog.cancel();
                        }
                    });

                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                } else {
                    deleteImage();
                }
            }
            return true;
        });

        return view;
    }

    @Override
    public void showOrHideToolbars(boolean show) {
        if (show) {
            topBar.setVisibility(View.VISIBLE);
            btnv.setVisibility(View.VISIBLE);
        } else {
            topBar.setVisibility(View.INVISIBLE);
            btnv.setVisibility(View.INVISIBLE);
        }
    }

    public static Uri getUriFromPath(Context context, File file) {
        String filePath = file.getAbsolutePath();
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Images.Media._ID},
                MediaStore.Images.Media.DATA + "=? ",
                new String[]{filePath}, null);
        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
            cursor.close();
            return Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "" + id);
        } else {
            if (file.exists()) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DATA, filePath);
                return context.getContentResolver().insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            } else {
                return null;
            }
        }
    }

    public void deleteImage(){
        try {
            Image image = images.get(imageViewPager2.getCurrentItem());
            String appFolder = getActivity().getApplicationContext().getExternalFilesDir("").getAbsolutePath();
            File folder = new File(appFolder, "Trash");
            if (!folder.exists()) {
                folder.mkdir();
            }

            oldPath = image.getPath();
            String[] dirInPath = oldPath.split("/");
            String filename = dirInPath[dirInPath.length - 1];
            newPath = folder.getAbsolutePath() + "/" + filename;

            if (FileManager.moveFile(main, oldPath, newPath, context)) {
                Toast.makeText(context, R.string.delete_photo_success, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, R.string.cannot_delete_photo, Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.d("error delete", e.getMessage());
        }
    }
}
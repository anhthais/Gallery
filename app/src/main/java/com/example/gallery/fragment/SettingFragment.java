package com.example.gallery.fragment;
import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.ConditionVariable;
import android.preference.PreferenceFragment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.FragmentTransaction;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.example.gallery.FragmentCallBacks;
import com.example.gallery.MainActivity;
import com.example.gallery.R;

import java.util.Calendar;

public class SettingFragment extends PreferenceFragmentCompat implements FragmentCallBacks {
    Context context;
    MainActivity main;
    TrashFragment trashFragment;
    FavouriteImageFragment favouriteImageFragment;
    HideFragment hideFragment;
    public  static SettingFragment getInstance()
    {
        return new SettingFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            context = getActivity(); // use this reference to invoke main callbacks
            main = (MainActivity) getActivity();
            trashFragment = main.getTrashFragment();
            favouriteImageFragment = main.getFavouriteImageFragment();
            hideFragment = main.getHideFragment();
        } catch (IllegalStateException e) {
            throw new IllegalStateException("MainActivity must implement callbacks");
        }
        addPreferencesFromResource(R.xml.preferences);
        Preference btnTheme = (Preference) findPreference("btnTheme");

        Preference btnLanguage = (Preference)findPreference("btnLanguage");
        Preference btnBackup = (Preference)findPreference("btnBackup");
        Preference btnHide = (Preference)findPreference("btnHide");
        Preference btnTrash= (Preference)findPreference("btnTrash");
        Preference btnFavourite = (Preference)findPreference("btnFavourite");
        btnTheme.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(@NonNull Preference preference) {
                Dialog addDialog=new Dialog(context);
                addDialog.setContentView(R.layout.theme_dialog);
                RadioGroup radioGroup = addDialog.findViewById(R.id.groupTheme);
                addDialog.create();
                addDialog.show();
                radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup radioGroup, int i) {
                        RadioButton radioButton = (RadioButton)radioGroup.findViewById(i);
                        radioButton.getId();
                        if (i ==R.id.btnThemeLight)
                        {
                            SharedPreferences myPref = main.getSharedPreferences("GALLERY", Activity.MODE_PRIVATE);
                            SharedPreferences.Editor editor = myPref.edit();
                            editor.putString("THEME","LIGHT").apply();
                            addDialog.cancel();
                            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

                        }
                        else if (i==R.id.btnThemeDark)
                        {

                            SharedPreferences myPref = main.getSharedPreferences("GALLERY", Activity.MODE_PRIVATE);
                            SharedPreferences.Editor editor = myPref.edit();
                            editor.putString("THEME","DARK").apply();
                            addDialog.cancel();
                            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);

                        }

                        else if (i==R.id.btnThemeSameAsSystem)
                        {

                            SharedPreferences myPref = main.getSharedPreferences("GALLERY", Activity.MODE_PRIVATE);
                            SharedPreferences.Editor editor = myPref.edit();
                            editor.putString("THEME","SAME-AS-SYSTEM").apply();
                            addDialog.cancel();
                            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);

                        }

                    }
                });
                return false;
            }
        });
        btnLanguage.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(@NonNull Preference preference) {
                Dialog addDialog=new Dialog(context);
                addDialog.setContentView(R.layout.language_dialog);
                RadioGroup radioGroup = addDialog.findViewById(R.id.groupLanguage);
                addDialog.create();
                addDialog.show();
                radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup radioGroup, int i) {
                        RadioButton radioButton = (RadioButton)radioGroup.findViewById(i);
                        radioButton.getId();
                        if (i ==R.id.btnEnglish)
                        {
                            if(!getResources().getConfiguration().getLocales().get(0).toString().equals("en")){
                                SharedPreferences pref=main.getSharedPreferences("GALLERY",MODE_PRIVATE);
                                SharedPreferences.Editor editor=pref.edit();
                                editor.putString("LANGUAGE","en");
                                editor.commit();
                                main.setLocale("en");
                            }
                            addDialog.cancel();
                        }
                        else if (i==R.id.btnVietnamese)
                        {
                            if(!getResources().getConfiguration().getLocales().get(0).toString().equals("vi")){
                                SharedPreferences pref=main.getSharedPreferences("GALLERY",MODE_PRIVATE);
                                SharedPreferences.Editor editor=pref.edit();
                                editor.putString("LANGUAGE","vi");
                                editor.commit();
                                main.setLocale("vi");
                            }
                            addDialog.cancel();
                        }

                    }
                });
                return false;
            }
        });
        btnBackup.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(@NonNull Preference preference) {
                return false;
            }
        });
        btnHide.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(@NonNull Preference preference) {
                SharedPreferences hidePref=main.getSharedPreferences("GALLERY",MODE_PRIVATE);
                            String password=hidePref.getString("PASSWORD",null);
                            if(password==null){
                                //show add new album frag
                                int opendate=hidePref.getInt("OPEN-DATE",0);
                                int openyear=hidePref.getInt("OPEN-YEAR",0);
                                int openmoth=hidePref.getInt("OPEN-MONTH",0);
                                Calendar calendar=Calendar.getInstance();
                                int curr_date=calendar.get(Calendar.DATE);
                                int curr_month=calendar.get(Calendar.MONTH);
                                int curr_year=calendar.get(Calendar.YEAR);
                                boolean firstVisit=true;
                                if(curr_year<openyear){
                                    firstVisit=false;
                                }else if(curr_year==openyear){
                                    if(curr_month<openmoth){
                                        firstVisit=false;
                                    }else if(curr_month==openmoth){
                                        if(curr_date<opendate){
                                            firstVisit=false;
                                        }
                                    }
                                }
                                if(firstVisit){
                                    main.createPasswordHideFragmentDialog();
                                }else{
                                    Toast.makeText(context, R.string.comebacklater, Toast.LENGTH_SHORT).show();
                                }
                            }else{
                                main.showHideFragmentDialog();
                            }
                return false;
            }
        });
        btnTrash.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(@NonNull Preference preference) {
                FragmentTransaction ft = main.getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.mainFragment,trashFragment);
                ft.addToBackStack("FRAG1");
                ft.commit();
                return false;
            }
        });
        btnFavourite.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(@NonNull Preference preference) {
                FragmentTransaction ft=main.getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.mainFragment, favouriteImageFragment);
                ft.addToBackStack("FRAG");
                ft.commit();
                return false;
            }
        });

    }

    @Override
    public void onCreatePreferences(@Nullable Bundle savedInstanceState, @Nullable String rootKey) {
    }

    @Override
    public void onMsgFromMainToFragment(String strValue) {

    }
//    public void showHideFragmentDialog(){
//        Dialog addDialog=new Dialog(context);
//        addDialog.setContentView(R.layout.access_hidefragment_dialog);
//        EditText editText=addDialog.findViewById(R.id.confirmPasswordEditText1);
//        Button ok=addDialog.findViewById(R.id.btnOKConfirmPassword);
//        Button cancel=addDialog.findViewById(R.id.btnCancelConfirmPassword);
//        Button reset=addDialog.findViewById(R.id.btnResetPassword);
//        addDialog.create();
//        addDialog.show();
//        ok.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                SharedPreferences hidePref=main.getSharedPreferences("GALLERY",MODE_PRIVATE);
//                String pass=hidePref.getString("PASSWORD",null);
//                if(pass.equals(editText.getText().toString())){
//                    //show hide frag
//                    main.showHideFragment();
//                    addDialog.cancel();
//                }else{
//                    Toast.makeText(context, R.string.wrong_password, Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
//        cancel.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                addDialog.cancel();
//            }
//        });
//        reset.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                SharedPreferences hidePref=main.getSharedPreferences("GALLERY",MODE_PRIVATE);
//                SharedPreferences.Editor editor=hidePref.edit();
//                editor.putString("PASSWORD",null);
//                Calendar calendar=Calendar.getInstance();
//                int curr_date=calendar.get(Calendar.DATE);
//                int curr_month=calendar.get(Calendar.MONTH);
//                int curr_year=calendar.get(Calendar.YEAR);
//                editor.putInt("OPEN-DATE",curr_date);
//                editor.putInt("OPEN-MONTH",curr_month);
//                editor.putInt("OPEN-YEAR",curr_year);
//                editor.commit();
//                addDialog.cancel();
//            }
//        });
//    }
//    public void createPasswordHideFragmentDialog(){
//        Dialog addDialog=new Dialog(context);
//        addDialog.setContentView(R.layout.create_password_hidefrag_dialog);
//        EditText password=addDialog.findViewById(R.id.createPasswordEditText1);
//        EditText confirmPass=addDialog.findViewById(R.id.createPasswordEditText2);
//        Button ok=addDialog.findViewById(R.id.btnOKCreatePassword);
//        Button cancel=addDialog.findViewById(R.id.btnCancelCreatePassword);
//        addDialog.create();
//        addDialog.show();
//        ok.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String pass=password.getText().toString();
//                String confirm=confirmPass.getText().toString();
//                if(!pass.equals(confirm)){
//                    Toast.makeText(context, R.string.retype_password_not_match, Toast.LENGTH_SHORT).show();
//                }
//                else{
//                    SharedPreferences hidePref=main.getSharedPreferences("GALLERY",MODE_PRIVATE);
//                    SharedPreferences.Editor editor=hidePref.edit();
//                    editor.putString("PASSWORD",pass);
//                    editor.commit();
//                    addDialog.cancel();
//                    //show hide fragment
//                    showHideFragment();
//                }
//            }
//        });
//        cancel.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                addDialog.cancel();
//            }
//        });
//    }
//    public void showHideFragment(){
//        hideFragment= HideFragment.getInstance();
//        main.getSupportFragmentManager().beginTransaction().replace(R.id.mainFragment,hideFragment).commit();
//    }
}
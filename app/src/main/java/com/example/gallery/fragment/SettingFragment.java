package com.example.gallery.fragment;
import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.ConditionVariable;
import android.preference.PreferenceFragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.FragmentTransaction;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.example.gallery.FragmentCallBacks;
import com.example.gallery.MainActivity;
import com.example.gallery.MainCallBacks;
import com.example.gallery.R;

import java.util.Calendar;

public class SettingFragment extends PreferenceFragmentCompat implements FragmentCallBacks {
    Context context;
    MainActivity main;
    TrashFragment trashFragment;
    FavouriteImageFragment favouriteImageFragment;
    HideFragment hideFragment;
    ActionBar action_bar;
    MainCallBacks callback;
    Menu menu;
    public  static SettingFragment getInstance()
    {
        return new SettingFragment();
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
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            context = getActivity(); // use this reference to invoke main callbacks
            main = (MainActivity) getActivity();
            trashFragment = main.getTrashFragment();
            favouriteImageFragment = main.getFavouriteImageFragment();
            hideFragment = main.getHideFragment();
            action_bar = ((MainActivity) getActivity()).getSupportActionBar();
            menu = main.getMenu();

        } catch (IllegalStateException e) {
            throw new IllegalStateException("MainActivity must implement callbacks");
        }
        menu.findItem(R.id.btnFind).setVisible(false);
        menu.findItem(R.id.btnChooseMulti).setVisible(false);
        menu.findItem(R.id.btnSlideShow).setVisible(false);
        menu.findItem(R.id.btnAI_Image).setVisible(false);
        menu.findItem(R.id.btnAddNewAlbum).setVisible(false);
        menu.findItem(R.id.btnDeleteAlbum).setVisible(false);
        menu.findItem(R.id.btnEvent).setVisible(false);
        menu.findItem(R.id.btnSort).setVisible(false);



        SharedPreferences pref=context.getSharedPreferences("GALLERY",MODE_PRIVATE) ;
        String theme=pref.getString("THEME","LIGHT");
        String themeSummary,languageSummary = null;
        if(theme.equals("LIGHT")){
            themeSummary = getResources().getString(R.string.light);;
        }else if(theme.equals("DARK")){
            themeSummary = getResources().getString(R.string.dark);
        }else
        {
            themeSummary = getResources().getString(R.string.same_as_system);
        }
        String locale=pref.getString("LANGUAGE","en");
        if (locale.equals("en"))
        {
            languageSummary = getResources().getString(R.string.english);
        }
        else if (locale.equals("vi"))
        {
            languageSummary = getResources().getString(R.string.vietnamese);
        }
        addPreferencesFromResource(R.xml.preferences);
        Preference btnTheme = (Preference) findPreference("btnTheme");
        Preference btnLanguage = (Preference)findPreference("btnLanguage");
        Preference btnBackup = (Preference)findPreference("btnBackup");
        Preference btnHide = (Preference)findPreference("btnHide");
        Preference btnTrash= (Preference)findPreference("btnTrash");
        Preference btnFavourite = (Preference)findPreference("btnFavourite");
        btnTheme.setSummary(themeSummary);
        btnLanguage.setSummary(languageSummary);

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
                            Intent refresh = new Intent(context, MainActivity.class);
                            startActivity(refresh);

                        }
                        else if (i==R.id.btnThemeDark)
                        {

                            SharedPreferences myPref = main.getSharedPreferences("GALLERY", Activity.MODE_PRIVATE);
                            SharedPreferences.Editor editor = myPref.edit();
                            editor.putString("THEME","DARK").apply();
                            addDialog.cancel();
                            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                            Intent refresh = new Intent(context, MainActivity.class);
                            startActivity(refresh);

                        }

                        else if (i==R.id.btnThemeSameAsSystem)
                        {

                            SharedPreferences myPref = main.getSharedPreferences("GALLERY", Activity.MODE_PRIVATE);
                            SharedPreferences.Editor editor = myPref.edit();
                            editor.putString("THEME","SAME-AS-SYSTEM").apply();
                            addDialog.cancel();
                            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                            Intent refresh = new Intent(context, MainActivity.class);
                            startActivity(refresh);
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
                callback.onMsgFromFragToMain("SETTING","BACK-UP");
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


}
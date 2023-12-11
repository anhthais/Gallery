package com.example.gallery;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.provider.Telephony;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class GetLocationActivity extends FragmentActivity implements OnMapReadyCallback {
    GoogleMap mMap;
    private SearchView mapSearchView;
    Context context;
    LatLng curLatLng;

    Button btnReturn;
    Button btnSave;
    Button btnCancelLocation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_location);
        curLatLng = null;
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapSearchView = findViewById(R.id.mapSearch);
        btnReturn = findViewById(R.id.btnGetLocationReturn);
        btnSave= findViewById(R.id.btnSaveLocation);
        btnCancelLocation = findViewById(R.id.btnCancelLocation);
        Intent intent = getIntent();
        int check = intent.getIntExtra("curPos",-1);


        mapSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                String location = mapSearchView.getQuery().toString();
                List<Address> addressList = null;
                if (location != null)
                {
                    Geocoder geocoder = new Geocoder(GetLocationActivity.this);
                    try {
                        addressList = geocoder.getFromLocationName(location,1);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (addressList.isEmpty())
                    {
                        Toast.makeText(GetLocationActivity.this, "Không tìm thấy địa chỉ", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Address address = addressList.get(0);
                        LatLng latLng = new LatLng(address.getLatitude(),address.getLongitude());
                        curLatLng = latLng;
                        mMap.addMarker(new MarkerOptions().position(latLng).title(location));
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,70));
                    }



                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
        btnReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent();
                setResult(-2, intent1);
                finish();
            }
        });
        btnCancelLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = getIntent();
                intent.putExtra("curPos",check);
                setResult(RESULT_CANCELED, intent);
                finish();
            }
        });
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                callBacks.onMsgFromLocationToMain("LOCATION", curLatLng.latitude,curLatLng.longitude);
                if (curLatLng==null)
                {
                    Toast.makeText(GetLocationActivity.this, "Bạn chưa chọn vị trí", Toast.LENGTH_SHORT).show();
                }
                else {
                    Intent intent = getIntent();
                    intent.putExtra("curPos",check);
                    intent.putExtra("latitude",curLatLng.latitude);
                    intent.putExtra("longitude", curLatLng.longitude);
                    setResult(RESULT_OK, intent);

                    finish();
                }

            }
        });
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        Intent intent = getIntent();
        Double latitude = intent.getDoubleExtra("latitude",-1);
        Double longitude = intent.getDoubleExtra("longitude",-1);
        if (latitude!=-1)
        {
            LatLng now = new LatLng(latitude,longitude);

            googleMap.addMarker(new MarkerOptions().position(now).title(now.toString()));
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(now));
        }
        else{
            LatLng vietnam = new LatLng(14.34523, 109.21360);

            googleMap.addMarker(new MarkerOptions().position(vietnam).title("Marker in Vietnam"));
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(vietnam));
        }

        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(@NonNull LatLng latLng) {
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(latLng);
                markerOptions.title(latLng.toString());
                curLatLng = latLng;
                googleMap.clear();
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,70));
                googleMap.addMarker(markerOptions);
            }
        });
    }
    public static String getAddressFromLatLng(Context context,Double latitude, Double longitude) throws IOException {
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(context, Locale.getDefault());

        addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
        String address;
        if (!addresses.isEmpty())
        {
            address = addresses.get(0).getAddressLine(0);

        }
        else {
            address=null;
        }
        return address;
    }

}
package com.dauntlessdev.memorableplaces;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    GoogleMap mMap;
    LocationManager locationManager;
    LocationListener locationListener;
    Marker locationMarker;
    LatLng currentLocation;
    SharedPreferences sharedPreferences;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 1, locationListener);
                }

            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        Log.i("ContextMap",this.toString());
        sharedPreferences = this.getSharedPreferences("com.dauntlessdev.memorableplaces", Context.MODE_PRIVATE);
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                locationMarker.remove();
                locationMarker = mMap.addMarker(new MarkerOptions().position(currentLocation).title("Your Location"));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 10));

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION},1);
        }else{
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,1, locationListener);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        Intent intent = getIntent();
        int position = intent.getIntExtra("Key",0);
        if(MainActivity.currentMemorableList!=null && MainActivity.currentMemorableList.size() > 0){
            for(int i =0; i < MainActivity.currentMemorableList.size(); i++){
                mMap.addMarker(new MarkerOptions().position(new LatLng(MainActivity.currentMemorableList.get(i).latitude,MainActivity.currentMemorableList.get(i).longitude)).title(MainActivity.addressMainList.get(i+1)));
            }
        }
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        currentLocation = new LatLng(lastKnownLocation.getLatitude()  , lastKnownLocation.getLongitude());
        if(position!=0){
            position--;
            currentLocation = new LatLng(MainActivity.currentMemorableList.get(position).latitude  , MainActivity.currentMemorableList.get(position).longitude);
        }
        locationMarker = mMap.addMarker(new MarkerOptions().position(currentLocation).title("Your Location"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 10));

        mMap.setOnMapLongClickListener(new OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                try {
                    List<Address> addressList = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
                    if(addressList.size() > 0){
                        String locationName ;
                        if(addressList.get(0).getThoroughfare()!= null){
                            mMap.addMarker(new MarkerOptions().position(latLng).title(addressList.get(0).getThoroughfare()));
                            locationName = addressList.get(0).getThoroughfare();
                        }else{
                            mMap.addMarker(new MarkerOptions().position(latLng).title(Calendar.getInstance().getTime().toString()));
                            locationName = Calendar.getInstance().getTime().toString();
                        }

                        MainActivity.currentMemorableList.add(new LatLng(addressList.get(0).getLatitude(),addressList.get(0).getLongitude()));
                        Toast.makeText(MapsActivity.this, "Location Saved!", Toast.LENGTH_SHORT).show();

                        MainActivity.addressMainList.add(locationName);
                        MainActivity.arrayAdapter.notifyDataSetChanged();

                        ArrayList<String> latitudeList = new ArrayList<>();
                        ArrayList<String> longitudeList = new ArrayList<>();
                        for (Address latlng : addressList){
                            latitudeList.add(String.valueOf(latlng.getLatitude()));
                            longitudeList.add(String.valueOf(latlng.getLongitude()));
                        }
                        sharedPreferences.edit().putString("places", ObjectSerializer.serialize(MainActivity.addressMainList)).apply();
                        sharedPreferences.edit().putString("latitude", ObjectSerializer.serialize(latitudeList)).apply();
                        sharedPreferences.edit().putString("longitude", ObjectSerializer.serialize(longitudeList)).apply();


                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });

    }
}

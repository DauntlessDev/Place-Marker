package com.dauntlessdev.memorableplaces;

import androidx.appcompat.app.AppCompatActivity;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    static ArrayList<String> addressMainList;
    static ArrayAdapter<String> arrayAdapter;
    static ArrayList<LatLng> currentMemorableList;
    ListView placeListView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        placeListView = findViewById(R.id.listView);

        addressMainList = new ArrayList<>();
        currentMemorableList = new ArrayList<>();
        ArrayList<String> longitudeList = new ArrayList<>();
        ArrayList<String> latitudeList = new ArrayList<>();

        SharedPreferences sharedPreferences = this.getSharedPreferences("com.dauntlessdev.memorableplaces", Context.MODE_PRIVATE);
        Log.i("ContextMain",getApplicationContext().toString());
        try {
            longitudeList = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("longitude", ObjectSerializer.serialize(new ArrayList<String>())));
            latitudeList = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("latitude", ObjectSerializer.serialize(new ArrayList<String>())));
            addressMainList = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("places", ObjectSerializer.serialize(new ArrayList<String>())));

        } catch (IOException e) {
            e.printStackTrace();
        }

        if (longitudeList != null && latitudeList != null && addressMainList != null) {
            if (latitudeList.size() > 0 && longitudeList.size() > 0 && addressMainList.size() > 0){
                if(addressMainList.size() == latitudeList.size() && latitudeList.size() == longitudeList.size()){
                    for (int i=0; i < latitudeList.size(); i++){
                        currentMemorableList.add(new LatLng(Double.parseDouble(latitudeList.get(i)),Double.parseDouble(longitudeList.get(i))));
                    }
                }
            }else{
                addressMainList.add("Add a memorable place...");

            }
        }


        if (addressMainList != null) {
            arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, addressMainList);
        }
        placeListView.setAdapter(arrayAdapter);
        placeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
                intent.putExtra("Key", position);
                startActivity(intent);
            }
        });

    }
}

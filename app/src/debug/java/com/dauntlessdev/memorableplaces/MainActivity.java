package com.dauntlessdev.memorableplaces;

import androidx.appcompat.app.AppCompatActivity;

import android.app.PendingIntent;
import android.content.Intent;
import android.location.Address;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    static ArrayList<String> addressMainList;
    static ArrayAdapter<String> arrayAdapter;
    static ArrayList<Address> currentMemorableList;
    ListView placeListView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        placeListView = findViewById(R.id.listView);

        addressMainList = new ArrayList<>();
        addressMainList.add("Add a memorable place...");

        currentMemorableList = new ArrayList<>();

        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, addressMainList);
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

package com.example.pavainteligente;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends Activity implements Contract.ViewMVP {

    private ProgressDialog mProgressDlg;

    private Contract.PresenterMVP presenter;

    List<ListElement> elements;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        presenter = new Presenter(this);
        init();

    }


    public void init() {
        elements = new ArrayList<>();
        elements.add(new ListElement("#FF0000", "Pava 1", "Lab 266", "Disponible", 920.01, false));
        elements.add(new ListElement("#000000", "Pava 2", "Casa", "Disponible", 550.12, false));
        elements.add(new ListElement("#000000", "Pava 2", "Casa", "Desconectado", 230.00, false));

        ListAdapter listAdapter = new ListAdapter(elements, this, new ListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(ListElement item) {
                moveToDescription(item);
            }
        });
        RecyclerView recyclerView = findViewById(R.id.deviceRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(listAdapter);
    }


    public void moveToDescription(ListElement item) {
        Intent intent = new Intent(this, DescriptionActivity.class);
        intent.putExtra("ListElement", item);
        startActivity(intent);
    }

    @RequiresApi(api = Build.VERSION_CODES.S)
    public void Bluetooth(View view) {
        Intent intent = new Intent(MainActivity.this, ActivityBluetooth.class);
        //intent.putExtra("ActivityBluetooth", view);
        startActivity(intent);
    }
}
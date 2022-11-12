package com.example.pavainteligente;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends Activity implements Contract.ViewMVP {

    private ProgressDialog mProgressDlg;
    private Contract.PresenterMVP presenter;
    private ListElement elements;
    private Button btnPava;
    private final String[] permissions = new String[]{
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.BLUETOOTH_ADVERTISE,
            Manifest.permission.ACCESS_FINE_LOCATION,
            //Manifest.permission.ACCESS_BACKGROUND_LOCATION
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkPermissions();
        setContentView(R.layout.activity_main);
        btnPava = (Button) findViewById(R.id.btnPava);
        btnPava.setOnClickListener(btnPavaListener);
        presenter = new Presenter(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void onDestroy() {
        super.onDestroy();
    }


    private View.OnClickListener btnPavaListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            presenter.onButtonClick(MainActivity.this);
            elements = new ListElement();
            Intent intent = new Intent(MainActivity.this, DescriptionActivity.class);
            intent.putExtra("ListElement", elements);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                startActivity(intent);
            }
        }
    };


    @Override
    public void onItemClick(ListElement item) {

    }

    @Override
    public void lanzar(Context mainView) {

    }
    /*
            @RequiresApi(api = Build.VERSION_CODES.S)
            public void Bluetooth(View view) {
                Intent intent = new Intent(MainActivity.this, ActivityBluetooth.class);
                //intent.putExtra("ActivityBluetooth", view);
                startActivity(intent);
            }
        */
    @Override
    public ListElement setString(ListElement element) {
        return element;
    }

    //Metodo que chequea si estan habilitados los permisos
    private  boolean checkPermissions() {
        int result;
        List<String> listPermissionsNeeded = new ArrayList<>();

        //Se chequea si la version de Android es menor a la 6
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {

            return true;
        }

        for (String p:permissions) {
            result = ContextCompat.checkSelfPermission(this, p);
            if (result != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(p);
            }
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this , listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), 100);
            return false;
        }
        return true;
    }

}
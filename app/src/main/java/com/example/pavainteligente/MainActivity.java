package com.example.pavainteligente;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;


public class MainActivity extends Activity implements ContratoMain.ViewMain {

    private ProgressDialog mProgressDlg;
    private ContratoMain.PresenterMain presenter;
    private Pava elements;
    private Button btnPava;
    TextView Dispositivos;
    ProgressBar prog_shake;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnPava = (Button) findViewById(R.id.btnPava);
        btnPava.setOnClickListener(btnPavaListener);
        Dispositivos=findViewById(R.id.dispositivos);
        prog_shake=findViewById(R.id.prog_shake);
        presenter = new PresenterMain(this);

    }

    @Override
    protected void onResume() {
        presenter.setUpSensormanager();
        super.onResume();
    }

    public void onDestroy() {
        super.onDestroy();
        presenter.onDestroy();
    }


    private View.OnClickListener btnPavaListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            elements = new Pava();
            Intent intent = new Intent(MainActivity.this, DescriptionActivity.class);
            intent.putExtra("Pava", elements);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                startActivity(intent);
            }
        }
    };


    @Override
    public void onItemClick(Pava item) {

    }

    @Override
    public void lanzar(Context mainView) {

    }

    @Override
    public Pava setString(Pava element) {
        return element;
    }

    protected void onPause() {
        presenter.onpausa();
        super.onPause();

    }

    @Override
    public void cambiarColorR() {
        Dispositivos.setTextColor(Color.RED);
    }

    @Override
    public void cambiarColorB() {
        Dispositivos.setTextColor(Color.BLUE);
    }

    @Override
    public void cambiarColorY() {
        Dispositivos.setTextColor(Color.YELLOW);
    }

    @Override
    public void cambiarBarra(int valor) {
        prog_shake.setProgress(valor);

    }

}
package com.example.pavainteligente;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class Control extends AppCompatActivity {

    private Toolbar barra;

    private TextView textoConSinAgua;
    private ImageView iconoConSinAgua;
    private ImageButton encenderButton;

    private void setDefaults() {
        iconoConSinAgua.setImageResource(R.drawable.warning);
        encenderButton.setImageResource(R.drawable.mate_no_disponible);
        encenderButton.setEnabled(false);

        textoConSinAgua.setTextSize(32.0f);
        textoConSinAgua.setText("Sin agua");
    }

    private void setListeners() {
        iconoConSinAgua.setOnClickListener(view -> {
            iconoConSinAgua.setImageResource(R.drawable.ready);
            iconoConSinAgua.setEnabled(false);
            encenderButton.setEnabled(true);
            encenderButton.setImageResource(R.drawable.mate);
            textoConSinAgua.setTextSize(24.0f);
            textoConSinAgua.setText("Hay agua!\nYa se puede\ncomenzar");
        });

        encenderButton.setOnClickListener(view -> {
            siguienteActivity(view);
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent intent = getIntent();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control);

        barra = (Toolbar) findViewById(R.id.barra);

        textoConSinAgua = (TextView) findViewById(R.id.textoTemperatura);
        iconoConSinAgua = (ImageView) findViewById(R.id.imagenIconoAgua);
        encenderButton = (ImageButton) findViewById(R.id.botonDetener);



        setDefaults();
        setListeners();
    }

    private void siguienteActivity(View view) {
        Intent calentar = new Intent(view.getContext(), Calentando.class);
        //buscarDispositivos.putExtra("key", value);
        view.getContext().startActivity(calentar);
    }
}
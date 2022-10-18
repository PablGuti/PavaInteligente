package com.example.pavainteligente;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class PantallaPrincipal extends AppCompatActivity {

    private TextView titulo;
    private Button activarDesactivar;
    private Button buscar;

    private void setListeners() {
        activarDesactivar.setOnClickListener(view -> {
            buscar.setEnabled(!buscar.isEnabled());
            activarDesactivar.setText(buscar.isEnabled() ? "Desactivar Bluetooth" : "Activar Bluetooth");
        });

        buscar.setOnClickListener(view -> {
            Intent buscarDispositivos = new Intent(PantallaPrincipal.this, Dispositivos.class);
            //buscarDispositivos.putExtra("key", value);
            PantallaPrincipal.this.startActivity(buscarDispositivos);
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantalla_principal);

        titulo = (TextView) findViewById(R.id.TituloTextView);
        activarDesactivar = (Button) findViewById(R.id.ActivarDesactivarButton);
        buscar = (Button) findViewById(R.id.BuscarButton);

        buscar.setEnabled(false);

        setListeners();
    }
}
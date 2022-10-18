package com.example.pavainteligente;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import java.util.LinkedList;

public class Dispositivos extends AppCompatActivity {

    private Toolbar barra;
    private RecyclerView dispositivosEncontradosView;

    //private LinkedList<String> lista = new LinkedList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dispositivos);

        Intent intent = this.getIntent();

        barra = (Toolbar) findViewById(R.id.Barra);
        dispositivosEncontradosView = (RecyclerView) findViewById(R.id.ListaDispositivosView);
        RecyclerView.LayoutManager manager = new LinearLayoutManager(this);
        dispositivosEncontradosView.setLayoutManager(manager);
        DividerItemDecoration divisor = new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL);
        dispositivosEncontradosView.addItemDecoration(divisor);

        String[] cosas = {"uno", "dos", "tres", "cuatro"};

        dispositivosEncontradosView.setAdapter(new DispositivosAdapter(cosas));
    }
}
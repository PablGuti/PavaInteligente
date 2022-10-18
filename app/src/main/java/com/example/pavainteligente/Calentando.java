package com.example.pavainteligente;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

public class Calentando extends AppCompatActivity {

    private TextView temperatura;
    private ImageView iconoTemperatura;
    private ImageButton botonDetener;

    //test
    private SeekBar simuladorTemperatura;

    private void setDefaults() {
        temperatura.setTextSize(128.0f);
        temperatura.setText("25°");

        iconoTemperatura.setImageResource(R.drawable.frio);

        botonDetener.setImageResource(R.drawable.detener);

        //test
        simuladorTemperatura.setProgress(0);
        simuladorTemperatura.setMax(75);
    }

    private void setListeners() {

        //test
        simuladorTemperatura.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                temperatura.setText(Integer.toString(seekBar.getProgress() + 25, 10) + "°");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int temperaturaSimulada = seekBar.getProgress() + 25;
                temperatura.setText(Integer.toString(temperaturaSimulada, 10) + "°");
                if (temperaturaSimulada <= 30) {
                    iconoTemperatura.setImageResource(R.drawable.frio);
                } else if (temperaturaSimulada <= 75) {
                    iconoTemperatura.setImageResource(R.drawable.ideal);
                } else {
                    iconoTemperatura.setImageResource(R.drawable.caliente);
                }
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calentando);

        temperatura = (TextView) findViewById(R.id.textoTemperatura);
        iconoTemperatura = (ImageView) findViewById(R.id.imagenTemperatura);
        botonDetener = (ImageButton) findViewById(R.id.botonDetener);

        //test
        simuladorTemperatura = (SeekBar) findViewById(R.id.simuladorTemperatura);

        setDefaults();
        setListeners();
    }
}
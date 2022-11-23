package com.example.pavainteligente;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.appcompat.app.AppCompatActivity;


public class DescriptionActivity extends AppCompatActivity implements Contract.ViewMVP {

    private Contract.PresenterMVP presenter;
    private Pava elemento;
    TextView titleDescriptionTextView;
    TextView houseDescriptionTextView;
    TextView statusDescriptionTextView;
    TextView tempDescriptionTextView;
    ImageView iconImageView;
    ToggleButton switchButton;
    Double temperatura;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_description);
        presenter = new Presenter(this);
        elemento = presenter.getPava();
        titleDescriptionTextView = findViewById(R.id.titleDescriptionTextView);
        houseDescriptionTextView = findViewById(R.id.houseDescriptionTextView);
        statusDescriptionTextView = findViewById(R.id.statusDescriptionTextView);
        tempDescriptionTextView = findViewById(R.id.tempDescriptionTextView);
        iconImageView = findViewById(R.id.iconImageView);
        switchButton = findViewById(R.id.switchButton);

        titleDescriptionTextView.setText(elemento.getName());
        houseDescriptionTextView.setText(elemento.getHouse());
        statusDescriptionTextView.setText(elemento.getStatus());
        switchButton.setOnClickListener(btnPavaListener);
        setImagen();

    }

    @Override
    public void onPause() {
        super.onPause();
        if (presenter.validarBluetoothEncendidoPresenter()) {
            switchButton.setEnabled(true);
        } else {
            switchButton.setEnabled(false);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if(presenter.validarBluetoothEncendidoPresenter()){
            switchButton.setEnabled(true);
        }else{
            switchButton.setEnabled(false);
        }
    }

    public void onDestroy() {
        super.onDestroy();
        presenter.onDestroy();
    }

    private View.OnClickListener btnPavaListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(presenter.validarBluetoothEncendidoPresenter()){
                presenter.onButtonClick(DescriptionActivity.this);
            }
        }

    };

    private void setImagen(){
        if(statusDescriptionTextView.getText().equals("Desconectado")) {
            switchButton.setEnabled(false);
            tempDescriptionTextView.setText("--");
            iconImageView.setImageResource(R.drawable.humidity_low_fill0_wght400_grad0_opsz48);
        } else {

            temperatura = elemento.getTemperature();
            tempDescriptionTextView.setText(String.format("%.2f", temperatura));

            if (temperatura > Constants.TEMPERATURA_MEDIA && temperatura < Constants.TEMPERATURA_ALTA) {
                iconImageView.setImageResource(R.drawable.temp_preferences_custom_fill0_wght400_grad0_opsz48);
            } else if (temperatura > Constants.TEMPERATURA_ALTA) {
                iconImageView.setImageResource(R.drawable.emergency_heat_fill0_wght400_grad0_opsz48);
            } else {
                iconImageView.setImageResource(R.drawable.ac_unit_fill0_wght400_grad0_opsz48);
            }
        }
    }

    @Override
    public void cambiarColorR() {
    }

    @Override
    public void cambiarColorB() {
    }

    @Override
    public void cambiarColorY() {
    }

    @Override
    public void cambiarBarra(int valor) {
    }


    @Override
    public void notificarView(String string) {
        Toast.makeText(getBaseContext(), string, Toast.LENGTH_SHORT).show();
    }

    @Override
    public Pava setString(Pava element) {
        elemento = element;
        tempDescriptionTextView.setText(String.format("%.2f", element.getTemperature()));
        setImagen();
        return element;
    }


}
package com.example.pavainteligente;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

public class DescriptionActivity extends AppCompatActivity {
    final static double TEMPERATURA_MEDIA = 197.26;
    final static double TEMPERATURA_ALTA = 389.45;

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

        ListElement element = (ListElement) getIntent().getSerializableExtra("ListElement");

        titleDescriptionTextView = findViewById(R.id.titleDescriptionTextView);
        houseDescriptionTextView = findViewById(R.id.houseDescriptionTextView);
        statusDescriptionTextView = findViewById(R.id.statusDescriptionTextView);
        tempDescriptionTextView = findViewById(R.id.tempDescriptionTextView);
        iconImageView = findViewById(R.id.iconImageView);
        switchButton = findViewById(R.id.switchButton);

        titleDescriptionTextView.setText(element.getName());
        houseDescriptionTextView.setText(element.getHouse());
        statusDescriptionTextView.setText(element.getStatus());
        switchButton.setChecked(element.getSwitchStatus());

        if(statusDescriptionTextView.getText().equals("Desconectado")) {
            switchButton.setEnabled(false);
            tempDescriptionTextView.setText("--");
            iconImageView.setImageResource(R.drawable.humidity_low_fill0_wght400_grad0_opsz48);
        } else {
            temperatura = element.getTemperature();
            Double voltaje = (5.0 / 1024 * temperatura);
            temperatura = voltaje * 100 - 50;
            tempDescriptionTextView.setText(String.format("%.2f", temperatura));

            if (temperatura > TEMPERATURA_MEDIA && temperatura < TEMPERATURA_ALTA) {
                iconImageView.setImageResource(R.drawable.temp_preferences_custom_fill0_wght400_grad0_opsz48);
            } else if (temperatura > TEMPERATURA_ALTA) {
                iconImageView.setImageResource(R.drawable.emergency_heat_fill0_wght400_grad0_opsz48);
            } else {
                iconImageView.setImageResource(R.drawable.ac_unit_fill0_wght400_grad0_opsz48);
            }
        }
    }

}
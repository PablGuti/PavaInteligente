package com.example.pavainteligente;

import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class ShakeDetector extends Service implements SensorEventListener {

    private final static float ACC = 10;
    private SensorManager sensor;
    private boolean isChecked; //Lo utilizo temporal para probar


    @Override
    public void onCreate(){
        super.onCreate();
        Toast.makeText(this, "Servicio creadoooooooooooooooooooooooooooo!", Toast.LENGTH_SHORT).show();
        Log.i(getClass().getSimpleName(), "servicio creadooooooooooooooooooooooooooooooooooooooooooooooo");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        Log.i(getClass().getSimpleName(), "Intent received");


        sensor = (SensorManager) getSystemService(SENSOR_SERVICE);
        //sensor.registerListener(this,sensor.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
        registerSenser();
        return START_NOT_STICKY;

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onSensorChanged(SensorEvent event)
    {

        int sensorType = event.sensor.getType();

        float[] values = event.values;

        if (sensorType == Sensor.TYPE_ACCELEROMETER)
        {
            if ((Math.abs(values[0]) > ACC || Math.abs(values[1]) > ACC || Math.abs(values[2]) > ACC))
            {
                Log.i("shake","se genero un shake ");
                //DescriptionActivity.setBoton();
                Intent intent2 = new Intent();
                intent2.setAction("com.example.pavainteligente.UPDATE");
                sendBroadcast(intent2);
                onCheckedChanged();
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }


    public void onCheckedChanged()
    {
        if (isChecked)
        {
            registerSenser();
        }
        else
        {
            unregisterSenser();
        }
    }

    private void registerSenser()
    {
        boolean done;
        done = sensor.registerListener(this, sensor.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
    }

    private void unregisterSenser()
    {
        sensor.unregisterListener(this);
    }

}

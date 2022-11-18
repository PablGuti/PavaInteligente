package com.example.pavainteligente;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class PresenterMain implements ContratoMain.ModelMain.OnSendToPresenter,ContratoMain.PresenterMain{

    private ContratoMain.ViewMain mView;
    private final ContratoMain.ModelMain model;
    private MainActivity mainView1;
    private boolean conexion = false;
    private SensorManager mSensormanager;
    private Sensor acelerometro;
    private double valorAceleracion;
    private double valorAceleraciondespues;


    public PresenterMain(ContratoMain.ViewMain mainView){
        this.mView = mainView;
        this.mainView1 = (MainActivity) mainView;
         this.model= new ModelMain(this,this.mView);
        setUpSensormanager();
        listener();
    }

    public PresenterMain(ContratoMain.ModelMain model) {
        this.model = model;
    }


    @Override
    public void onFinished(Pava element) {

        this.mView.setString(element);
    }

    public void onDestroy() {

        this.mView = null;
    }

    @Override
    public void onpausa() {

        mSensormanager.unregisterListener(sensorEventListener);
    }

    @Override
    public void setUpSensormanager() {
        mSensormanager = (SensorManager) this.mainView1.getSystemService(Context.SENSOR_SERVICE);
        acelerometro=mSensormanager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }

    @Override
    public void listener() {
        mSensormanager.registerListener(sensorEventListener, acelerometro, SensorManager.SENSOR_DELAY_NORMAL);

    }
    private SensorEventListener sensorEventListener=new SensorEventListener() {


        @Override
        public void onSensorChanged(SensorEvent event) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            valorAceleracion=Math.sqrt(x*x+y*y+z*z);
            double cambiandoAceleracion=Math.abs(valorAceleracion-valorAceleraciondespues);
            valorAceleraciondespues=valorAceleracion;
            mainView1.cambiarBarra((int)cambiandoAceleracion);
            if(cambiandoAceleracion>5) {
                mainView1.cambiarColorR();
            } else if(cambiandoAceleracion>3){
                mainView1.cambiarColorB();


            }
            else if(cambiandoAceleracion>2){
                mainView1.cambiarColorY();


            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };
}

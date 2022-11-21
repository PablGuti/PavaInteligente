package com.example.pavainteligente;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class Presenter  implements Contract.ModelMVP.OnSendToPresenter, Contract.PresenterMVP{

    private Contract.ViewMVP mView;
    private MainActivity mainView1;
    private final Contract.ModelMVP model;
    private boolean conexion = false;
    private SensorManager mSensormanager;
    private Sensor acelerometro;
    private double valorAceleracion;
    private double valorAceleraciondespues;

    public Presenter(Contract.ViewMVP mainView, String main){
        this.mView = mainView;
        this.model = new Model(this, this.mView);
        this.mainView1 = (MainActivity) mainView;
        setUpSensormanager();

    }

    public Presenter(Contract.ViewMVP mainView){
        this.mView = mainView;
        this.model = new Model(this, this.mView);
    }


    @Override
    public void onFinished(Pava element) {
        this.mView.setString(element);
    }

    @Override
    public void onButtonClick(Context context) {
        this.model.sendMessage( this);
    }

    @Override
    public void notificar(String mensaje) {
        this.mView.notificarView(mensaje);
    }


    public void onDestroy() {
        this.model.desconectar();
        this.mView = null;
    }

    @Override
    public boolean validarBluetoothEncendidoPresenter() {
        if(this.model.validarBluetoothEncendido( this)){
            if(this.model.validarConexion()){
                return true;
            }
        }
        return false;
    }

    @Override
    public Pava getPava() {
        return this.model.obtenerPava();
    }

    @Override
    public void setUpSensormanager() {
        mSensormanager = (SensorManager) this.mainView1.getSystemService(Context.SENSOR_SERVICE);
        acelerometro=mSensormanager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        listener();
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

    @Override
    public void onpausa() {
        mSensormanager.unregisterListener(sensorEventListener);
    }
}

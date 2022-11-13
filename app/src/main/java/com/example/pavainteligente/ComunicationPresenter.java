package com.example.pavainteligente;

import android.bluetooth.BluetoothSocket;
import android.content.Context;

import java.io.IOException;

public class ComunicationPresenter implements ComunicationContract.Presenter, ComunicationContract.Model.OnUpdateListener {

    private ComunicationContract.View view;
    private ComunicationModel Model;

    public ComunicationPresenter(BluetoothSocket socket, ComunicationContract.View view) throws IOException {
        this.view = view;
        Model = new ComunicationModel(socket, this);
    }

    @Override
    public void onTemperaturaChanged(String temperatura) {

    }

    @Override
    public void onSinHumedad() {

    }

    @Override
    public void onEncendido() {

    }

    @Override
    public void onApagado() {

    }

    @Override
    public void onDesconexion() {

    }

    @Override
    public void onEncenderClicked() {

    }

    @Override
    public void onApagarClicked() {

    }

    @Override
    public void showMensajeError(String mensaje) {
        view.showMensajeError(mensaje);
    }

    @Override
    public void onButtonClick(Context context) {

    }

    @Override
    public void onDestroy() {

    }
}

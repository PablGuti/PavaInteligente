package com.example.pavainteligente;

import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Handler;

import java.io.IOException;
import java.io.OutputStream;

public class ComunicationModel implements ComunicationContract.Model {

    private ComunicationContract.Model.OnUpdateListener listener;
    private BluetoothSocket socket;
    private HiloSalida salida;

    public ComunicationModel(BluetoothSocket socket, ComunicationContract.Model.OnUpdateListener listener) throws IOException {
        this.listener = listener;
        this.socket = socket;
        salida = new HiloSalida(socket, listener);
    }

    @Override
    public void encender() {

    }

    @Override
    public void apagar() {

    }

    @Override
    public void onDestroy() {

    }

    @Override
    public void sendMessage(OnSendToPresenter presenter, Context context) {

    }

    @Override
    public void conectar(OnSendToPresenter presenter) {

    }
}

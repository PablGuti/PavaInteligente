package com.example.pavainteligente;

import android.bluetooth.BluetoothSocket;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class HiloSalida extends Thread {

    private final char MENSAJE = 't';
    private final int MS_ENTRE_ENVIOS = 5000;
    private OutputStream salida;
    private InputStream entrada;
    private ComunicationContract.Model.OnUpdateListener listener;

    public HiloSalida(BluetoothSocket socket, ComunicationContract.Model.OnUpdateListener listener) throws IOException {
        salida = socket.getOutputStream();
        entrada = socket.getInputStream();
        this.listener = listener;
    }

    @Override
    public void run() {
        try {
            enviarMensaje();
            while(!isInterrupted()) {
                /*
                    la idea del sleep es evitar la espera activa
                    que no llene de mensajes al socket del arduino
                    y que se pueda interrumpir el hilo
                 */
                Thread.sleep(MS_ENTRE_ENVIOS);
                enviarMensaje();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private synchronized void enviarMensaje() throws IOException {
        salida.write(MENSAJE);
        new HiloEntrada(entrada, listener).start();
    }
}

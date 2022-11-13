package com.example.pavainteligente;

import android.os.Build;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class HiloEntrada extends Thread {

    private final int BUFFER_LENGTH = 2;
    private byte[] buffer;
    private String temperatura;

    private InputStream entrada;
    private ComunicationContract.Model.OnUpdateListener listener;

    //la idea del listener es que el model implemente el OnUpdateListener para pasarlo como this
    public HiloEntrada(InputStream entrada, ComunicationContract.Model.OnUpdateListener listener) {
        this.entrada = entrada;
        this.listener = listener;
        this.buffer = new byte[BUFFER_LENGTH];
    }

    @Override
    public void run() {
        int contador = 0;
        try {
            while (entrada.available() == 0 && contador < 8) {
                //la idea del sleep es evitar la espera activa
                //si no hago que tenga este timeout el read podria blockear e impedirme cerrar el socket
                Thread.sleep(500);
                contador++;
            }
            entrada.read(buffer, 0, 1); //lee un solo byte para saber que tipo de mensaje es
        } catch (IOException | InterruptedException e) {
            buffer[0] = 'e';
        }
        handleBuffer();
    }

    private synchronized void handleBuffer() {
        switch (buffer[0]) {
            case 't': //temperatura
                try {
                    entrada.read(buffer, 0, BUFFER_LENGTH); //lee 2 bytes que deberian tener la temperatura

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        temperatura = new String(buffer, StandardCharsets.UTF_8);
                    } else {
                        temperatura = Arrays.toString(buffer); //dudoso pero necesito que temperatura tenga algo incluso si la version no me deja
                    }
                    listener.onTemperaturaChanged(temperatura);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case 'h': //humedad
                listener.onSinHumedad();
                break;
            case 'c': //encendido
                listener.onEncendido();
                break;
            case 'a': //apagado
                listener.onApagado();
                break;
            case 'e': //IOException
                listener.onDesconexion();
                break;
            default:
                Log.println(Log.WARN, "HiloEntrada", "llego un mensaje con " + buffer[0] + " y no supe que hacer");
                break;
        }
    }
}

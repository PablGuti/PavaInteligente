package com.example.pavainteligente;

import android.content.Context;

public interface ComunicationContract {

    interface View extends Contract.ViewMVP {
        void updateTemperatura(String temperatura);
        void showSinAgua();
        void showConAgua();
        void showDesconectado();
        void showEncendido();
        void showApagado();
        void showMensajeError(String mensaje);

        Context getContext();
    }

    interface Model extends Contract.ModelMVP {
        void encender();
        void apagar();

        void onDestroy(); //porque va a tener los hilos y el socket

        interface OnUpdateListener {
            public void onTemperaturaChanged(String temperatura);
            public void onSinHumedad();
            public void onEncendido();
            public void onApagado();
            public void onDesconexion();
        }
    }

    interface Presenter extends Contract.PresenterMVP {
        void onEncenderClicked();
        void onApagarClicked();
        void showMensajeError(String mensaje);

        @Override
        void onDestroy();
    }
}

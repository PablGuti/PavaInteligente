package com.example.pavainteligente;

import android.content.Context;

public interface Contract {

    interface ViewMVP{
        void cambiarColorR();
        void cambiarColorB();
        void cambiarColorY();
        void cambiarBarra(int valor);

        void notificarView(String string);
        Pava setString(Pava element);
    }

    interface ModelMVP{
        Pava obtenerPava();

        interface OnSendToPresenter{
            void onFinished(Pava element);
        }

        boolean validarBluetoothEncendido(Presenter presenter);
        boolean validarConexion();
        void sendMessage(Contract.ModelMVP.OnSendToPresenter presenter);
        void desconectar();
    }

    interface PresenterMVP{
        void onButtonClick(Context context);
        void notificar(String string);
        void onDestroy();
        boolean validarBluetoothEncendidoPresenter();
        void setUpSensormanager();
        void listener();
        void onpausa();
        Pava getPava();
    }

}

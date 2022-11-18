package com.example.pavainteligente;

import android.content.Context;

public interface ContratoMain {

    interface ViewMain{
        void cambiarColorR();
        void cambiarColorB();
        void cambiarColorY();
        void cambiarBarra(int valor);
        void onItemClick(Pava item);

        void lanzar(Context mainView);

        Pava setString(Pava element);
    }

    interface ModelMain{
        interface OnSendToPresenter{
            void onFinished(Pava element);
        }

    }

    interface PresenterMain{
        void onDestroy();
        void onpausa();
        void setUpSensormanager();
        void listener();

    }
}

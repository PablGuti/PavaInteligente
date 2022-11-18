package com.example.pavainteligente;

import android.content.Context;

public interface Contract {

    interface ViewMVP{
        void cambiarColorR();
        void cambiarColorB();
        void cambiarColorY();
        void cambiarBarra(int valor);
        void onItemClick(Pava item);

        void lanzar(Context mainView);

        Pava setString(Pava element);
    }

    interface ModelMVP{
        interface OnSendToPresenter{
            void onFinished(Pava element);
        }
        void sendMessage(Contract.ModelMVP.OnSendToPresenter presenter,Context context);
        void desconectar();
    }

    interface PresenterMVP{
        void onButtonClick(Context context);
        void onDestroy();


    }

}

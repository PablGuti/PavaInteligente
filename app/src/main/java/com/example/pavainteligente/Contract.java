package com.example.pavainteligente;

import android.content.Context;

public interface Contract {

    interface ViewMVP{
        void onItemClick(ListElement item);

        void lanzar(Context mainView);

        ListElement setString(ListElement element);
    }

    interface ModelMVP{
        interface OnSendToPresenter{
            void onFinished(ListElement element);
        }
        void sendMessage(Contract.ModelMVP.OnSendToPresenter presenter,Context context);

        void conectar(Contract.ModelMVP.OnSendToPresenter presenter);
    }

    interface PresenterMVP{
        void onButtonClick(Context context);
        void onDestroy();


    }

}

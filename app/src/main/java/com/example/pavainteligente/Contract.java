package com.example.pavainteligente;

public interface Contract {

    interface ViewMVP{
        void setString(String string);
    }

    interface ModelMVP{
        interface onSendToPresenter{
            void onFinished(String string);
        }
        void sendMessage(Contract.ModelMVP.onSendToPresenter presenter);
    }

    interface PresenterMVP{
        void onButtonClick();
        void onDestroy();
    }

}

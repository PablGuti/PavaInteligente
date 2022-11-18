package com.example.pavainteligente;

import android.content.Context;

public class Presenter  implements Contract.ModelMVP.OnSendToPresenter, Contract.PresenterMVP{

    private Contract.ViewMVP mView;
    private final Contract.ModelMVP model;
    private boolean conexion = false;


    public Presenter(Contract.ViewMVP mainView){
        this.mView = mainView;
        this.model = new Model(this, this.mView);
    }

    @Override
    public void onFinished(Pava element) {
        this.mView.setString(element);
    }

    @Override
    public void onButtonClick(Context context) {
        this.model.sendMessage( this,context);
    }


    public void onDestroy() {
        this.model.desconectar();
        this.mView = null;
    }

}

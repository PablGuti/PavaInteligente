package com.example.pavainteligente;

import android.app.Activity;
import android.content.Context;

public class Presenter extends Activity implements Contract.ModelMVP.OnSendToPresenter, Contract.PresenterMVP{

    private Contract.ViewMVP view;
    private final Contract.ModelMVP model;
    private boolean conexion = false;


    public Presenter(Contract.ViewMVP mainView){
        this.view = mainView;
        this.model = new Model();
    }


    @Override
    public void onFinished(ListElement element) {

        this.view.setString(element);
    }

    @Override
    public void onButtonClick(Context context) {
        this.model.sendMessage( this,context);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.view = null;
    }




}

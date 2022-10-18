package com.example.pavainteligente;

public class Presenter implements Contract.ModelMVP.OnSendToPresenter, Contract.PresenterMVP{

    private Contract.ViewMVP mainView;
    private final Contract.ModelMVP model;

    public Presenter (Contract.ViewMVP mainView){
        this.mainView = mainView;
        this.model = new  Model();
    }

    @Override
    public void onFinished(String string) {
        this.mainView.setString(string);
    }

    @Override
    public void onButtonClick() {
        this.model.sendMessage(this);
    }

    @Override
    public void onDestroy() {
        this.mainView = null;
    }
}

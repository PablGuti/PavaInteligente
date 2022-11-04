package com.example.pavainteligente;

public class Presenter implements Contract.ModelMVP.onSendToPresenter, Contract.PresenterMVP{

    private Contract.ViewMVP mainView;
    private final Contract.ModelMVP model;

    public Presenter(Contract.ViewMVP mainView) {
        this.mainView = mainView;
        this.model = new Model();
    }

    @Override
    public void onButtonClick() {

    }

    @Override
    public void onDestroy() {
        this.mainView = null; // Anula la conexion con la vista
    }

    @Override
    public void onFinished(String string) {

    }
}

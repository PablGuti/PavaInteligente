package com.example.pavainteligente;

import android.annotation.SuppressLint;
import android.content.Context;

public class ModelMain implements ContratoMain.ModelMain{
    private final PresenterMain presenter;
    private ContratoMain.ViewMain view;

    @SuppressLint("MissingPermission")
    public ModelMain(PresenterMain presenter, ContratoMain.ViewMain view) {
        this.presenter = presenter;
        this.view = view;

    }


}

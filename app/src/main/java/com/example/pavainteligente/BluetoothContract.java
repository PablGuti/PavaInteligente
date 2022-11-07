package com.example.pavainteligente;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.Context;

import java.util.ArrayList;

public interface BluetoothContract extends Contract {

    interface View extends Contract.ViewMVP {
        void habilitarBotones();
        void deshabilitarBotones();
        void mostrarProgressDialog();
        void ocultarProgressDialog();
        void showUnsupported();
        void showToast(String message);
        Context getContext();
        Activity getViewActivity();
    }

    interface Model extends Contract.ModelMVP {
        void addDispositivo(BluetoothDevice dispositivoNuevo);
        ArrayList<BluetoothDevice> getListaDispositivos();
        void setListaDispositivos(ArrayList<BluetoothDevice> dispositivos);
        String[] getPermissions();
    }

    interface Presenter extends Contract.PresenterMVP {
        void onClickActivarDesactivar();
        void onClickEmparejar();
        void onClickBuscar();
        void onCancelDiscovery();
        void onResultadoPermisos(int requestCode, String[] permissions, int[] grantResults);
        void onPause();

        void addDispositivo(BluetoothDevice dispositivoNuevo);
        ArrayList<BluetoothDevice> getDispositivosEncontrados();
        void setDispositivosEncontrados(ArrayList<BluetoothDevice> dispositivosEncontrados);
    }
}

package com.example.pavainteligente;

import static androidx.core.content.ContextCompat.checkSelfPermission;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;


import androidx.core.app.ActivityCompat;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class Model extends Thread implements Contract.ModelMVP {

    private Pava element;
    private Handler bluetoothIn;
    private int handlerState = 0;
    private BluetoothAdapter btAdapter;
    private BluetoothSocket btSocket;
    private StringBuilder recDataString = new StringBuilder();
    private InputStream mBTInputStream = null;
    private OutputStream mBTOutputStream = null;
    private ConnectedThread mConnectedThread;
    private Presenter presenter;
    private Contract.ViewMVP view;
    private BluetoothDevice device;
    private final String[] permissions = new String[]{
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.BLUETOOTH_ADVERTISE,
            Manifest.permission.ACCESS_FINE_LOCATION,
    };


    @SuppressLint("MissingPermission")
    public Model(Presenter presenter, Contract.ViewMVP view) {
        this.presenter = presenter;
        this.view = view;
        checkPermissions();
        this.element = obtenerPava();
    }

    @Override
    public void sendMessage(OnSendToPresenter presenter) {
        if (!element.getSwitchStatus()) {
            try {
                inicializaBluetooth();
            } catch (IOException e) {
                e.printStackTrace();
            }
            encender();
        } else {
            apagar();
        }
        presenter.onFinished(element);
    }


    @Override
    public boolean validarBluetoothEncendido(Presenter presenter){
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!btAdapter.isEnabled()){
            presenter.notificar("No esta encendido el bluetooth");
            return false;
        }
        return true;
    }

    @SuppressLint("MissingPermission")
    public boolean validarConexion() {
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        device = btAdapter.getRemoteDevice(Constants.address);

        for (BluetoothDevice d : btAdapter.getBondedDevices()) {
            if (d.getAddress().equals(Constants.address)) {
                return true;
            }
        }
        presenter.notificar("No esta conectado a el HC-05 correcto");
        return false;
    }


    public boolean verificarConexion() {
        if (btSocket==null) {
            return false;
        } else
            return true;
    }

    public boolean estadoPava() {
        if (verificarConexion() && btSocket.isConnected()) {
            return true;
        } else
            return false;
    }


    @SuppressLint("MissingPermission")
    private void inicializaBluetooth() throws IOException {
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        device = btAdapter.getRemoteDevice(Constants.address);
        bluetoothIn = Handler_Msg_Hilo_Principal();
        btAdapter.cancelDiscovery();
        btSocket = device.createRfcommSocketToServiceRecord(Constants.BTMODULEUUID);
        int i = 0;
        do {
            try {
                btSocket.connect();
            } catch (IOException e) {
                btSocket.close();
            }
            i++;
        }while (!btSocket.isConnected() && i<3);
        mConnectedThread = new ConnectedThread(btSocket);
        mConnectedThread.start();
    }

    public void cerrarSocket() throws IOException {
        if(estadoPava()){
            btSocket.close();
        }
    }


    @SuppressLint("HandlerLeak")
    private Handler Handler_Msg_Hilo_Principal ()
    {
        return new Handler() {
            public void handleMessage(android.os.Message msg)
            {

                if (msg.what == handlerState)
                {
                    String readMessage = (String) msg.obj;
                    recDataString.append(readMessage);
                    int endOfLineIndex = recDataString.indexOf("\r\n");

                    if (endOfLineIndex > 0)
                    {
                        String dataInPrint = recDataString.substring(0, endOfLineIndex);
                        element.setTemperature(Double.parseDouble(dataInPrint));
                        presenter.onFinished(element);
                        recDataString.delete(0, recDataString.length());
                    }
                }
            }
        };

    }

    public void encender(){
        element.setSwitchStatus(true);
        mConnectedThread.write("c");
    }

    public void apagar(){
        element.setSwitchStatus(false);
        mConnectedThread.write("a");
        try {
            cerrarSocket();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }




    private class ConnectedThread extends Thread {
        public ConnectedThread(BluetoothSocket socket) {
            InputStream tmpIn = null;
            OutputStream tmpOut = null;
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
            }

            mBTInputStream = tmpIn;
            mBTOutputStream = tmpOut;
        }


        public void run() {
            byte[] buffer = new byte[5];
            int bytes;


            while (true) {
                try {
                    bytes = mBTInputStream.read(buffer);
                    String readMessage = new String(buffer, 0, bytes);
                    bluetoothIn.obtainMessage(handlerState,bytes,-1,readMessage).sendToTarget();
                } catch (IOException e) {
                    break;
                }
            }
        }

        public void write(String input) {
            byte[] msgBuffer = input.getBytes();
            try {
                mBTOutputStream.write(msgBuffer);
            } catch (IOException e) {

            }
        }
    }


    @Override
    public void desconectar() {
        if(estadoPava()){
            apagar();
            try {
                cerrarSocket();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private  boolean checkPermissions() {
        int result;
        List<String> listPermissionsNeeded = new ArrayList<>();

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }

        for (String p:permissions) {
            result = checkSelfPermission((Context) view,p);
            if (result != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(p);
            }
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions((Activity) view, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), 12);
            return false;
        }
        return true;
    }

    public Pava obtenerPava(){
        return new Pava("#FF0000", "Pava 1", "Lab 266", "No disponible", 0.0, false);
    }
}

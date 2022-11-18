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
import android.util.Log;

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
    private int aux = 0;
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
        //validarConexion();
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
    private boolean validarConexion() {
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        //address = "00:21:11:01:B7:6E";//extras.getString("Direccion_Bluethoot"); //MAC 35:0B:68:DA:0A:2F
        device = btAdapter.getRemoteDevice(Constants.address);

        ArrayList<BluetoothDevice> devices = new ArrayList<BluetoothDevice>();

        //Obtenemos todos los dispositivos bluetooth
        //vinculados
        for (BluetoothDevice d : btAdapter.getBondedDevices()) {
            if (d.getAddress().equals(Constants.address)) {
                devices.add(d);
            }
        }
        return true;
    }


    public Model(PresenterMain presenterMain, ContratoMain.ViewMain mView) {
    }
    // Closes the client socket and causes the thread to finish.
    public void cancel() {
        try {
            btSocket.close();
        } catch (IOException e) {
            Log.e("ERROR", "Could not close the client socket", e);
        }
    }



    @Override
    public void sendMessage(OnSendToPresenter presenter) {
        if (!verificarConexion() && aux<2) {
            System.out.println("soy el aux !!!!!!-......................");
            System.out.println(aux);
            try {
                aux=2;
                inicializaBluetooth();
            } catch (IOException e) {
                e.printStackTrace();
            }
            encender();

        } else {
                aux=0;
                apagar();
                element.switchStatus = false;
        }
        presenter.onFinished(element);
    }

    public boolean verificarConexion() {
        if (btSocket==null) {
            return false;
        } else
            return true;
    }

    public boolean estadoPava() {
        if (btSocket.isConnected()) {
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
        }while (!btSocket.isConnected() && i<5);
        mConnectedThread = new ConnectedThread(btSocket);
        mConnectedThread.start();
    }

    //Llamar cuando se ejecute el onPause en la view
    public void cerrarSocket() throws IOException {
        btSocket.close();
    }


    @SuppressLint("HandlerLeak")
    private Handler Handler_Msg_Hilo_Principal ()
    {
        return new Handler() {
            public void handleMessage(android.os.Message msg)
            {
                //si se recibio un msj del hilo secundario
                if (msg.what == handlerState)
                {
                    //voy concatenando el msj
                    String readMessage = (String) msg.obj;
                    recDataString.append(readMessage);
                    int endOfLineIndex = recDataString.indexOf("\r\n");

                    //cuando recibo toda una linea la muestro en el layout
                    if (endOfLineIndex > 0)
                    {
                        String dataInPrint = recDataString.substring(0, endOfLineIndex);
                        //txtPotenciometro.setText(dataInPrint);
                        element.setTemperature(Double.parseDouble(dataInPrint));
                        presenter.onFinished(element);
                        recDataString.delete(0, recDataString.length());
                    }
                }
            }
        };

    }

    public void encender(){
        element.switchStatus = true;
        mConnectedThread.write("c");
    }

    public void apagar(){
        element.switchStatus = false;
        mConnectedThread.write("a");
    }



    /////////////////////////////////////CLASES SOPORTE PARA CONECTAR BLUETOOTH//////////////////////////////////////
    private class ConnectedThread extends Thread {

        //Constructor de la clase del hilo secundario
        public ConnectedThread(BluetoothSocket socket) {
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            try {
                //Create I/O streams for connection
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
            }

            mBTInputStream = tmpIn;
            mBTOutputStream = tmpOut;
        }

        //metodo run del hilo, que va a entrar en una espera activa para recibir los msjs del HC05
        public void run() {
            byte[] buffer = new byte[5];
            int bytes;

            //el hilo secundario se queda esperando mensajes del HC05
            while (true) {
                try {
                    //se leen los datos del Bluethoot
                    bytes = mBTInputStream.read(buffer);
                    String readMessage = new String(buffer, 0, bytes);

                    bluetoothIn.obtainMessage(handlerState,bytes,-1,readMessage).sendToTarget();



                } catch (IOException e) {
                    break;
                }
            }
        }


        //write method
        public void write(String input) {
            byte[] msgBuffer = input.getBytes();           //converts entered String into bytes
            try {
                mBTOutputStream.write(msgBuffer);                //write bytes over BT connection via outstream
            } catch (IOException e) {
                //if you cannot write, close the application
                // showToast("La conexion fallo");
                //finish();
            }
        }
    }


    @Override
    public void desconectar() {
        apagar();
        try {
            cerrarSocket();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Metodo que chequea si estan habilitados los permisos
    private  boolean checkPermissions() {
        int result;
        List<String> listPermissionsNeeded = new ArrayList<>();


        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }

        for (String p:permissions) {
            //CustomPermission needed;//:BT_PERMISSIONS_NEEDED
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

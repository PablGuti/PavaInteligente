package com.example.pavainteligente;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.UUID;

public class Model extends Activity implements Contract.ModelMVP {

    private ListElement element = new ListElement("#FF0000", "Pava 1", "Lab 266", "Disponible", 920.01, false);
    private Handler bluetoothIn;
    final int handlerState = 0; //used to identify handler message
    private BluetoothAdapter btAdapter = null;
    private BluetoothSocket btSocket = null;
    private StringBuilder recDataString = new StringBuilder();
    private InputStream  mBTInputStream  = null;
    private OutputStream mBTOutputStream = null;
    private ConnectedThread mConnectedThread;

    // SPP UUID service  - Funciona en la mayoria de los dispositivos
    private static final UUID BTMODULEUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    // String for MAC address del Hc05
    private static String address = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void sendMessage(OnSendToPresenter presenter, Context context) {

        if(!verificarConexion()){

            inicializaBluetooth();
            encender();
            element.switchStatus = true;
        }else{

        }
        presenter.onFinished(element);
    }

    public boolean verificarConexion(){
        if(btSocket == null){
            return false;
        }else
            return true;
    }

    public boolean estadoPava(){
        if(btSocket.isConnected()){
            return true;
        }else
            return false;
    }

    @Override
    public void conectar(OnSendToPresenter presenter) {

    }

    @SuppressLint("MissingPermission")
    private void inicializaBluetooth() {

        ArrayList<BluetoothDevice> devices = new ArrayList<BluetoothDevice>();
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        if (btAdapter == null)
            return;

        //Obtenemos todos los dispositivos bluetooth
        //vinculados
        for (BluetoothDevice d : btAdapter.getBondedDevices()){
            if(d.getAddress().equals("00:21:11:01:b7:6e")){
                devices.add(d);
            }
        }

        bluetoothIn = Handler_Msg_Hilo_Principal();
        Intent intent = getIntent();
        //Bundle extras = intent.getExtras();
        //address = "35:0B:68:DA:0A:2F";//extras.getString("Direccion_Bluethoot"); //MAC 00:21:11:01:b7:6e
        //address ="00:21:11:01:b7:6e";

        BluetoothDevice device = devices.get(0);//btAdapter.getRemoteDevice(address);

        try {
            btSocket = createBluetoothSocket(device);
        } catch (IOException e) {
            showToast("La creacción del Socket fallo");
        }

        try {
            //resetConnection();
            btSocket.connect();
            mConnectedThread = new ConnectedThread(btSocket);
            mConnectedThread.start();
            mConnectedThread.write("c");

        } catch (IOException e) {
            try {
                showToast("Fallo la conexion");
                btSocket.close();
            } catch (IOException e2) {
                //insert code to deal with this
            }
        }
    }

    //Llamar cuando se ejecute el onPause en la view
    public void cerrarSocket() throws IOException {
        btSocket.close();
    }


    private synchronized void resetConnection() {
        if (mBTInputStream != null) {
            try {mBTInputStream.close();} catch (Exception e) {}
            mBTInputStream = null;
        }

        if (mBTOutputStream != null) {
            try {mBTOutputStream.close();} catch (Exception e) {}
            mBTOutputStream = null;
        }

        if (btSocket != null) {
            try {btSocket.close();} catch (Exception e) {}
            btSocket = null;
        }

    }

    //Metodo que crea el socket bluethoot
    @SuppressLint("MissingPermission")
    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {
        //if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
        //}
        return device.createRfcommSocketToServiceRecord(BTMODULEUUID);
    }

    //Handler que sirve que permite mostrar datos en el Layout al hilo secundario
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

                        recDataString.delete(0, recDataString.length());
                    }
                }
            }
        };

    }

    public void encender(){
        mConnectedThread.write("c");
    }

    public void apagar(){
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
            byte[] buffer = new byte[256];
            int bytes;

            //el hilo secundario se queda esperando mensajes del HC05
            while (true) {
                try {
                    //se leen los datos del Bluethoot
                    bytes = mBTInputStream.read(buffer);
                    String readMessage = new String(buffer, 0, bytes);

                    //se muestran en el layout de la activity, utilizando el handler del hilo
                    // principal antes mencionado
                    bluetoothIn.obtainMessage(handlerState, bytes, -1, readMessage).sendToTarget();
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
                finish();
            }
        }
    }

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }


}

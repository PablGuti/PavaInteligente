package com.example.pavainteligente;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Handler;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class Model extends Thread implements  Contract.ModelMVP {

    private ListElement element = new ListElement("#FF0000", "Pava 1", "Lab 266", "Disponible", 920.01, false);
    private Handler bluetoothIn;
    private final int handlerState = 0; //used to identify handler message
    private final BluetoothAdapter btAdapter;
    private BluetoothSocket btSocket;
    private StringBuilder recDataString = new StringBuilder();
    private InputStream mBTInputStream = null;
    private OutputStream mBTOutputStream = null;
    private ConnectedThread mConnectedThread;
    private final Presenter presenter;
    private final Contract.ViewMVP view;
    private final BluetoothDevice mmDevice;

    // SPP UUID service  - Funciona en la mayoria de los dispositivos
    private static final UUID BTMODULEUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    // String for MAC address del Hc05
    private static String address = null;

    @SuppressLint("MissingPermission")
    public Model(Presenter presenter, Contract.ViewMVP view) {
        this.presenter = presenter;
        this.view = view;
        btAdapter = BluetoothAdapter.getDefaultAdapter();

        address = "35:0B:68:DA:0A:2F";//extras.getString("Direccion_Bluethoot"); //MAC 00:21:11:01:B7:6E
        BluetoothDevice device = btAdapter.getRemoteDevice(address);// devices.get(0);//
            // Use a temporary object that is later assigned to mmSocket
            // because mmSocket is final.
            BluetoothSocket tmp = null;
            mmDevice = device;

            try {
                // Get a BluetoothSocket to connect with the given BluetoothDevice.
                // MY_UUID is the app's UUID string, also used in the server code.
                tmp = device.createRfcommSocketToServiceRecord(BTMODULEUUID);
            } catch (IOException e) {
                Log.e("ERROR", "Socket's create() method failed", e);
            }
            btSocket = tmp;
            run();
        }

    @SuppressLint("MissingPermission")
    public void run() {
        // Cancel discovery because it otherwise slows down the connection.
        btAdapter.cancelDiscovery();

        try {
            // Connect to the remote device through the socket. This call blocks
            // until it succeeds or throws an exception.
            btSocket.connect();
        } catch (IOException connectException) {
            // Unable to connect; close the socket and return.
            try {
                btSocket.close();
            } catch (IOException closeException) {
                Log.e("error", "Could not close the client socket", closeException);
            }
            return;
        }

        // The connection attempt succeeded. Perform work associated with
        // the connection in a separate thread.
        if (btSocket != null) {
            // A connection was accepted. Perform work associated with
            // the connection in a separate thread.
            new ConnectedThread(btSocket);
            try {
                btSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            //break;
        }
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
    public void sendMessage(OnSendToPresenter presenter, Context context) {

        if (!verificarConexion()) {

            try {
                inicializaBluetooth();
            } catch (IOException e) {
                e.printStackTrace();
            }
            encender();
            element.switchStatus = true;
        } else {

        }
        presenter.onFinished(element);
    }

    public boolean verificarConexion() {
        if (btSocket == null) {
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

        /*
        Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
         //getViewActivity().startActivityForResult(intent, 1000);
        ArrayList<BluetoothDevice> devices = new ArrayList<BluetoothDevice>();
        if (btAdapter == null)
            return;
        //Obtenemos todos los dispositivos bluetooth
        //vinculados
        for (BluetoothDevice d : btAdapter.getBondedDevices()) {
            if (d.getAddress().equals("35:0B:68:DA:0A:2F")) {
                devices.add(d);
            }
        }

        //Intent intent = getIntent();
        //Bundle extras = intent.getExtras();

        //bluetoothIn = Handler_Msg_Hilo_Principal();
        btAdapter.startDiscovery();
        btAdapter.cancelDiscovery();
        if (btAdapter.isDiscovering()) {
            btAdapter.cancelDiscovery();
        }
        btSocket = device.createRfcommSocketToServiceRecord(BTMODULEUUID);
        int i = 0;
        do {
            try {
                btSocket.connect();

            } catch (IOException e) {
                btSocket.close();
            }
            i++;
        }while (!btSocket.isConnected() && i<10);
        mConnectedThread = new ConnectedThread(btSocket);
        mConnectedThread.start();
        mConnectedThread.write("c");
        btSocket.close();

         */
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
                //finish();
            }
        }
    }

    private void showToast(String message) {
        //Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void conectar(OnSendToPresenter presenter) {

    }
}

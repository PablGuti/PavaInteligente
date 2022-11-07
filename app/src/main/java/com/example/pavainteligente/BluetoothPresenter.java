package com.example.pavainteligente;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class BluetoothPresenter implements BluetoothContract.Presenter {

    public static final int MULTIPLE_PERMISSIONS = 12; // code you want.

    private BluetoothContract.View view;
    private BluetoothContract.Model model;

    private IntentFilter filter;
    private BroadcastReceiver mReceiver;
    private BluetoothAdapter mBluetoothAdapter;

    public BluetoothPresenter(BluetoothContract.View view, BluetoothContract.Model model) {
        this.view = view;
        this.model = model;

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (checkPermissions())
            enableComponent();

        addFilters();
        addListeners();
    }

    protected void enableComponent() {
        //se determina si existe bluethoot en el celular
        if (mBluetoothAdapter == null) {
            //si el celular no soporta bluethoot
            view.showUnsupported();
        } else {
            //se determina si esta activado el bluethoot
            if (mBluetoothAdapter.isEnabled()) {
                //se informa si esta habilitado
                view.habilitarBotones();
            } else {
                //se informa si esta deshabilitado
                view.deshabilitarBotones();
            }
        }
    }

    @Override
    public void onClickActivarDesactivar() {
        if (mBluetoothAdapter.isEnabled()) {
            activar();
        } else {
            desactivar();
        }
    }

    private void activar() {
        if (ActivityCompat.checkSelfPermission(view.getContext(), Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mBluetoothAdapter.disable();

        view.deshabilitarBotones();
    }

    private void desactivar() {
        Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);

        if (ActivityCompat.checkSelfPermission(view.getContext(), Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        view.getViewActivity().startActivityForResult(intent, 1000);
    }

    @Override
    public void onClickEmparejar() {
        if (ActivityCompat.checkSelfPermission(view.getContext(), Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();

        if (pairedDevices == null || pairedDevices.size() == 0) {
            view.showToast("No se encontraron dispositivos emparejados");
        } else {
            ArrayList<BluetoothDevice> list = new ArrayList<BluetoothDevice>();

            list.addAll(pairedDevices);

            Intent intent = new Intent(view.getContext(), DeviceListActivity.class);

            intent.putParcelableArrayListExtra("device.list", list);

            view.getViewActivity().startActivity(intent);
        }
    }

    @Override
    public void onClickBuscar() {
        if (ActivityCompat.checkSelfPermission(view.getContext(), Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        mBluetoothAdapter.startDiscovery();
    }

    @Override
    public void onCancelDiscovery() {
        view.ocultarProgressDialog();

        if (ActivityCompat.checkSelfPermission(view.getContext(), Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mBluetoothAdapter.cancelDiscovery();
    }

    @Override
    public void onResultadoPermisos(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MULTIPLE_PERMISSIONS: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permissions granted.
                    enableComponent(); // Now you call here what ever you want :)
                } else {
                    String perStr = "";
                    for (String per : permissions) {
                        perStr += "\n" + per;
                    }
                    // permissions list of don't granted permission
                    view.showToast("ATENCION: La aplicacion no funcionara " +
                            "correctamente debido a la falta de Permisos");
                }
                return;
            }
        }
    }

    @Override
    public void onPause() {
        if (mBluetoothAdapter != null) {
            if (ActivityCompat.checkSelfPermission(view.getContext(), Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {

                do
                {
                }
                while(!checkPermissions());

                //return;
            }
            if (mBluetoothAdapter.isDiscovering()) {
                mBluetoothAdapter.cancelDiscovery();
            }
        }
    }

    @Override
    public void addDispositivo(BluetoothDevice dispositivoNuevo) {
        model.addDispositivo(dispositivoNuevo);
    }

    @Override
    public ArrayList<BluetoothDevice> getDispositivosEncontrados() {
        return model.getListaDispositivos();
    }

    @Override
    public void setDispositivosEncontrados(ArrayList<BluetoothDevice> dispositivosEncontrados) {
        model.setListaDispositivos(dispositivosEncontrados);
    }

    @Override
    public void onDestroy() {
        view.getContext().unregisterReceiver(mReceiver);
    }

    private void addFilters() {
        filter = new IntentFilter();

        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED); //Cambia el estado del Bluethoot (Acrtivado /Desactivado)
        filter.addAction(BluetoothDevice.ACTION_FOUND); //Se encuentra un dispositivo bluethoot al realizar una busqueda
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED); //Cuando se comienza una busqueda de bluethoot
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED); //cuando la busqueda de bluethoot finaliza

    }

    private void addListeners() {

        mReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {

                //Atraves del Intent obtengo el evento de Bluethoot que informo el broadcast del SO
                String action = intent.getAction();

                //Si cambio de estado el Bluethoot(Activado/desactivado)
                if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
                    //Obtengo el parametro, aplicando un Bundle, que me indica el estado del Bluethoot
                    final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);

                    //Si esta activado
                    if (state == BluetoothAdapter.STATE_ON) {
                        view.showToast("Activar");

                        view.habilitarBotones();
                    }
                }
                //Si se inicio la busqueda de dispositivos bluethoot
                else if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                    //muestro el cuadro de dialogo de busqueda
                    //esto lo tiene que pedir el presenter
                    view.mostrarProgressDialog();
                }
                //Si finalizo la busqueda de dispositivos bluethoot
                else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                    //se cierra el cuadro de dialogo de busqueda
                    view.ocultarProgressDialog();

                    //se inicia el activity DeviceListActivity pasandole como parametros, por intent,
                    //el listado de dispositovos encontrados
                    Intent newIntent = new Intent(view.getContext(), DeviceListActivity.class);

                    newIntent.putParcelableArrayListExtra("device.list", model.getListaDispositivos());

                    view.getViewActivity().startActivity(newIntent);
                }
                //si se encontro un dispositivo bluethoot
                else if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                    //Se lo agregan sus datos a una lista de dispositivos encontrados
                    BluetoothDevice device = (BluetoothDevice) intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                    model.addDispositivo(device);
                    if (ActivityCompat.checkSelfPermission(view.getContext(), Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    view.showToast("Dispositivo Encontrado:" + device.getName());
                }
            }
        };
        view.getContext().registerReceiver(mReceiver, filter);

    }

    //Metodo que chequea si estan habilitados los permisos
    private  boolean checkPermissions() {
        int result;
        List<String> listPermissionsNeeded = new ArrayList<>();

        //Se chequea si la version de Android es menor a la 6
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {

            return true;
        }

        for (String p:model.getPermissions()) {
            result = ContextCompat.checkSelfPermission(view.getContext(), p);
            if (result != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(p);
            }
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(view.getViewActivity(), listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]),MULTIPLE_PERMISSIONS );
            return false;
        }
        return true;
    }
}

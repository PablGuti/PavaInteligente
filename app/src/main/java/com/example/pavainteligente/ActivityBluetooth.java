package com.example.pavainteligente;


import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/*********************************************************************************************************
 * Activity Principal de la App. Es la primiera activity que se ejecuta cuando el usuario ingresa a la App
 **********************************************************************************************************/

@RequiresApi(api = Build.VERSION_CODES.S)
public class ActivityBluetooth extends Activity implements BluetoothContract.View {

    private BluetoothPresenter presenter;

    private TextView txtEstado;
    private Button btnActivar;
    private Button btnEmparejar;
    private Button btnBuscar;

    private ProgressDialog mProgressDlg;

    @Override
    //Metodo On create
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_bluetooth);


        //Se definen los componentes del layout
        txtEstado = (TextView) findViewById(R.id.txtEstado);
        btnActivar = (Button) findViewById(R.id.btnActivar);
        btnEmparejar = (Button) findViewById(R.id.btnEmparejar);
        btnBuscar = (Button) findViewById(R.id.btnBuscar);

        //Se Crea la ventana de dialogo que indica que se esta buscando dispositivos bluethoot
        mProgressDlg = new ProgressDialog(this);

        mProgressDlg.setMessage("Buscando dispositivos...");
        mProgressDlg.setCancelable(false);

        //se asocia un listener al boton cancelar para la ventana de dialogo ue busca los dispositivos bluethoot
        mProgressDlg.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancelar", btnCancelarDialogListener);

        btnEmparejar.setOnClickListener(btnEmparejarListener);
        btnBuscar.setOnClickListener(btnBuscarListener);
        btnActivar.setOnClickListener(btnActivarListener);

        presenter = new BluetoothPresenter(this, new BluetoothModel());

    }

    @Override
    //Cuando se llama al metodo OnPausa se cancela la busqueda de dispositivos bluethoot
    public void onPause() {
        presenter.onPause();
        super.onPause();
    }

    @Override
    //Cuando se detruye la Acivity se quita el registro de los brodcast. Apartir de este momento no se
    //recibe mas broadcast del SO. del bluethoot
    public void onDestroy() {
        presenter.onDestroy();
        super.onDestroy();
    }

    private void showDisabled() {

    }

    @Override
    public void showUnsupported() {
        txtEstado.setText("Bluetooth no es soportado por el dispositivo movil");

        btnActivar.setText("Activar");
        btnActivar.setEnabled(false);

        btnEmparejar.setEnabled(false);
        btnBuscar.setEnabled(false);
    }

    //Metodo que actua como Listener de los eventos que ocurren en los componentes graficos de la activty
    private View.OnClickListener btnEmparejarListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            presenter.onClickEmparejar();
        }
    };

    private View.OnClickListener btnBuscarListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            presenter.onClickBuscar();
        }
    };


    private View.OnClickListener btnActivarListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            presenter.onClickActivarDesactivar();
        }
    };


    private DialogInterface.OnClickListener btnCancelarDialogListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            presenter.onCancelDiscovery();
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        presenter.onResultadoPermisos(requestCode, permissions, grantResults);
    }

    @Override
    public void habilitarBotones() {
        txtEstado.setText("Bluetooth Habilitar");
        txtEstado.setTextColor(Color.BLUE);

        btnActivar.setText("Desactivar");
        btnActivar.setEnabled(true);

        btnEmparejar.setEnabled(true);
        btnBuscar.setEnabled(true);
    }

    @Override
    public void deshabilitarBotones() {
        txtEstado.setText("Bluetooth Deshabilitado");
        txtEstado.setTextColor(Color.RED);

        btnActivar.setText("Activar");
        btnActivar.setEnabled(true);

        btnEmparejar.setEnabled(false);
        btnBuscar.setEnabled(false);
    }

    @Override
    public void mostrarProgressDialog() {
        mProgressDlg.show();
    }

    @Override
    public void ocultarProgressDialog() {
        mProgressDlg.dismiss();
    }

    @Override
    public void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public Context getContext() {
        return this.getApplicationContext();
    }

    @Override
    public Activity getViewActivity() {
        return this;
    }
}
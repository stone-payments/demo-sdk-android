package br.com.stonesdk.sdkdemo.activities;

import android.annotation.SuppressLint;
import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.Set;

import br.com.stonesdk.sdkdemo.R;
import stone.application.interfaces.StoneCallbackInterface;
import stone.providers.BluetoothConnectionProvider;
import stone.utils.PinpadObject;

public class DevicesActivity extends AppCompatActivity implements OnItemClickListener {

    private static final int REQUEST_BLUETOOTH_CONNECT = 1001;
    private static final int REQUEST_ENABLE_BLUETOOTH = 1002;

    private BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private boolean btConnected = false;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_devices);

        listView = findViewById(R.id.listDevicesActivity);
        listView.setOnItemClickListener(this);
        setupBluetoothDevices();
    }

    private void setupBluetoothDevices() {
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth nao esta disponivel neste dispositivo", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!hasBluetoothConnectPermission()) {
            requestBluetoothConnectPermission();
            return;
        }

        if (!turnBluetoothOn()) {
            return;
        }

        listBluetoothDevices();
    }

    @SuppressLint("MissingPermission")
    public void listBluetoothDevices() {
        if (!hasBluetoothConnectPermission()) {
            requestBluetoothConnectPermission();
            return;
        }

        // Lista de Pinpads para passar para o BluetoothConnectionProvider.
        ArrayAdapter<String> btArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);

        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            listView.setAdapter(btArrayAdapter);
            return;
        }

        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();

        // Lista todos os dispositivos pareados.
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                btArrayAdapter.add(String.format("%s_%s", device.getName(), device.getAddress()));
            }
        }

        // Exibe todos os dispositivos da lista.
        listView.setAdapter(btArrayAdapter);
    }

    @SuppressLint("MissingPermission")
    public boolean turnBluetoothOn() {
        if (!hasBluetoothConnectPermission()) {
            requestBluetoothConnectPermission();
            return false;
        }

        try {
            if (mBluetoothAdapter.isEnabled()) {
                return true;
            }

            Intent enableBluetoothIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBluetoothIntent, REQUEST_ENABLE_BLUETOOTH);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    private boolean hasBluetoothConnectPermission() {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.S
                || checkSelfPermission(Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestBluetoothConnectPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            requestPermissions(new String[]{Manifest.permission.BLUETOOTH_CONNECT}, REQUEST_BLUETOOTH_CONNECT);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_BLUETOOTH_CONNECT) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                setupBluetoothDevices();
            } else {
                Toast.makeText(this, "Permissao Bluetooth negada", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_ENABLE_BLUETOOTH && resultCode == RESULT_OK && hasBluetoothConnectPermission()) {
            listBluetoothDevices();
        }
    }

    @Override
    @SuppressLint("MissingPermission")
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (!hasBluetoothConnectPermission()) {
            requestBluetoothConnectPermission();
            return;
        }

        // Pega o pinpad selecionado do ListView.
        String[] pinpadInfo = listView.getAdapter().getItem(position).toString().split("_");
        PinpadObject pinpadSelected = new PinpadObject(pinpadInfo[0], pinpadInfo[1], false);

        // Passa o pinpad selecionado para o provider de conexão bluetooth.
        final BluetoothConnectionProvider bluetoothConnectionProvider = new BluetoothConnectionProvider(DevicesActivity.this, pinpadSelected);
        bluetoothConnectionProvider.setDialogMessage("Criando conexao com o pinpad selecionado"); // Mensagem exibida do dialog.
        bluetoothConnectionProvider.setConnectionCallback(new StoneCallbackInterface() {

            public void onSuccess() {
                Toast.makeText(getApplicationContext(), "Pinpad conectado", Toast.LENGTH_SHORT).show();
                btConnected = true;
                finish();
            }

            public void onError() {
                Toast.makeText(getApplicationContext(), "Erro durante a conexao. Verifique a lista de erros do provider para mais informacoes", Toast.LENGTH_SHORT).show();
                Log.e("DevicesActivity", "onError: " + bluetoothConnectionProvider.getListOfErrors());
            }
        });
        bluetoothConnectionProvider.execute(); // Executa o provider de conexão bluetooth.
    }
}

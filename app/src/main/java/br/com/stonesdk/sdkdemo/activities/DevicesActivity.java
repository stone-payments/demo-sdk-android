package br.com.stonesdk.sdkdemo.activities;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Set;

import br.com.stone.sdk.android.error.StoneStatus;
import br.com.stone.sdk.payment.database.models.pinpad.PinpadObject;
import br.com.stone.sdk.payment.enums.Action;
import br.com.stone.sdk.payment.providers.BluetoothConnectionProvider;
import br.com.stone.sdk.payment.providers.interfaces.StoneActionCallback;
import br.com.stonesdk.sdkdemo.R;

public class DevicesActivity extends AppCompatActivity implements OnItemClickListener {

    private final BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private boolean btConnected = false;
    private ListView listView;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_devices);

        listView = findViewById(R.id.listDevicesActivity);
        listView.setOnItemClickListener(this);
        turnBluetoothOn();
        listBluetoothDevices();
    }

    @SuppressLint("MissingPermission")
    public void listBluetoothDevices() {
        ArrayAdapter<String> btArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();

        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                btArrayAdapter.add(String.format("%s_%s", device.getName(), device.getAddress()));
            }
        }

        listView.setAdapter(btArrayAdapter);
    }

    @SuppressLint("MissingPermission")
    public void turnBluetoothOn() {
        try {
            do {
                mBluetoothAdapter.enable();
            } while (!mBluetoothAdapter.isEnabled());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String[] pinpadInfo = listView.getAdapter().getItem(position).toString().split("_");
        PinpadObject pinpadSelected = new PinpadObject(pinpadInfo[0], pinpadInfo[1], false);

        final BluetoothConnectionProvider bluetoothConnectionProvider = new BluetoothConnectionProvider(DevicesActivity.this, pinpadSelected);
        bluetoothConnectionProvider.setConnectionCallback(new StoneActionCallback() {
            @Override
            public void onStatusChanged(Action action) {

            }

            @Override
            public void onSuccess() {
                Toast.makeText(getApplicationContext(), "Pinpad conectado", Toast.LENGTH_SHORT).show();
                btConnected = true;
                finish();
            }

            @Override
            public void onError(@Nullable StoneStatus stoneStatus) {
                Toast.makeText(getApplicationContext(), "Erro durante a conexao. Verifique a mensagem do Stone Status", Toast.LENGTH_SHORT).show();
                String error;
                if (stoneStatus != null) {
                    error = stoneStatus.getMessage();
                } else {
                    error = bluetoothConnectionProvider.getListOfErrors().toString();
                }
                Log.e("DevicesActivity", "onError: " + error);
            }
        });

        bluetoothConnectionProvider.execute();
    }
}

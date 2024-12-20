package br.com.stonesdk.sdkdemo.activities.devices

import android.Manifest.permission.BLUETOOTH_CONNECT
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.content.pm.PackageManager
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemClickListener
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material3.MaterialTheme
import androidx.core.content.ContextCompat
import br.com.stonesdk.sdkdemo.FeatureFlag
import br.com.stonesdk.sdkdemo.databinding.ActivityDevicesBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class DevicesActivity : AppCompatActivity(), OnItemClickListener {

    private lateinit var binding: ActivityDevicesBinding

    private val mBluetoothAdapter: BluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
    private var requestPermissionLauncher: ActivityResultLauncher<String>? = null

    private val providerWrapper: BluetoothProviderWrapper by inject()

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (FeatureFlag.composeRefactorEnabled) {
            setContent {
                MaterialTheme {
                    DevicesScreen(
                        closeScreen = ::finish
                    )
                }
            }
        } else {
            onCreateStart()
        }
    }

    @SuppressLint("MissingPermission")
    @RequiresApi(Build.VERSION_CODES.S)
    private fun onCreateStart() {

        binding = ActivityDevicesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.listDevicesActivity.onItemClickListener = this

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            requestPermissionLauncher =
                registerForActivityResult(RequestPermission()) { isGranted: Boolean ->
                    if (isGranted) {
                        turnBluetoothOn()
                        listBluetoothDevices()
                    } else {
                        if (isBluetoothAvailable()) {
                            requestBluetoothPermission()
                        } else {
                            Toast.makeText(
                                this,
                                "Bluetooth not supported on this device",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            checkPermission()
        } else {
            if (isBluetoothAvailable()) {
                turnBluetoothOn()
                listBluetoothDevices()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.S)
    fun checkPermission() {
        when {
            ContextCompat.checkSelfPermission(this, BLUETOOTH_CONNECT) == PERMISSION_GRANTED -> {
                Log.e("BLUE", "aqui 10")
                turnBluetoothOn()
                listBluetoothDevices()
            }

            shouldShowRequestPermissionRationale(BLUETOOTH_CONNECT) -> {
                Toast.makeText(
                    this,
                    "Bluetooth permission needed",
                    Toast.LENGTH_SHORT
                ).show()
                requestPermissionLauncher?.launch(BLUETOOTH_CONNECT)
            }

            else -> requestPermissionLauncher?.launch(BLUETOOTH_CONNECT)
        }
    }

    private fun isBluetoothAvailable(): Boolean {
        return packageManager.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH)
    }

    @RequiresApi(Build.VERSION_CODES.S)
    private fun requestBluetoothPermission() {
        // Request the BLUETOOTH_CONNECT permission
        requestPermissionLauncher?.launch(BLUETOOTH_CONNECT)
    }

    @RequiresPermission(BLUETOOTH_CONNECT)
    private fun listBluetoothDevices() {
        // Lista de Pinpads para passar para o BluetoothConnectionProvider.

        val btArrayAdapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1)

        val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        val pairedDevices = bluetoothAdapter.bondedDevices

        // Lista todos os dispositivos pareados.
        pairedDevices.forEach { device ->
            btArrayAdapter.add(String.format("%s_%s", device.name, device.address))
        }

        // Exibe todos os dispositivos da lista.
        binding.listDevicesActivity.adapter = btArrayAdapter
    }

    @RequiresPermission(BLUETOOTH_CONNECT)
    fun turnBluetoothOn() {
        try {
            mBluetoothAdapter.enable()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    override fun onItemClick(parent: AdapterView<*>?, view: View, position: Int, id: Long) {
        val pinpadInfo = binding.listDevicesActivity.adapter.getItem(position)
            .toString()
            .split("_".toRegex())
            .dropLastWhile { it.isEmpty() }
            .toTypedArray()

        CoroutineScope(Dispatchers.IO).launch {
            providerWrapper.connectPinpad(
                BluetoothInfo(
                    name = pinpadInfo[0],
                    address = pinpadInfo[1],
                )
            )
        }

    }
}

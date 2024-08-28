package br.com.stonesdk.sdkdemo.activities.devices

import android.Manifest.permission.BLUETOOTH_CONNECT
import android.bluetooth.BluetoothAdapter
import androidx.annotation.RequiresPermission
import androidx.lifecycle.ViewModel


class DevicesViewModel(
    private val adapter: BluetoothAdapter

): ViewModel(){

    @RequiresPermission(BLUETOOTH_CONNECT)
    fun turnBluetoothOn(){
        adapter.enable()
    }
data class DeviceUI(
    val listBluetoothDevices: List<String> = emptyList()
)
    sealed class ConnectionStatus {
        data object Success : ConnectionStatus()
        data class Error(val errorList: List<String>?) : ConnectionStatus()
    }
}
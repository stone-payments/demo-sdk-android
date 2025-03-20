package br.com.stonesdk.sdkdemo.ui.paired_devices

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


data class DevicePinpadUiModel(

    val pinpadConnected: Boolean = false,
    val bluetoothDevices: List<BluetoothInfo> = emptyList(),
    val state: DevicesEvent? = null
) {
    sealed interface DevicesEvent {
        data object ScanningDevices : DevicesEvent
    }
}

class DevicesViewModel : ViewModel() {

    var state by mutableStateOf(DevicePinpadUiModel())
        private set

    fun startDevicesScan() {
        viewModelScope.launch(Dispatchers.IO) {
            BluetoothDevice().startScan().collect { bluetoothDeviceList ->
                state = state.copy(
                    bluetoothDevices = bluetoothDeviceList.map {
                        BluetoothInfo(
                            name = it.deviceName,
                            address = it.hardwareAddress
                        )
                    }.distinctBy { it.address }
                )
            }
        }
    }


    fun stopScan() {
        BluetoothDevice().stopScan()
    }

    fun connect(bluetoothInfo: BluetoothInfo) {
        viewModelScope.launch(Dispatchers.IO) {
            BluetoothDevice().connect(bluetoothInfo.address).onSuccess {
                state = state.copy(
                    pinpadConnected = true
                )
            }
        }
    }

    fun disconnect() {
        BluetoothDevice().disconnect()
        state = state.copy(
            pinpadConnected = false
        )
    }

}

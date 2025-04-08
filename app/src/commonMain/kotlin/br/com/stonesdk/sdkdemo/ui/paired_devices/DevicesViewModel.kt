package br.com.stonesdk.sdkdemo.ui.paired_devices

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
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

class DevicesViewModel() : ViewModel() {

    var state by mutableStateOf(DevicePinpadUiModel())
        private set

    var repository = BluetoothDeviceRepository()

    fun startDevicesScan() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.startScan().collect { bluetoothDeviceList ->
                state = state.copy(
                    bluetoothDevices = bluetoothDeviceList.map {
                        BluetoothInfo(
                            name = it.deviceName,
                            address = it.hardwareAddress,
                            isConnected = true
                        )
                    }.distinctBy { it.address }
                )
            }
        }
    }


    fun stopScan() {
        repository.stopScan()
    }

    fun connect(bluetoothInfo: BluetoothInfo) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.connect(bluetoothInfo.address).onSuccess {
                state = state.copy(
                    pinpadConnected = true
                )
            }
        }
    }

    fun disconnect() {
        repository.disconnect()
        state = state.copy(
            pinpadConnected = false
        )
    }

}

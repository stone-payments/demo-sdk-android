package br.com.stonesdk.sdkdemo.ui.paired_devices

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.stonesdk.sdkdemo.routes.NavigationManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch


data class DevicePinpadUiModel(
    val pinpadConnected: Boolean = false,
    val bluetoothDevices: List<BluetoothInfo> = emptyList(),
)

class DevicesViewModel(
    private val navigationManager: NavigationManager,
    private val bluetoothDeviceRepository: BluetoothDeviceRepository
) : ViewModel() {

    var state by mutableStateOf(DevicePinpadUiModel())
        private set

    fun startDevicesScan() {
        viewModelScope.launch(Dispatchers.IO) {
            val devices = mutableListOf<BluetoothInfo>()
            bluetoothDeviceRepository.startScan().collect { device ->
                val bluetoothInfo = BluetoothInfo(
                    name = device.deviceName,
                    address = device.hardwareAddress
                )
                if (devices.none { it.address == bluetoothInfo.address }) {
                    devices.add(bluetoothInfo)
                }
                state = state.copy(bluetoothDevices = devices)
            }
        }
    }


    fun stopScan() {
        bluetoothDeviceRepository.stopScan()
    }

    fun connect(bluetoothInfo: BluetoothInfo) {
        viewModelScope.launch(Dispatchers.IO) {
            bluetoothDeviceRepository.connect(bluetoothInfo.address).onSuccess {
                bluetoothDeviceRepository.saveConnectedBluetoothDevice(
                    deviceName = bluetoothInfo.name,
                    deviceAddress = bluetoothInfo.address
                )
                state = state.copy(
                    pinpadConnected = true
                )
            }
        }
    }

    fun disconnect() {
        bluetoothDeviceRepository.disconnect()
        state = state.copy(
            pinpadConnected = false
        )
    }

    fun navigateBack() {
        navigationManager.navigateBack()
    }

}

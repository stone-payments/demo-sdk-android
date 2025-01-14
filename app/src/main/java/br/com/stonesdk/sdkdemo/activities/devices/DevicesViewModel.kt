package br.com.stonesdk.sdkdemo.activities.devices

import android.annotation.SuppressLint
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.stonesdk.sdkdemo.activities.devices.BluetoothInfo.Companion.toDeviceInfo
import br.com.stonesdk.sdkdemo.activities.devices.DeviceEffects.CloseScreen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class DevicesViewModel(
    private val bluetoothProviderWrapper: BluetoothProviderWrapper,
) : ViewModel() {

    var viewState by mutableStateOf(DevicePinpadUiModel())
        private set

    private val _sideEffects = MutableStateFlow<DeviceEffects?>(null)
    val sideEffects: StateFlow<DeviceEffects?> = _sideEffects

    @SuppressLint("MissingPermission")
    fun onEvent(event: DevicesEvent) {
        when (event) {
            is DevicesEvent.DeviceItemClick -> connectToPinpad(event.position)
            DevicesEvent.StartDeviceScan -> startDeviceScan()
            DevicesEvent.StopDeviceScan -> stopDeviceScan()
            DevicesEvent.Permission -> listBluetoothDevices()
        }
    }

    private fun startDeviceScan() {
        viewModelScope.launch {
            bluetoothProviderWrapper.startDeviceScan(
                onDiscover = { device ->
                    val deviceInfo = device.toDeviceInfo()
                    val availableDevices = viewState.bluetoothDevices
                    if (availableDevices.contains(deviceInfo).not()) {

                        availableDevices.toMutableList().add(deviceInfo)
                        viewState = viewState.copy(bluetoothDevices = availableDevices)
                    }
                },
                onBound = { device ->
                    val deviceInfo = device.toDeviceInfo()
                    viewState = viewState.copy(selectedDevice = deviceInfo)
                }
            )
        }
    }

    private fun stopDeviceScan() {
        bluetoothProviderWrapper.stopDeviceScan()
    }

    private fun listBluetoothDevices() {
        val devices = bluetoothProviderWrapper.listBluetoothDevices()
        viewState = viewState.copy(bluetoothDevices = devices)
        if (devices.isEmpty()) {
            viewState = viewState.copy(errorMessage = "Nenhum dispositivo pareado encontrado")
        }
    }

    private fun connectToPinpad(position: Int) {
        viewModelScope.launch {
            viewState = viewState.copy(loading = true)
            val pinpad = viewState.bluetoothDevices[position]
            val isSuccess = bluetoothProviderWrapper.connectPinpad(pinpad)
            if (isSuccess) {
                _sideEffects.emit(CloseScreen)
            } else {
                viewState = viewState.copy(
                    errorMessage = "Erro durante a conexao. Verifique a lista de erros " +
                            "do provider para mais informações",
                    loading = false,
                    pinpadConnected = false
                )
            }
        }
    }


}

data class DevicePinpadUiModel(
    val loading: Boolean = false,
    val pinpadConnected: Boolean = false,
    val bluetoothDevices: List<BluetoothInfo> = emptyList(),
    val selectedDevice: BluetoothInfo? = null,
    val errorMessage: String? = null
)

sealed interface DevicesEvent {
    data class DeviceItemClick(val position: Int) : DevicesEvent
    data object StartDeviceScan : DevicesEvent
    data object StopDeviceScan : DevicesEvent
    data object Permission : DevicesEvent

}

sealed interface DeviceEffects {
    data object CloseScreen : DeviceEffects
}
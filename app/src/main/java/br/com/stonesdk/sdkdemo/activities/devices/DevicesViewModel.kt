package br.com.stonesdk.sdkdemo.activities.devices

import android.Manifest
import android.annotation.SuppressLint
import android.os.Build
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.stonesdk.sdkdemo.activities.devices.BluetoothInfo.Companion.toDeviceInfo
import br.com.stonesdk.sdkdemo.activities.devices.DeviceEffects.CloseScreen
import br.com.stonesdk.sdkdemo.utils.getCurrentDateTime
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DevicesViewModel(
    private val bluetoothProviderWrapper: BluetoothProviderWrapper,
) : ViewModel() {

    private val _uiState: MutableStateFlow<DevicePinpadUiModel> =
        MutableStateFlow(DevicePinpadUiModel())
    val uiState: StateFlow<DevicePinpadUiModel> = _uiState.asStateFlow()

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
        // TODO in the current version of SDK, the stop discovery is not working as intended
        // so this method is being marked as not working;

        // TODO discovery is not working as expected, must be fixed in future versions

        return
//        viewModelScope.launch {
//            _uiState.update { it.copy(isScanningDevices = true) }
//
//            bluetoothProviderWrapper.startDeviceScan(
//                onStartDiscover = { device ->
//                    val deviceInfo = device.toDeviceInfo()
//                    val availableDevices = _uiState.value.bluetoothDevices
//                    if (availableDevices.contains(deviceInfo).not()) {
//                        availableDevices.toMutableList().add(deviceInfo)
//                        _uiState.update {
//                            it.copy(bluetoothDevices = availableDevices)
//                        }
//                    }
//                },
//                onStopDiscover = { result ->
//                    _uiState.update { it.copy(isScanningDevices = result) }
//                },
//                onBound = { device ->
//                    val deviceInfo = device.toDeviceInfo()
//                    _uiState.update {
//                        it.copy(
//                            selectedDevice = deviceInfo
//                        )
//                    }
//                }
//            )
//        }
    }

    fun getBluetoothPermissions(): List<String> {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            listOf(
                Manifest.permission.BLUETOOTH_CONNECT,
                Manifest.permission.BLUETOOTH_SCAN,
            )
        } else
            listOf(Manifest.permission.BLUETOOTH)
    }

    private fun stopDeviceScan() {
        viewModelScope.launch {
            bluetoothProviderWrapper.stopDeviceScan()
        }
    }

    private fun listBluetoothDevices() {
        val devices = bluetoothProviderWrapper.listBluetoothDevices()
        _uiState.update {
            if (devices.isEmpty()) {
                val errorMessages = _uiState.value.errorMessages.toMutableList()
                val newErrorMessage =
                    "${getCurrentDateTime()}: Nenhum dispositivo pareado encontrado"
                errorMessages.add(0, newErrorMessage)
                it.copy(
                    bluetoothDevices = devices,
                    errorMessages = errorMessages
                )
            } else {
                it.copy(bluetoothDevices = devices)
            }
        }
    }

    private fun connectToPinpad(position: Int) {
        viewModelScope.launch {
            stopDeviceScan()
            val devices = _uiState.value.bluetoothDevices
            val selectedDevice = devices[position]
            bluetoothProviderWrapper.connectPinpad(selectedDevice).let { status ->
                when (status) {
                    is ConnectPinpadStatus.Success -> {
                        _uiState.update { it.copy(pinpadConnected = true) }
                        _sideEffects.emit(CloseScreen)
                    }

                    is ConnectPinpadStatus.Error -> {
                        val errorMessages = _uiState.value.errorMessages.toMutableList()
                        val newErrorMessage =
                            "${getCurrentDateTime()}: ${status.errorMessage}"
                        errorMessages.add(0, newErrorMessage)

                        _uiState.value = DevicePinpadUiModel(
                            errorMessages = errorMessages
                        )
                    }
                }
            }
        }
    }


}

data class DevicePinpadUiModel(
    val isScanningDevices: Boolean = false,
    val pinpadConnected: Boolean = false,
    val bluetoothDevices: List<BluetoothInfo> = emptyList(),
    val selectedDevice: BluetoothInfo? = null,
    val errorMessages: List<String> = emptyList()
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
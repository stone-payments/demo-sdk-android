package br.com.stonesdk.sdkdemo.activities.devices

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.stonesdk.sdkdemo.activities.devices.DeviceEffects.CloseScreen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class DevicesViewModel(
    private val providerWrapper: BluetoothProviderWrapper,
) : ViewModel() {

    var viewState by mutableStateOf(DevicePinpadUiModel())
        private set

    private val _sideEffects = MutableStateFlow<DeviceEffects?>(null)
    val sideEffects: StateFlow<DeviceEffects?> = _sideEffects

    fun onEvent(event: DevicesEvent) {
        when (event) {
            is DevicesEvent.DeviceItemClick -> connectToPinpad(event.position)
            DevicesEvent.EnableBluetooth -> providerWrapper.turnBluetoothOn()
            DevicesEvent.Permission ->
                viewState = viewState.copy(
                    bluetoothDevices = providerWrapper.listBluetoothDevices()
                )
        }
    }


    private fun connectToPinpad(position: Int) {
        viewModelScope.launch {
            viewState = viewState.copy(loading = true)
            val pinpad = viewState.bluetoothDevices[position]
            val isSuccess = providerWrapper.connectPinpad(pinpad)
            if (isSuccess) {
                _sideEffects.emit(CloseScreen)
            } else {
                viewState =
                    viewState.copy(
                        errorMessage = "Erro durante a conexao. Verifique a lista de erros " +
                                "do provider para mais informações",
                        loading = false,
                        pinpadConnected = false
                    )

            }
        }
    }

    data class DevicePinpadUiModel(
        val loading: Boolean = false,
        val pinpadConnected: Boolean = false,
        val bluetoothDevices: List<String> = emptyList(),
        //val selectedPinpad: PinpadObject? = null,
        val errorMessage: String? = null
    )

}

sealed interface DevicesEvent {
    data class DeviceItemClick(val position: Int) : DevicesEvent
    data object EnableBluetooth : DevicesEvent
    data object Permission : DevicesEvent

}

sealed interface DeviceEffects {
    data object CloseScreen : DeviceEffects
}
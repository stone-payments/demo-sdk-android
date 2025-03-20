package br.com.stonesdk.sdkdemo.ui.paired_devices

import androidx.compose.runtime.Composable

data class BluetoothInfo(
    val name: String,
    val address: String,
)


@Composable
fun DeviceScreen(
    viewModel: DevicesViewModel = androidx.lifecycle.viewmodel.compose.viewModel { DevicesViewModel() },
) {
    DevicesContent(
        viewModel.state.bluetoothDevices,
        viewModel.state.pinpadConnected,
        onStopClick = {
            viewModel.stopScan()
        },
        onScanClick = {
            viewModel.startDevicesScan()
        },
        onDisconnectClick = {
            viewModel.disconnect()
        },
        onConnectClick = {
            viewModel.connect(it)
        }
    )
}

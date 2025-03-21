package br.com.stonesdk.sdkdemo.ui.paired_devices

import androidx.compose.runtime.Composable
import androidx.navigation.NavController

data class BluetoothInfo(
    val name: String,
    val address: String,
    val isConnected : Boolean
)


@Composable
fun DeviceScreen(
    navController: NavController,
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
        },
        onBackPressed = {
            navController.popBackStack()
        }
    )
}

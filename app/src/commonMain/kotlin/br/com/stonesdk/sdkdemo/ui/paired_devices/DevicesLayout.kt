package br.com.stonesdk.sdkdemo.ui.paired_devices

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import org.koin.compose.viewmodel.koinViewModel

data class BluetoothInfo(
    val name: String,
    val address: String
)


@Composable
fun DeviceScreen(
    navController: NavController,
    viewModel: DevicesViewModel = koinViewModel()
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
            navController.navigateUp()
        }
    )
}

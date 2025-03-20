package br.com.stonesdk.sdkdemo.ui.paired_devices

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

data class BluetoothInfo(
    val name: String,
    val address: String,
) {
    companion object {
        fun BluetoothDevice.toDeviceInfo(): BluetoothInfo {
            return BluetoothInfo(
                name = "",
                address = ""
            )
        }
    }
}


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

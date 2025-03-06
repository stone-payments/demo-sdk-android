package br.com.stonesdk.sdkdemo.ui.paired_devices

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
//import co.stone.posmobile.sdk.bluetooth.domain.model.BluetoothDevice
import co.stone.posmobile.sdk.bluetooth.domain.model.BluetoothDevice


data class BluetoothInfo(
    val name: String,
    val address: String,
){
    companion object {
        fun BluetoothDevice.toDeviceInfo(): BluetoothInfo {
            return BluetoothInfo(
                name = this.deviceName,
                address = this.hardwareAddress
            )
        }
    }
}



@Composable
fun DeviceScreen(
    viewModel: DevicesViewModel = androidx.lifecycle.viewmodel.compose.viewModel { DevicesViewModel() },
){
    Column(modifier = Modifier.fillMaxSize()){

        Button(
            onClick = {
                viewModel.startDevicesScan()
            }
        ) {
            Text("Scan")
        }
    }
}


@Composable
fun ScanningDeviceContent(modifier: Modifier = Modifier, onStopDiscovery : () -> Unit) {

    Column(
        modifier = modifier
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Procurando dispositivos...",
                style = MaterialTheme.typography.h6,
                modifier = Modifier
                    .padding(start = 8.dp)
                    .weight(1f)
            )

            Button(
                modifier = Modifier.padding(end = 8.dp),
                onClick = onStopDiscovery
            ) {
                Text("Stop")
            }

            CircularProgressIndicator()
        }
    }

}



//@Composable
//fun BluetoothDeviceList(
//    modifier: Modifier = Modifier,
//    bluetoothDevices: List<BluetoothInfo>,
//    onEvent: (DevicesEvent) -> Unit
//) {
//
//    if (bluetoothDevices.isEmpty()) {
//        Text(
//            textAlign = TextAlign.Center,
//            text = "Nenhum dispositivo Bluetooth disponível.",
//            modifier = Modifier.padding(16.dp)
//        )
//    } else {
//        LazyColumn(
//            modifier = modifier
//                .fillMaxWidth()
//                .fillMaxSize(),
//            contentPadding = PaddingValues(vertical = 8.dp)
//        ) {
//            items(bluetoothDevices.size, key = { index -> bluetoothDevices[index].name }) { index ->
//                BluetoothDeviceItem(deviceName = bluetoothDevices[index].name,
//                    onClick = { onEvent(DevicesEvent.DeviceItemClick(index)) })
//            }
//        }
//    }
//}
//
//
//@Composable
//fun BluetoothDeviceItem(
//    deviceName: String, onClick: () -> Unit
//) {
//    Card(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(vertical = 4.dp)
//            .clickable { onClick() },
//    ) {
//        Text(
//            text = deviceName,
//            modifier = Modifier.padding(16.dp),
//            style = MaterialTheme.typography.subtitle1
//        )
//    }
//}


//@Composable
//fun DevicesScreen(
//    viewModel: DevicesViewModel = androidx.lifecycle.viewmodel.compose.viewModel { DevicesViewModel() },
//    closeScreen: () -> Unit
//) {
//    val uiModel = viewModel.uiState.collectAsState()
//    val effects by viewModel.sideEffects.collectAsState()
//
////    val permissionState = rememberMultiplePermissionsState(
////        viewModel.getBluetoothPermissions()
////    )
//
//    LaunchedEffect(effects) {
//        effects?.let { event ->
//            if (event == DeviceEffects.CloseScreen) {
//                closeScreen()
//            }
//        }
//    }
//
////    LaunchedEffect(true) {
////        if (!permissionState.allPermissionsGranted) {
////            requestBluetoothPermission(
////                onPermissionGranted = {
////                    viewModel.onEvent(DevicesEvent.StartDeviceScan)
////                    viewModel.onEvent(DevicesEvent.Permission)
////                }, permissionState = permissionState
////            )
////        } else {
////            viewModel.onEvent(DevicesEvent.StartDeviceScan)
////            viewModel.onEvent(DevicesEvent.Permission)
////        }
////    }
//
//    val isScanning = remember { derivedStateOf { uiModel.value.isScanningDevices } }
//    val availableDevices = remember { derivedStateOf { uiModel.value.bluetoothDevices } }
//    val errorMessages = remember { derivedStateOf { uiModel.value.errorMessages } }
//
//    DevicesContent(
//        isScanning = isScanning.value,
//        availableDevices = availableDevices.value,
//        errorMessages = errorMessages.value,
//        onEvent = viewModel::onEvent
//    )
//}
//
//@Composable
//fun DevicesContent(
//    isScanning: Boolean,
//    availableDevices: List<BluetoothInfo>,
//    errorMessages: List<String>,
//    onEvent: (DevicesEvent) -> Unit
//) {
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .padding(16.dp),
//        horizontalAlignment = Alignment.CenterHorizontally
//    ) {
//        if (isScanning) {
//            Row (
//                verticalAlignment = Alignment.CenterVertically
//            ){
//                Text(
//                    text = "Procurando dispositivos...",
//                    style = MaterialTheme.typography.bodySmall,
//                    modifier = Modifier
//                        .padding(start = 8.dp)
//                        .weight(1f)
//                )
//
//                Button(
//                    modifier = Modifier.padding(end = 8.dp),
//                    onClick = { onEvent(DevicesEvent.StopDeviceScan) }
//                ) {
//                    Text("Stop")
//                }
//
//                CircularProgressIndicator()
//            }
//        }
//
//        BluetoothDeviceList(
//            bluetoothDevices = availableDevices,
//            onEvent = onEvent
//        )
//
//        LazyColumn {
//            items(
//                count = errorMessages.size,
//                key = { index -> index }
//            ) { index ->
//                MonospacedText(
//                    text = errorMessages
//                        .getOrNull(index)
//                        .orEmpty(),
//                    modifier = Modifier.padding(8.dp)
//                )
//            }
//        }
//    }
//}
//
//@Composable
//fun BluetoothDeviceList(
//    modifier: Modifier = Modifier,
//    bluetoothDevices: List<BluetoothInfo>,
//    onEvent: (DevicesEvent) -> Unit
//) {
//
//    if (bluetoothDevices.isEmpty()) {
//        Text(
//            textAlign = TextAlign.Center,
//            text = "Nenhum dispositivo Bluetooth disponível.",
//            modifier = Modifier.padding(16.dp)
//        )
//    } else {
//        LazyColumn(
//            modifier = modifier
//                .fillMaxWidth()
//                .fillMaxSize(),
//            contentPadding = PaddingValues(vertical = 8.dp)
//        ) {
//            itemsIndexed(bluetoothDevices) { index, device ->
//                BluetoothDeviceItem(deviceName = device.name,
//                    onClick = { onEvent(DevicesEvent.DeviceItemClick(index)) })
//            }
//        }
//    }
//}
//
//@SuppressLint("MissingPermission")
//@Composable
//fun BluetoothDeviceItem(
//    deviceName: String, onClick: () -> Unit
//) {
//    Card(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(vertical = 4.dp)
//            .clickable { onClick() },
//    ) {
//        Text(
//            text = deviceName,
//            modifier = Modifier.padding(16.dp),
//            style = MaterialTheme.typography.bodySmall
//        )
//    }
//}
//
//@ExperimentalPermissionsApi
//fun requestBluetoothPermission(
//    onPermissionGranted: () -> Unit,
//    permissionState: MultiplePermissionsState
//) {
//    when {
//        permissionState.allPermissionsGranted -> onPermissionGranted()
//        else -> permissionState.launchMultiplePermissionRequest()
//    }
//}
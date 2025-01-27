package br.com.stonesdk.sdkdemo.activities.devices

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import br.com.stonesdk.sdkdemo.ui.components.MonospacedText
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import org.koin.androidx.compose.koinViewModel


@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun DevicesScreen(
    viewModel: DevicesViewModel = koinViewModel(),
    closeScreen: () -> Unit
) {
    val uiModel = viewModel.uiState.collectAsState()
    val effects by viewModel.sideEffects.collectAsState()

    val permissionState = rememberMultiplePermissionsState(
        viewModel.getBluetoothPermissions()
    )

    LaunchedEffect(effects) {
        effects?.let { event ->
            if (event == DeviceEffects.CloseScreen) {
                closeScreen()
            }
        }
    }

    LaunchedEffect(true) {
        if (!permissionState.allPermissionsGranted) {
            requestBluetoothPermission(
                onPermissionGranted = {
                    viewModel.onEvent(DevicesEvent.StartDeviceScan)
                    viewModel.onEvent(DevicesEvent.Permission)
                }, permissionState = permissionState
            )
        } else {
            viewModel.onEvent(DevicesEvent.StartDeviceScan)
            viewModel.onEvent(DevicesEvent.Permission)
        }
    }

    val isScanning = remember { derivedStateOf { uiModel.value.isScanningDevices } }
    val availableDevices = remember { derivedStateOf { uiModel.value.bluetoothDevices } }
    val errorMessages = remember { derivedStateOf { uiModel.value.errorMessages } }

    DevicesContent(
        isScanning = isScanning.value,
        availableDevices = availableDevices.value,
        errorMessages = errorMessages.value,
        onEvent = viewModel::onEvent
    )
}

@Composable
fun DevicesContent(
    isScanning: Boolean,
    availableDevices: List<BluetoothInfo>,
    errorMessages: List<String>,
    onEvent: (DevicesEvent) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (isScanning) {
            Row (
                verticalAlignment = Alignment.CenterVertically
            ){
                Text(
                    text = "Procurando dispositivos...",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .weight(1f)
                )

                Button(
                    modifier = Modifier.padding(end = 8.dp),
                    onClick = { onEvent(DevicesEvent.StopDeviceScan) }
                ) {
                    Text("Stop")
                }

                CircularProgressIndicator()
            }
        }

        BluetoothDeviceList(
            bluetoothDevices = availableDevices,
            onEvent = onEvent
        )

        LazyColumn {
            items(
                count = errorMessages.size,
                key = { index -> index }
            ) { index ->
                MonospacedText(
                    text = errorMessages
                        .getOrNull(index)
                        .orEmpty(),
                    modifier = Modifier.padding(8.dp)
                )
            }
        }
    }
}

@Composable
fun BluetoothDeviceList(
    modifier: Modifier = Modifier,
    bluetoothDevices: List<BluetoothInfo>,
    onEvent: (DevicesEvent) -> Unit
) {

    if (bluetoothDevices.isEmpty()) {
        Text(
            textAlign = TextAlign.Center,
            text = "Nenhum dispositivo Bluetooth disponível.",
            modifier = Modifier.padding(16.dp)
        )
    } else {
        LazyColumn(
            modifier = modifier
                .fillMaxWidth()
                .fillMaxSize(),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            itemsIndexed(bluetoothDevices) { index, device ->
                BluetoothDeviceItem(deviceName = device.name,
                    onClick = { onEvent(DevicesEvent.DeviceItemClick(index)) })
            }
        }
    }
}

@SuppressLint("MissingPermission")
@Composable
fun BluetoothDeviceItem(
    deviceName: String, onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onClick() },
    ) {
        Text(
            text = deviceName,
            modifier = Modifier.padding(16.dp),
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@ExperimentalPermissionsApi
fun requestBluetoothPermission(
    onPermissionGranted: () -> Unit,
    permissionState: MultiplePermissionsState
) {
    when {
        permissionState.allPermissionsGranted -> onPermissionGranted()
        else -> permissionState.launchMultiplePermissionRequest()
    }
}
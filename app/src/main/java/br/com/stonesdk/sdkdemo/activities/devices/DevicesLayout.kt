package br.com.stonesdk.sdkdemo.activities.devices

import android.Manifest
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import br.com.stonesdk.sdkdemo.activities.devices.DevicesViewModel.DevicePinpadUiModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import org.koin.androidx.compose.getViewModel


@Composable
fun DevicesScreen(
    viewModel: DevicesViewModel = getViewModel(),
    closeScreen: () -> Unit
) {
    val effects by viewModel.sideEffects.collectAsState()
    LaunchedEffect(effects) {
        effects?.let { event ->
            if (event == DeviceEffects.CloseScreen) {
                closeScreen()
            }
        }
    }
    val viewState = viewModel.viewState
    if (viewState.loading) {
        CircularProgressIndicator(modifier = Modifier.padding(16.dp))
    }

    DevicesContent(
        viewState = viewModel.viewState,
        onEvent = viewModel::onEvent
    )

}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun DevicesContent(
    onEvent: (DevicesEvent) -> Unit,
    viewState: DevicePinpadUiModel
) {
    var requestPermission by remember { mutableStateOf(false) }
    val permissionState = rememberPermissionState(Manifest.permission.BLUETOOTH_CONNECT)
    if (!permissionState.status.isGranted) {
        RequestBluetoothPermission(
            onPermissionGranted = {
                requestPermission = false
                onEvent(DevicesEvent.EnableBluetooth)
                onEvent(DevicesEvent.Permission)

            },
            permissionState = permissionState
        )
    } else {
        onEvent(DevicesEvent.EnableBluetooth)
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        if (viewState.bluetoothDevices.isNotEmpty()) {
            Text(
                text = "Selecione um dispositivo",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(8.dp)
            )
        }

        BluetoothDeviceList(
            bluetoothDevices = viewState.bluetoothDevices,
            onDeviceClick = { position -> onEvent(DevicesEvent.DeviceItemClick(position)) }
        )

        viewState.errorMessage?.let { errorMessage ->
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 16.dp)
            )
        }
    }

}

@Composable
fun BluetoothDeviceList(
    bluetoothDevices: List<String>,
    onDeviceClick: (Int) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(vertical = 8.dp)
    ) {
        items(bluetoothDevices) { device ->
            val index = bluetoothDevices.indexOf(device)
            BluetoothDeviceItem(
                deviceName = device,
                onClick = { onDeviceClick(index) }
            )
        }
    }
}

@Composable
fun BluetoothDeviceItem(
    deviceName: String,
    onClick: () -> Unit
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


@OptIn(ExperimentalPermissionsApi::class)
@ExperimentalPermissionsApi
@Composable
fun RequestBluetoothPermission(
    onPermissionGranted: () -> Unit,
    permissionState: PermissionState
) {
    LaunchedEffect(permissionState) {
        when {
            permissionState.status.isGranted -> onPermissionGranted()
            else -> permissionState.launchPermissionRequest()
        }

    }

}
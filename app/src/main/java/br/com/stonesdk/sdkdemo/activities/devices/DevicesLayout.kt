package br.com.stonesdk.sdkdemo.activities.devices

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import org.koin.androidx.compose.koinViewModel


@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun DevicesScreen(
    viewModel: DevicesViewModel = koinViewModel(), closeScreen: () -> Unit
) {
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

    if (!permissionState.allPermissionsGranted) {
        RequestBluetoothPermission(
            onPermissionGranted = {
                viewModel.onEvent(DevicesEvent.StartDeviceScan)
                viewModel.onEvent(DevicesEvent.Permission)
            }, permissionState = permissionState
        )
    } else {
        viewModel.onEvent(DevicesEvent.StartDeviceScan)
        viewModel.onEvent(DevicesEvent.Permission)
    }

    DevicesContent(
        viewState = viewModel.viewState, onEvent = viewModel::onEvent
    )
}

@Composable
fun DevicesContent(
    onEvent: (DevicesEvent) -> Unit, viewState: DevicePinpadUiModel
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (viewState.loading) {
            CircularProgressIndicator(modifier = Modifier.padding(16.dp))
        }
        if (viewState.bluetoothDevices.isEmpty()) {
            Text(
                textAlign = TextAlign.Center,
                text = "Nenhum dispositivo Bluetooth disponível.",
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        } else {
            BluetoothDeviceList(
                modifier = Modifier.weight(1f),
                bluetoothDevices = viewState.bluetoothDevices,
                onEvent = onEvent
            )
        }
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
    modifier: Modifier = Modifier,
    bluetoothDevices: List<BluetoothInfo>,
    onEvent: (DevicesEvent) -> Unit
) {
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
@Composable
fun RequestBluetoothPermission(
    onPermissionGranted: () -> Unit,
    permissionState: MultiplePermissionsState
) {
    LaunchedEffect(permissionState) {
        when {
            permissionState.allPermissionsGranted -> onPermissionGranted()
            else -> permissionState.launchMultiplePermissionRequest()
        }
    }
}
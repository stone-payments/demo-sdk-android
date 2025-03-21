package br.com.stonesdk.sdkdemo.ui.paired_devices

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun DevicesContent(
    bluetoothDevicesList: List<BluetoothInfo>,
    pinpadConnected: Boolean = false,
    onStopClick: () -> Unit,
    onScanClick: () -> Unit,
    onDisconnectClick: () -> Unit,
    onConnectClick: (BluetoothInfo) -> Unit,
    onBackPressed: () -> Unit
) {
    var isDeviceConnected = false
    Column(modifier = Modifier.fillMaxSize()) {
        Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
            Button(
                modifier = Modifier.padding(end = 8.dp),
                onClick = {
                    onScanClick.invoke()
                }
            ) {
                Text("Scan")
            }

            Button(
                modifier = Modifier.padding(end = 8.dp),
                onClick = {
                    onStopClick.invoke()
                }
            ) {
                Text("Stop scan")
            }

            Button(
                modifier = Modifier.padding(end = 8.dp),
                enabled = pinpadConnected,
                onClick = {
                    onDisconnectClick.invoke()
                }
            ) {
                Text("Disconnect")
            }
            Button(
                modifier = Modifier.padding(end = 8.dp),
                onClick = {
                    onBackPressed.invoke()
                }
            ) {
                Text("Voltar")
            }
        }

        Text(
            fontSize = 18.sp,
            fontWeight = FontWeight.W900,
            textAlign = TextAlign.Center,
            text = "Lista de dispositivos:",
            modifier = Modifier.fillMaxWidth()
        )

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(bluetoothDevicesList) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            onConnectClick(it)
                        }
                ) {
                    Column {
                        Text(it.name)
                        Text(it.address)
                        Text("Conectado: ${it.isConnected}")
                    }
                }
            }
        }
    }
}
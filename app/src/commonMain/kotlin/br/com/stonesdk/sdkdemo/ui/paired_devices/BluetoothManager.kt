package br.com.stonesdk.sdkdemo.ui.paired_devices

import br.com.stonesdk.sdkdemo.data.BluetoothPreferences
import br.com.stonesdk.sdkdemo.wrappers.BluetoothConnectStatus
import br.com.stonesdk.sdkdemo.wrappers.BluetoothDiscoverStatus
import br.com.stonesdk.sdkdemo.wrappers.BluetoothProviderWrapper
import co.stone.posmobile.sdk.bluetooth.domain.model.BluetoothDevice
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow

class BluetoothDeviceRepository(
    private val bluetoothProviderWrapper: BluetoothProviderWrapper,
    private val bluetoothPreferences: BluetoothPreferences
) {

    fun startScan(): Flow<BluetoothDevice> = channelFlow {
        when (val result = bluetoothProviderWrapper.discoverPinpad()) {
            is BluetoothDiscoverStatus.Success -> {
                result.devices.forEach { trySend(it) }
            }

            is BluetoothDiscoverStatus.Error -> {
                close()
            }
        }

        awaitClose {
            bluetoothProviderWrapper.stopDiscover()
        }

    }

    fun stopScan() {
        bluetoothProviderWrapper.stopDiscover()
    }

    fun disconnect() {
        bluetoothProviderWrapper.disconnect()
    }

    suspend fun getConnectedBluetoothDevice(): BluetoothDevice? {
        return bluetoothPreferences.getPreferences()
    }

    suspend fun saveConnectedBluetoothDevice(
        deviceName: String,
        deviceAddress: String
    ) {
        bluetoothPreferences.savePreferences(bluetoothName = deviceName, bluetoothAddress = deviceAddress)
    }

    suspend fun connect(address: String): Result<Unit> {
        return when (val connectStatus = bluetoothProviderWrapper.connect(deviceAddress = address)) {
            BluetoothConnectStatus.Success -> {
                Result.success(Unit)
            }

            is BluetoothConnectStatus.Error -> {
                Result.failure(Exception(connectStatus.errorMessage))
            }
        }
    }
}

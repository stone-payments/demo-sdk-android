package br.com.stonesdk.sdkdemo.ui.paired_devices

import co.stone.posmobile.sdk.bluetooth.domain.model.BluetoothDevice
import kotlinx.coroutines.flow.Flow

expect class BluetoothDevice() {
    fun startScan(): Flow<List<BluetoothDevice>>
    fun stopScan()
    suspend fun connect(address : String): Result<Unit>
    fun disconnect()
}
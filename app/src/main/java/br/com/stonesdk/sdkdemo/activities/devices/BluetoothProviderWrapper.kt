package br.com.stonesdk.sdkdemo.activities.devices

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import br.com.stone.sdk.android.error.StoneStatus
import co.stone.posmobile.sdk.bluetooth.domain.model.BluetoothDevice
import co.stone.posmobile.sdk.bluetooth.provider.BluetoothProvider
import co.stone.posmobile.sdk.callback.StoneResultCallback
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class BluetoothProviderWrapper {

    val provider: BluetoothProvider
        get() = BluetoothProvider.create()

    suspend fun connectPinpad(
        pinpad: BluetoothInfo
    ): Boolean {
        return bluetoothConnection(pinpad)
    }

    private suspend fun bluetoothConnection(pinpad: BluetoothInfo): Boolean =
        suspendCancellableCoroutine { continuation ->

            provider.connect(
                pinpad.address,
                pinpad.name,
                stoneResultCallback = object : StoneResultCallback<Boolean> {
                    override fun onSuccess(result: Boolean) {
                        continuation.resume(true)
                    }

                    override fun onError(stoneStatus: StoneStatus?, throwable: Throwable) {
                        continuation.resume(false)
                    }
                })

            continuation.invokeOnCancellation {}
        }

    @SuppressLint("MissingPermission")
    fun listBluetoothDevices(): List<BluetoothInfo> {
        val adapter = BluetoothAdapter.getDefaultAdapter()
        val bluetoothAdapter = adapter.bondedDevices

        return bluetoothAdapter.map { device ->
            BluetoothInfo(
                name = device.name,
                address = device.address
            )
        }
    }

    fun startDeviceScan(onDiscover: (BluetoothDevice) -> Unit, onBound: (BluetoothDevice) -> Unit) {
        provider.discoverPinpad(onDiscover, onBound)
    }

    fun stopDeviceScan(){
        provider.stopDiscover()
    }

}

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
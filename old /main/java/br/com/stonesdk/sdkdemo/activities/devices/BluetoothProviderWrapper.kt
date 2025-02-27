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
    ): ConnectPinpadStatus {
        return bluetoothConnection(pinpad)
    }

    private suspend fun bluetoothConnection(pinpad: BluetoothInfo): ConnectPinpadStatus =
        suspendCancellableCoroutine { continuation ->

            provider.connect(
                pinpadAddress = pinpad.address,
                pinpadModelName = pinpad.name,
                stoneResultCallback = object : StoneResultCallback<Boolean> {
                    override fun onSuccess(result: Boolean) {
                        continuation.resume(ConnectPinpadStatus.Success)
                    }

                    override fun onError(stoneStatus: StoneStatus?, throwable: Throwable) {
                        continuation.resume(ConnectPinpadStatus.Error(
                            stoneStatus?.message ?: throwable.message ?: "Erro desconhecido"
                        ))
                    }
                }
            )

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

    fun startDeviceScan(
        onStartDiscover: (BluetoothDevice) -> Unit,
        onStopDiscover: (Boolean) -> Unit,
        onBound: (BluetoothDevice) -> Unit
    ) {
        provider.discoverPinpad(
            onStartDiscover = onStartDiscover,
            onStopDiscover = onStopDiscover,
            onBound = onBound
        )
    }

    fun stopDeviceScan(){
        provider.stopDiscover()
    }

}

sealed class ConnectPinpadStatus {
    data object Success : ConnectPinpadStatus()
    data class Error(val errorMessage: String) : ConnectPinpadStatus()
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
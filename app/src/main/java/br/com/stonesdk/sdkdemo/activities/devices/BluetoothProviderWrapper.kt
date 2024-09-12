package br.com.stonesdk.sdkdemo.activities.devices

import android.Manifest.permission.BLUETOOTH_CONNECT
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.content.Context
import androidx.annotation.RequiresPermission
import kotlinx.coroutines.suspendCancellableCoroutine
import stone.application.interfaces.StoneCallbackInterface
import stone.providers.BluetoothConnectionProvider
import stone.utils.PinpadObject
import kotlin.coroutines.resume

class BluetoothProviderWrapper(
    private val context: Context,
    private val bluetoothAdapter: BluetoothAdapter
) {
    suspend fun connectPinpad(
        pinpad: BluetoothInfo,
        provider: BluetoothConnectionProvider = bluetoothProvider(pinpad)
    ): Boolean {
        return bluetoothConnection(provider)
    }

    private suspend fun bluetoothConnection(provider: BluetoothConnectionProvider): Boolean =
        suspendCancellableCoroutine { continuation ->

            provider.connectionCallback = object : StoneCallbackInterface {
                override fun onSuccess() {
                    continuation.resume(true)
                }

                override fun onError() {
                    continuation.resume(false)
                }
            }
            provider.execute()
            continuation.invokeOnCancellation {}
        }

    private fun bluetoothProvider(pinpad: BluetoothInfo) =
        BluetoothConnectionProvider(context, getPinpadObject(pinpad))

    private fun getPinpadObject(pinpad: BluetoothInfo): PinpadObject {
        return PinpadObject(pinpad.name, pinpad.address, false)
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

    @RequiresPermission(BLUETOOTH_CONNECT)
    fun turnBluetoothOn() {
        try {
            bluetoothAdapter.enable()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}

data class BluetoothInfo(
    val name: String,
    val address: String,
)
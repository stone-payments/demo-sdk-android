package br.com.stonesdk.sdkdemo.activities.devices

import android.Manifest.permission.BLUETOOTH_CONNECT
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
        pinpad: String,
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

    private fun bluetoothProvider(pinpad: String) =
        BluetoothConnectionProvider(context, getPinpadObject(pinpad))

    private fun getPinpadObject(pinpad: String): PinpadObject {
        val pinpadInfo = pinpad
            .split("_".toRegex())
            .dropLastWhile { it.isEmpty() }
        return PinpadObject(pinpadInfo[0], pinpadInfo[1], false)
    }

    fun listBluetoothDevices(): List<String> {

        val bluetoothAdapter = bluetoothAdapter.bondedDevices
            .map { device -> "${device.name}_${device.address}" }
            .toList()
        return bluetoothAdapter
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
package br.com.stonesdk.sdkdemo.activities.devices

import android.Manifest.permission.BLUETOOTH_CONNECT
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.util.Log
import androidx.annotation.RequiresPermission
import br.com.stone.sdk.android.error.StoneStatus
import co.stone.posmobile.sdk.domain.model.response.StoneResultCallback
import co.stone.posmobile.sdk.hardware.provider.bluetooth.BluetoothProvider
import kotlinx.coroutines.suspendCancellableCoroutine

import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class BluetoothProviderWrapper(
    private val bluetoothAdapter: BluetoothAdapter,
    private val bluetoothProvider: BluetoothProvider
) {
    suspend fun connectPinpad(
        pinpad: BluetoothInfo,
    ): Boolean {
        return suspendCoroutine { cont ->
            bluetoothProvider.connect(pinpad.address, "", object : StoneResultCallback<Boolean> {
                override fun onSuccess(result: Boolean) {
                    cont.resume(true)
                }

                override fun onError(stoneStatus: StoneStatus?, throwable: Throwable) {
                    Log.i("log", throwable.message, throwable)
                    cont.resume(false)
                }
            })
        }

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
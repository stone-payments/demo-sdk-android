package br.com.stonesdk.sdkdemo.wrappers

import br.com.stone.sdk.android.error.StoneStatus
import co.stone.posmobile.sdk.bluetooth.provider.BluetoothProvider
import co.stone.posmobile.sdk.callback.StoneResultCallback
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class BluetoothProviderWrapper {
    private val bluetoothProvider: BluetoothProvider
        get() = BluetoothProvider.create()

    suspend fun connect(deviceAddress: String): BluetoothStatus = suspendCancellableCoroutine { continuation ->
        bluetoothProvider.connect(
            pinpadAddress = deviceAddress,
            stoneResultCallback = object :
                StoneResultCallback<Boolean> {
                override fun onSuccess(result: Boolean) {
                    continuation.resume(BluetoothStatus.Success)
                }

                override fun onError(stoneStatus: StoneStatus?, throwable: Throwable) {
                    continuation.resume(
                        BluetoothStatus.Error(
                            stoneStatus?.message ?: throwable.message ?: "Erro desconhecido"
                        )
                    )
                }
            }
        )
    }
}

sealed class BluetoothStatus {
    data object Success : BluetoothStatus()
    data class Error(val errorMessage: String) : BluetoothStatus()
}
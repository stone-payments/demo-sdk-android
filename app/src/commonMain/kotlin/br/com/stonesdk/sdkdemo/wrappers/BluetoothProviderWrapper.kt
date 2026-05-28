package br.com.stonesdk.sdkdemo.wrappers

import br.com.stone.sdk.android.error.StoneStatus
import co.stone.posmobile.sdk.bluetooth.domain.model.BluetoothDevice
import co.stone.posmobile.sdk.bluetooth.provider.BluetoothProvider
import co.stone.posmobile.sdk.callback.StoneResultCallback
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class BluetoothProviderWrapper {

    private val bluetoothProvider: BluetoothProvider
        get() = BluetoothProvider.create()

    fun connect(deviceAddress: String): Flow<BluetoothConnectStatus> = channelFlow {
        bluetoothProvider.connect(
            pinpadAddress = deviceAddress,
            stoneResultCallback = object :
                StoneResultCallback<Boolean> {
                override fun onSuccess(result: Boolean) {
                    trySend(BluetoothConnectStatus.Success)
                }

                override fun onError(stoneStatus: StoneStatus?, throwable: Throwable) {
                    val errorMessage = stoneStatus?.message ?: throwable.message ?: "Erro desconhecido"
                    trySend(BluetoothConnectStatus.Error(errorMessage))
                }
            }
        )
        awaitClose {  }
    }

    suspend fun discoverPinpad(): BluetoothDiscoverStatus = suspendCancellableCoroutine { continuation ->
        bluetoothProvider.discoverPinpad(object : StoneResultCallback<List<BluetoothDevice>> {
            override fun onSuccess(result: List<BluetoothDevice>) {
                continuation.resume(
                    BluetoothDiscoverStatus.Success(result)
                )
            }

            override fun onError(stoneStatus: StoneStatus?, throwable: Throwable) {
                val errorMessage = stoneStatus?.message ?: throwable.message ?: "Erro desconhecido"
                continuation.resume(
                    BluetoothDiscoverStatus.Error(errorMessage)
                )
            }

        })
    }

    fun stopDiscover() {
        bluetoothProvider.stopDiscover()
    }

    fun disconnect() {
        bluetoothProvider.disconnect()
    }

}


sealed class BluetoothDiscoverStatus {
    data class Success(val devices: List<BluetoothDevice>) : BluetoothDiscoverStatus()
    data class Error(val errorMessage: String) : BluetoothDiscoverStatus()
}

sealed class BluetoothConnectStatus {
    data object Success : BluetoothConnectStatus()
    data class Error(val errorMessage: String) : BluetoothConnectStatus()
}
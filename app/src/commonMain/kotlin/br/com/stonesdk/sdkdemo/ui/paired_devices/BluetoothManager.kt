package br.com.stonesdk.sdkdemo.ui.paired_devices

import br.com.stone.sdk.android.error.StoneStatus
import co.stone.posmobile.sdk.bluetooth.domain.model.BluetoothDevice
import co.stone.posmobile.sdk.bluetooth.provider.BluetoothProvider
import co.stone.posmobile.sdk.callback.StoneResultCallback
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class BluetoothDeviceRepository() {
    val provider: BluetoothProvider
        get() = BluetoothProvider.create()

    val scope = CoroutineScope(Dispatchers.IO)

    fun startScan(): Flow<BluetoothDevice> = channelFlow {

        provider.discoverPinpad(object : StoneResultCallback<List<BluetoothDevice>> {
            override fun onSuccess(result: List<BluetoothDevice>) {
                result.forEach { trySend(it) }
            }

            override fun onError(stoneStatus: StoneStatus?, throwable: Throwable) {
                close(throwable)
            }
        })
        awaitClose {
            provider.stopDiscover()
        }

    }

    fun stopScan() {
        provider.stopDiscover()
    }

    fun disconnect() {
        provider.disconnect()
    }

    suspend fun connect(address: String): Result<Unit> {

        val channel = Channel<Result<Unit>>()

        provider.connect(
            pinpadAddress = address,
            stoneResultCallback = object : StoneResultCallback<Boolean> {
                override fun onSuccess(result: Boolean) {
                    scope.launch { channel.trySend(Result.success(Unit)) }
                }

                override fun onError(stoneStatus: StoneStatus?, throwable: Throwable) {
                    scope.launch { channel.trySend(Result.failure(throwable)) }
                }
            }

        )
        return channel.receive()
    }
}

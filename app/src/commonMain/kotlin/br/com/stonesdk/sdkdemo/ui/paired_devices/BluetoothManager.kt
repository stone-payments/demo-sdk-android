package br.com.stonesdk.sdkdemo.ui.paired_devices

import br.com.stone.sdk.android.error.StoneStatus
import co.stone.posmobile.sdk.bluetooth.domain.model.BluetoothDevice
import co.stone.posmobile.sdk.bluetooth.provider.BluetoothProvider
import co.stone.posmobile.sdk.callback.StoneResultCallback
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch

class BluetoothDeviceRepository() {
    val provider: BluetoothProvider
        get() = BluetoothProvider.create()

    val scope = CoroutineScope(Dispatchers.IO)

    fun startScan() = callbackFlow<List<BluetoothDevice>> {
        println(">>> startScan")

        provider.discoverPinpad(object : StoneResultCallback<List<BluetoothDevice>>{
            override fun onSuccess(result: List<BluetoothDevice>) {
                trySend(result)
            }

            override fun onError(stoneStatus: StoneStatus?, throwable: Throwable) {

            }
        })
    }

    fun stopScan() {
        println(">>> stopScan")
        provider.stopDiscover()
    }

    fun disconnect() {
        provider.disconnect()
    }

    suspend fun connect(address: String): Result<Unit> {
        println(">>> connect")

        val channel = Channel<Result<Unit>>()

        provider.connect(
            address,
            object : StoneResultCallback<Boolean> {
                override fun onError(stoneStatus: StoneStatus?, throwable: Throwable) {
                    scope.launch { channel.trySend(Result.failure(throwable)) }
                }

                override fun onSuccess(result: Boolean) {
                    scope.launch { channel.trySend(Result.success(Unit)) }
                }
            },
        )
        return channel.receive()
    }
}

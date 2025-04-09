package br.com.stonesdk.sdkdemo.activities.cancel

import br.com.stone.sdk.android.error.StoneStatus
import co.stone.posmobile.sdk.callback.StoneResultCallback
import co.stone.posmobile.sdk.cancellation.provider.CancellationProvider
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow

class CancelProviderWrapper {
    private val cancelProvider: CancellationProvider
        get() = CancellationProvider.create()

    fun cancelTransactionByItk(itk: String): Flow<CancelStatus> {
        return channelFlow {
            trySend(CancelStatus.Loading)
            cancelProvider.cancel(
                itk = itk,
                object : StoneResultCallback<Unit> {
                    override fun onSuccess(result: Unit) {
                        trySend(CancelStatus.Success)
                    }

                    override fun onError(stoneStatus: StoneStatus?, throwable: Throwable) {
                        val error = stoneStatus?.message ?: throwable.message ?: "Unknown error"
                        trySend(CancelStatus.Error(error))
                    }
                },
            )
            awaitClose { }
        }
    }

    sealed class CancelStatus {
        data object Loading : CancelStatus()

        data object Success : CancelStatus()

        data class Error(val error: String) : CancelStatus()
    }
}

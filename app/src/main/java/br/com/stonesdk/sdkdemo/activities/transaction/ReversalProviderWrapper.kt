package br.com.stonesdk.sdkdemo.activities.transaction

import br.com.stone.sdk.android.error.StoneStatus
import co.stone.posmobile.sdk.callback.StoneResultCallback
import co.stone.posmobile.sdk.reversal.provider.ReversalProvider
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch

class ReversalProviderWrapper {
    private val reversalProvider: ReversalProvider
        get() = ReversalProvider.create()

    fun reverseTransactions(): Flow<RevertTransactionsStatus> =
        callbackFlow {
            reversalProvider.reverseTransactions(
                object : StoneResultCallback<Unit> {
                    override fun onSuccess(result: Unit) {
                        launch { send(RevertTransactionsStatus.Completed) }
                    }

                    override fun onError(
                        stoneStatus: StoneStatus?,
                        throwable: Throwable,
                    ) {
                        val error = stoneStatus?.message ?: throwable.message ?: "Unknown error"
                        launch { send(RevertTransactionsStatus.Error(error)) }
                    }
                },
            )
            awaitClose { }
        }

    sealed class RevertTransactionsStatus {
        data object Completed : RevertTransactionsStatus()

        data class Error(
            val errorMessage: String,
        ) : RevertTransactionsStatus()
    }
}

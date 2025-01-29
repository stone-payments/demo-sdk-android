package br.com.stonesdk.sdkdemo.activities.main

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


    fun reverseTransactions(): Flow<TransactionRevertStatus> {
        return callbackFlow {
            reversalProvider.reverseTransactions(
                object : StoneResultCallback<Unit> {

                    override fun onSuccess(result: Unit) {
                        launch { send(TransactionRevertStatus.Success) }
                    }

                    override fun onError(stoneStatus: StoneStatus?, throwable: Throwable) {
                        val error = stoneStatus?.message ?: throwable.message ?: "Unknown error"
                        launch { send(TransactionRevertStatus.Error(error)) }
                    }
                }
            )
            awaitClose { }
        }
    }

    sealed class TransactionRevertStatus {
        data object Success : TransactionRevertStatus()
        data class Error(val errorMessage: String) : TransactionRevertStatus()
    }

}
package br.com.stonesdk.sdkdemo.activities.transaction

import br.com.stone.sdk.android.error.StoneStatus
import co.stone.posmobile.sdk.callback.StoneResultCallback
import co.stone.posmobile.sdk.payment.domain.model.response.PaymentData
import co.stone.posmobile.sdk.transactionList.provider.TransactionListProvider
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch

class TransactionListProviderWrapper {

    private val transactionListProvider: TransactionListProvider
        get() = TransactionListProvider.create()

    fun getAllTransactions(): Flow<TransactionListStatus> {
        return callbackFlow {
            transactionListProvider.getAllTransactions(
                object : StoneResultCallback<List<PaymentData>> {
                    override fun onSuccess(result: List<PaymentData>) {
                        launch { send(TransactionListStatus.Success(result)) }
                    }

                    override fun onError(stoneStatus: StoneStatus?, throwable: Throwable) {
                        val error = stoneStatus?.message ?: throwable.message ?: "Unknown error"
                        launch { send(TransactionListStatus.Error(error)) }
                    }
                }
            )
            awaitClose { }
        }
    }

    sealed class TransactionListStatus {
        data class Success(val transactions: List<PaymentData>) : TransactionListStatus()
        data class Error(val errorMessage: String) : TransactionListStatus()
    }

}
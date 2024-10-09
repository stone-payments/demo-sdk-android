package br.com.stonesdk.sdkdemo.activities.transaction

import android.content.Context
import kotlinx.coroutines.suspendCancellableCoroutine
import stone.application.enums.Action
import stone.application.interfaces.StoneActionCallback
import stone.providers.BaseTransactionProvider
import kotlin.coroutines.resume

class TransactionProviderWrapper(
    private val transactionProvider: BaseTransactionProvider,
    ) {
    suspend fun startTransaction(): TransactionStatus =
        suspendCancellableCoroutine { continuation ->
            transactionProvider.connectionCallback = object : StoneActionCallback {
                override fun onSuccess() {
                    continuation.resume(TransactionStatus.Success)
                }

                override fun onError() {
                    continuation.resume(TransactionStatus.Error)
                }

                override fun onStatusChanged(action: Action) {
                    continuation.resume(TransactionStatus.StatusChanged(action))
                }

            }
            transactionProvider.execute()
        }

     fun cancelTransaction() {
        transactionProvider.abortPayment()
    }
}

sealed class TransactionStatus {
    data object Success : TransactionStatus()
    data object Error : TransactionStatus()
    data class StatusChanged(val action: Action) : TransactionStatus()
}

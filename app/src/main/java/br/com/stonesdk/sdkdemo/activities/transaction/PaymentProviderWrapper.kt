package br.com.stonesdk.sdkdemo.activities.transaction

import br.com.stone.sdk.android.error.StoneStatus
import co.stone.posmobile.sdk.payment.domain.model.PaymentAction
import co.stone.posmobile.sdk.payment.domain.model.PaymentInput
import co.stone.posmobile.sdk.payment.domain.model.response.PaymentData
import co.stone.posmobile.sdk.payment.domain.model.response.StonePaymentResultCallback
import co.stone.posmobile.sdk.payment.provider.PaymentProvider
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class PaymentProviderWrapper {
    private val paymentProvider: PaymentProvider
        get() = PaymentProvider.create()

    fun startPayment(paymentInput: PaymentInput): Flow<TransactionStatus> {
        return callbackFlow {
            paymentProvider.startPayment(
                paymentInput = paymentInput,
                object : StonePaymentResultCallback<PaymentData> {
                    override fun onSuccess(result: PaymentData) {
                        trySend(TransactionStatus.Success)
                    }

                    override fun onError(stoneStatus: StoneStatus?, throwable: Throwable) {
                        val error = stoneStatus?.message ?: throwable.message ?: "Unknown error"
                        trySend(TransactionStatus.Error(error))
                    }

                    override fun onEvent(event: PaymentAction) {
                        trySend(TransactionStatus.StatusChanged(event))
                    }
                },
            )

            awaitClose { }
        }
    }

    sealed class TransactionStatus {
        data object Success : TransactionStatus()

        data class Error(val error: String) : TransactionStatus()

        data class StatusChanged(val action: PaymentAction) : TransactionStatus()
    }
}

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
import kotlinx.coroutines.launch

class PaymentProviderWrapper {

    private val paymentProvider: PaymentProvider
        get() = PaymentProvider.create()

    fun startPayment(paymentInput: PaymentInput): Flow<TransactionStatus> {
        return callbackFlow {
            paymentProvider.startPayment(
                paymentInput = paymentInput,
                object : StonePaymentResultCallback<PaymentData> {
                    override fun onSuccess(result: PaymentData) {
                        launch { send(TransactionStatus.Success) }
                    }

                    override fun onError(stoneStatus: StoneStatus?, throwable: Throwable) {
                        launch { send(TransactionStatus.Error) }
                    }

                    override fun onEvent(event: PaymentAction) {
                        launch { send(TransactionStatus.StatusChanged(event)) }
                    }
                }
            )

            awaitClose {  }
        }
    }

    sealed class TransactionStatus {
        data object Success : TransactionStatus()
        data object Error : TransactionStatus()
        data class StatusChanged(val action: PaymentAction) : TransactionStatus()
    }
}

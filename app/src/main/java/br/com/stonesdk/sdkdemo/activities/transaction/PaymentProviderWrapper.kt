package br.com.stonesdk.sdkdemo.activities.transaction

import br.com.stone.sdk.android.error.StoneStatus
import co.stone.posmobile.sdk.payment.domain.model.PaymentAction
import co.stone.posmobile.sdk.payment.domain.model.PaymentInput
import co.stone.posmobile.sdk.payment.domain.model.response.PaymentData
import co.stone.posmobile.sdk.payment.domain.model.response.StonePaymentResultCallback
import co.stone.posmobile.sdk.payment.provider.PaymentProvider
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class PaymentProviderWrapper(

) {

    private val paymentProvider: PaymentProvider
        get() = PaymentProvider.create()

    suspend fun startPayment(paymentInput: PaymentInput): TransactionStatus =
        suspendCancellableCoroutine { continuation ->

            paymentProvider.startPayment(paymentInput = paymentInput, object :
                StonePaymentResultCallback<PaymentData> {
                override fun onSuccess(result: PaymentData) {
                    continuation.resume(TransactionStatus.Success)
                }

                override fun onError(stoneStatus: StoneStatus?, throwable: Throwable) {
                    continuation.resume(TransactionStatus.Error)
                }

                override fun onEvent(event: PaymentAction) {
                    continuation.resume(TransactionStatus.StatusChanged(event))
                }

            })
        }

    sealed class TransactionStatus {
        data object Success : TransactionStatus()
        data object Error : TransactionStatus()
        data class StatusChanged(val action: PaymentAction) : TransactionStatus()
    }
}

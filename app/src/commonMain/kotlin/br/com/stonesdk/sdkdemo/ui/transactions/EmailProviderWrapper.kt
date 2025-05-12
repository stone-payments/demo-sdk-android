package br.com.stonesdk.sdkdemo.ui.transactions

import br.com.stone.sdk.android.error.StoneStatus
import co.stone.posmobile.sdk.callback.StoneResultCallback
import co.stone.posmobile.sdk.payment.domain.model.response.PaymentData
import co.stone.posmobile.sdk.payment.domain.model.response.TransactionStatus
import co.stone.posmobile.sdk.sendEmail.domain.model.EmailConfig
import co.stone.posmobile.sdk.sendEmail.domain.model.EmailReceiptType
import co.stone.posmobile.sdk.sendEmail.provider.EmailProvider
import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class EmailProviderWrapper {
    private val emailProvider: EmailProvider
        get() = EmailProvider.create()

    fun sendMail(
        paymentData: PaymentData,
    ): Flow<EmailStatus> {
        return callbackFlow {
            trySend(EmailStatus.Loading)

            val isTransactionCancelled =
                paymentData
                    .transactionStatus == TransactionStatus.CANCELLED

            val config = getEmailConfig()

            if (isTransactionCancelled) {
                sendPaymentEmail(config = config, paymentData = paymentData)
            } else {
                sendCancelEmail(config = config, paymentData = paymentData)
            }

            awaitClose { }
        }
    }

    private suspend fun ProducerScope<EmailStatus>.sendPaymentEmail(
        config: EmailConfig,
        paymentData: PaymentData,
    ) {
        emailProvider.sendPaymentEmail(
            config = config,
            paymentData = paymentData,
            stoneResultCallback =
                object : StoneResultCallback<Unit> {
                    override fun onSuccess(result: Unit) {
                        trySend(EmailStatus.Success)
                    }

                    override fun onError(stoneStatus: StoneStatus?, throwable: Throwable) {
                        val error = stoneStatus?.message ?: throwable.message ?: "Unknown error"
                        trySend(EmailStatus.Error(error))
                    }
                },
        )
    }

    private suspend fun ProducerScope<EmailStatus>.sendCancelEmail(
        config: EmailConfig,
        paymentData: PaymentData,
    ) {
        emailProvider.sendCancelEmail(
            config = config,
            initiatorTransactionKey = paymentData.initiatorTransactionKey,
            stoneResultCallback =
                object : StoneResultCallback<Unit> {
                    override fun onSuccess(result: Unit) {
                        trySend(EmailStatus.Success)
                    }

                    override fun onError(stoneStatus: StoneStatus?, throwable: Throwable) {
                        val error = stoneStatus?.message ?: throwable.message ?: "Unknown error"
                        trySend(EmailStatus.Error(error))
                    }
                },
        )
    }

    private fun getEmailConfig(): EmailConfig {
        return EmailConfig(
            from = MAILER_ADDRESS,
            to = listOf(RECIPIENT_ADDRESS),
            receiptType = EmailReceiptType.CLIENT,
        )
    }

    sealed class EmailStatus {
        data object Loading : EmailStatus()

        data object Success : EmailStatus()

        data class Error(val errorMessage: String) : EmailStatus()
    }

    companion object {
        const val MAILER_ADDRESS = ""
        const val RECIPIENT_ADDRESS = ""
    }
}

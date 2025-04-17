package br.com.stonesdk.sdkdemo.activities.transaction

import android.util.Log
import br.com.stone.sdk.android.error.StoneStatus
import co.stone.posmobile.sdk.merchant.domain.model.Merchant
import co.stone.posmobile.sdk.payment.domain.model.response.PaymentData
import co.stone.posmobile.sdk.sendEmail.domain.model.Contact
import co.stone.posmobile.sdk.sendEmail.domain.model.EmailConfig
import co.stone.posmobile.sdk.sendEmail.domain.model.EmailReceiptType
import co.stone.posmobile.sdk.sendEmail.provider.EmailProvider
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class EmailProviderWrapper {
    private val emailProvider: EmailProvider
        get() = EmailProvider.create()

    fun sendMail(
        paymentData: PaymentData,
        merchant: Merchant,
    ): Flow<EmailStatus> {
        return callbackFlow {
            trySend(EmailStatus.Loading)

            val config = getEmailConfig()

            emailProvider.sendEmail(
                config = config,
                data = paymentData,
                merchant = merchant,
                stoneResultCallback =
                    object : co.stone.posmobile.sdk.callback.StoneResultCallback<Unit> {
                        override fun onSuccess(result: Unit) {
                            Log.d("EmailProviderWrapper", "Email sent successfully")
                            trySend(EmailStatus.Success)
                        }

                        override fun onError(stoneStatus: StoneStatus?, throwable: Throwable) {
                            val error = stoneStatus?.message ?: throwable.message ?: "Unknown error"
                            Log.e("EmailProviderWrapper", "Error: $stoneStatus", throwable)
                            trySend(EmailStatus.Error(error))
                        }
                    },
            )

            awaitClose { }
        }
    }

    private fun getEmailConfig(): EmailConfig {
        return EmailConfig(
            from = Contact(MAILER_ADDRESS, "Stone Pagamentos"),
            to = listOf(Contact(RECIPIENT_ADDRESS, "Joao Carlos")),
            type = EmailReceiptType.CLIENT,
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

package br.com.stonesdk.sdkdemo.activities.transaction.list

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.stone.sdk.android.error.StoneStatus
import br.com.stonesdk.sdkdemo.utils.parseCentsToCurrency
import co.stone.posmobile.datacontainer.data.migration.Migration31to32.Companion.logger
import co.stone.posmobile.sdk.callback.StoneResultCallback
import co.stone.posmobile.sdk.merchant.domain.model.Merchant
import co.stone.posmobile.sdk.merchant.provider.MerchantProvider
import co.stone.posmobile.sdk.payment.domain.model.CardPaymentMethod
import co.stone.posmobile.sdk.payment.domain.model.InstallmentTransaction
import co.stone.posmobile.sdk.payment.domain.model.response.PaymentData
import co.stone.posmobile.sdk.sendEmail.domain.model.Contact
import co.stone.posmobile.sdk.sendEmail.domain.model.EmailConfig
import co.stone.posmobile.sdk.sendEmail.domain.model.EmailReceiptType
import co.stone.posmobile.sdk.sendEmail.provider.EmailProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resumeWithException

class TransactionListViewModel(
    val transactionProvider: TransactionListProviderWrapper
) : ViewModel() {

    private val _uiState: MutableStateFlow<TransactionListUiModel> =
        MutableStateFlow(TransactionListUiModel())
    val uiState: StateFlow<TransactionListUiModel> = _uiState.asStateFlow()
    private val merchantProvider: MerchantProvider
        get() = MerchantProvider.create()

    init {
        getTransactions()
    }

    private fun getTransactions() {

        viewModelScope.launch {
            _uiState.update { it.copy(loading = true) }
            transactionProvider.getAllTransactions().collect { status ->

                when (status) {
                    is TransactionListProviderWrapper.TransactionListStatus.Success -> {
                        merchantProvider.getFirstMerchant(object :
                            StoneResultCallback<Merchant> {
                            override fun onSuccess(result: Merchant) {
                                val transactions = status.transactions
                                    .sortedByDescending { it.transactionId }
                                    .map { transaction ->
                                        Transaction(
                                            id = transaction.transactionId.toString(),
                                            authorizedAmount = transaction.amountAuthorized.parseCentsToCurrency(),
                                            authorizationDate = transaction.time,
                                            atk = transaction.acquirerTransactionKey,
                                            status = transaction.transactionStatus.name,
                                            data = transaction,
                                            merchant = result
                                        )
                                    }
                                _uiState.update {
                                    it.copy(
                                        transactions = transactions,
                                        loading = false
                                    )
                                }
                            }

                            override fun onError(stoneStatus: StoneStatus?, throwable: Throwable) {
                                // Handle error
                            }
                        })

                    }

                    is TransactionListProviderWrapper.TransactionListStatus.Error -> {
                        _uiState.update {
                            it.copy(
                                transactions = emptyList(),
                                loading = false,
                                errorMessage = status.errorMessage
                            )
                        }
                    }
                }
            }
        }
    }

    fun onItemClick(transaction: Transaction) {
        viewModelScope.launch {
            val installmentTransaction: InstallmentTransaction =
                (transaction.data as? PaymentData.CardPaymentData)
                    ?.cardPaymentMethod
                    ?.takeIf { it is CardPaymentMethod.Credit }
                    ?.let { (it as CardPaymentMethod.Credit).installmentTransaction }
                    ?: InstallmentTransaction.None()
            EmailProvider.create().sendEmail(
                config = EmailConfig(
                    Contact("", ""),
                    listOf(Contact("", "")),
                    EmailReceiptType.MERCHANT
                ),
                data = transaction.data,
                receiptType = EmailReceiptType.MERCHANT,
                merchant = transaction.merchant,
                installmentTransaction = installmentTransaction

            )
        }
    }
}

data class Transaction(
    val id: String,
    val authorizedAmount: String,
    val authorizationDate: String,
    val atk: String?,
    val status: String,
    val data: PaymentData,
    val merchant: Merchant
)

data class TransactionListUiModel(
    val loading: Boolean = false,
    val errorMessage: String? = null,
    val transactions: List<Transaction> = emptyList()
)
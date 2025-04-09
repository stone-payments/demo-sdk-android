package br.com.stonesdk.sdkdemo.activities.transaction.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.stonesdk.sdkdemo.activities.transaction.list.TransactionListProviderWrapper.TransactionListStatus
import br.com.stonesdk.sdkdemo.utils.parseCentsToCurrency
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TransactionListViewModel(
    val transactionProvider: TransactionListProviderWrapper,
) : ViewModel() {
    private val _uiState: MutableStateFlow<TransactionListUiModel> =
        MutableStateFlow(TransactionListUiModel())
    val uiState: StateFlow<TransactionListUiModel> = _uiState.asStateFlow()

    init {
        getTransactions()
    }

    private fun getTransactions() {
        viewModelScope.launch {
            transactionProvider.getAllTransactions().collect { status ->
                when (status) {
                    TransactionListStatus.Loading -> {
                        _uiState.update { it.copy(loading = true) }
                    }

                    is TransactionListStatus.Success -> {
                        val transactions =
                            status.transactions
                                .sortedByDescending { it.transactionId }
                                .map { transaction ->

                                    Transaction(
                                        id = transaction.transactionId.toString(),
                                        affiliationCode = transaction.affiliationCode,
                                        authorizedAmount = transaction.amountAuthorized.parseCentsToCurrency(),
                                        authorizationDate = transaction.time,
                                        itk = transaction.initiatorTransactionKey,
                                        status = transaction.transactionStatus.name,
                                    )
                                }
                        _uiState.update {
                            it.copy(
                                transactions = transactions,
                                loading = false,
                            )
                        }
                    }

                    is TransactionListStatus.Error -> {
                        _uiState.update {
                            it.copy(
                                transactions = emptyList(),
                                loading = false,
                                errorMessage = status.errorMessage,
                            )
                        }
                    }
                }
            }
        }
    }

    fun onItemClick(transaction: Transaction) {
        viewModelScope.launch {
//            val installmentTransaction: InstallmentTransaction =
//                (transaction.data as? PaymentData.CardPaymentData)
//                    ?.cardPaymentMethod
//                    ?.takeIf { it is CardPaymentMethod.Credit }
//                    ?.let { (it as CardPaymentMethod.Credit).installmentTransaction }
//                    ?: InstallmentTransaction.None()
//            EmailProvider.create().sendEmail(
//                config = EmailConfig(
//                    Contact("", ""),
//                    listOf(Contact("", "")),
//                    EmailReceiptType.MERCHANT
//                ),
//                data = transaction.data,
//                receiptType = EmailReceiptType.MERCHANT,
//                merchant = transaction.merchant,
//                installmentTransaction = installmentTransaction
//
//            )
        }
    }
}

data class Transaction(
    val id: String,
    val affiliationCode: String,
    val authorizedAmount: String,
    val authorizationDate: String,
    val itk: String,
    val status: String,
)

data class TransactionListUiModel(
    val loading: Boolean = false,
    val errorMessage: String? = null,
    val transactions: List<Transaction> = emptyList(),
)

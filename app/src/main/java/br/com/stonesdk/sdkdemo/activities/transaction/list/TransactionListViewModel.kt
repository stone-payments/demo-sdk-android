package br.com.stonesdk.sdkdemo.activities.transaction.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.stonesdk.sdkdemo.activities.transaction.EmailProviderWrapper
import br.com.stonesdk.sdkdemo.activities.transaction.MerchantProviderWrapper
import br.com.stonesdk.sdkdemo.activities.transaction.MerchantProviderWrapper.MerchantByAffiliationCodeStatus
import br.com.stonesdk.sdkdemo.activities.transaction.list.TransactionListProviderWrapper.TransactionByIdStatus
import br.com.stonesdk.sdkdemo.activities.transaction.list.TransactionListProviderWrapper.TransactionListStatus
import br.com.stonesdk.sdkdemo.utils.parseCentsToCurrency
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TransactionListViewModel(
    val emailProviderWrapper: EmailProviderWrapper,
    val merchantProviderWrapper: MerchantProviderWrapper,
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
                                .map { paymentData ->

                                    Transaction(
                                        id = paymentData.transactionId,
                                        affiliationCode = paymentData.affiliationCode,
                                        authorizedAmount = paymentData.amountAuthorized.parseCentsToCurrency(),
                                        authorizationDate = paymentData.time,
                                        atk = paymentData.acquirerTransactionKey,
                                        status = paymentData.transactionStatus.name,
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
            val paymentDataResult =
                transactionProvider.getTransactionById(transactionId = transaction.id)
                    .first { TransactionByIdStatus.Loading != it }

            if (paymentDataResult is TransactionByIdStatus.Error) {
                _uiState.update {
                    it.copy(
                        transactions = emptyList(),
                        loading = false,
                        errorMessage = paymentDataResult.errorMessage,
                    )
                }
                return@launch
            }

            paymentDataResult as TransactionByIdStatus.Success
            if (paymentDataResult.transaction == null) {
                _uiState.update {
                    it.copy(
                        transactions = emptyList(),
                        loading = false,
                        errorMessage = "Transaction not found",
                    )
                }
                return@launch
            }

            val merchantResult =
                merchantProviderWrapper.getMerchantByAffiliationCode(affiliationCode = transaction.affiliationCode)
                    .first { MerchantByAffiliationCodeStatus.Loading != it }

            if (merchantResult is MerchantByAffiliationCodeStatus.Error) {
                _uiState.update {
                    it.copy(
                        transactions = emptyList(),
                        loading = false,
                        errorMessage = merchantResult.errorMessage,
                    )
                }
                return@launch
            }

            merchantResult as MerchantByAffiliationCodeStatus.Success
            if (merchantResult.merchant == null) {
                _uiState.update {
                    it.copy(
                        transactions = emptyList(),
                        loading = false,
                        errorMessage = "Merchant not found",
                    )
                }
                return@launch
            }

            emailProviderWrapper.sendMail(
                paymentData = paymentDataResult.transaction,
                merchant = merchantResult.merchant,
            ).collect { status ->
                when (status) {
                    EmailProviderWrapper.EmailStatus.Loading -> {
                        _uiState.update { it.copy(loading = true) }
                    }

                    is EmailProviderWrapper.EmailStatus.Success -> {
                        _uiState.update {
                            it.copy(
                                loading = false,
                            )
                        }
                    }

                    is EmailProviderWrapper.EmailStatus.Error -> {
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
}

data class Transaction(
    val id: Long,
    val affiliationCode: String,
    val authorizedAmount: String,
    val authorizationDate: String,
    val atk: String? = null,
    val status: String,
)

data class TransactionListUiModel(
    val loading: Boolean = false,
    val errorMessage: String? = null,
    val transactions: List<Transaction> = emptyList(),
)

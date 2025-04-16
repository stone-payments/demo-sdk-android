package br.com.stonesdk.sdkdemo.activities.cancel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.stonesdk.sdkdemo.activities.transaction.list.Transaction
import br.com.stonesdk.sdkdemo.activities.transaction.list.TransactionListProviderWrapper
import br.com.stonesdk.sdkdemo.activities.transaction.list.TransactionListProviderWrapper.TransactionListStatus
import br.com.stonesdk.sdkdemo.utils.parseCentsToCurrency
import co.stone.posmobile.sdk.payment.domain.model.response.TransactionStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CancelViewModel(
    val transactionProvider: TransactionListProviderWrapper,
    val cancelProvider: CancelProviderWrapper,
) : ViewModel() {
    private val _uiState: MutableStateFlow<CancelUiModel> =
        MutableStateFlow(CancelUiModel())
    val uiState: StateFlow<CancelUiModel> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            transactionProvider.getAllTransactions().collect { status ->
                when (status) {
                    is TransactionListStatus.Loading -> {
                        _uiState.update { it.copy(loading = true) }
                    }
                    is TransactionListStatus.Success -> {
                        val transactions =
                            status.transactions
                                .filter { it.transactionStatus == TransactionStatus.APPROVED }
                                .sortedByDescending { it.transactionId }
                                .map { transaction ->
                                    Transaction(
                                        id = transaction.transactionId.toString(),
                                        affiliationCode = transaction.affiliationCode,
                                        authorizedAmount = transaction.amountAuthorized.parseCentsToCurrency(),
                                        authorizationDate = transaction.time,
                                        atk = transaction.acquirerTransactionKey,
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
            val atk = transaction.atk

            if (atk == null) {
                _uiState.update {
                    it.copy(
                        transactions = emptyList(),
                        loading = false,
                        errorMessage = "Transaction not found",
                    )
                }
                return@launch
            }

            cancelProvider.cancelTransactionByAtk(transaction.atk).collect { status ->
                when (status) {
                    is CancelProviderWrapper.CancelStatus.Loading -> {
                        _uiState.update { it.copy(loading = true) }
                    }

                    is CancelProviderWrapper.CancelStatus.Success -> {
                        _uiState.update {
                            it.copy(
                                loading = false,
                                transactions = it.transactions.filter { t -> t.atk != transaction.atk },
                            )
                        }
                    }

                    is CancelProviderWrapper.CancelStatus.Error -> {
                        _uiState.update {
                            it.copy(
                                transactions = emptyList(),
                                loading = false,
                                errorMessage = status.error,
                            )
                        }
                    }
                }
            }
        }
    }
}

data class CancelUiModel(
    val loading: Boolean = false,
    val errorMessage: String? = null,
    val transactions: List<Transaction> = emptyList(),
)

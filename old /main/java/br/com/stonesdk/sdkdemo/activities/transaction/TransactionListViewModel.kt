package br.com.stonesdk.sdkdemo.activities.transaction

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.stonesdk.sdkdemo.utils.parseCentsToCurrency
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TransactionListViewModel(
    val transactionProvider: TransactionListProviderWrapper
) : ViewModel() {

    private val _uiState: MutableStateFlow<TransactionListUiModel> =
        MutableStateFlow(TransactionListUiModel())
    val uiState: StateFlow<TransactionListUiModel> = _uiState.asStateFlow()

    init {
        getTransactions()
    }

    private fun getTransactions() {

        viewModelScope.launch {
            _uiState.update { it.copy(loading = true) }
            transactionProvider.getAllTransactions().collect { status ->

                when (status) {
                    is TransactionListProviderWrapper.TransactionListStatus.Success -> {
                        val transactions = status.transactions
                            .sortedByDescending { it.transactionId }
                            .map { transaction ->
                                Transaction(
                                    id = transaction.transactionId.toString(),
                                    authorizedAmount = transaction.amountAuthorized.parseCentsToCurrency(),
                                    authorizationDate = transaction.time,
                                    atk = transaction.acquirerTransactionKey,
                                    status = transaction.transactionStatus.name
                                )
                            }
                        _uiState.update {
                            it.copy(
                                transactions = transactions,
                                loading = false
                            )
                        }
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
        // Handle item click
    }
}

data class Transaction(
    val id: String,
    val authorizedAmount: String,
    val authorizationDate: String,
    val atk: String?,
    val status: String
)

data class TransactionListUiModel(
    val loading: Boolean = false,
    val errorMessage : String? = null,
    val transactions: List<Transaction> = emptyList()
)
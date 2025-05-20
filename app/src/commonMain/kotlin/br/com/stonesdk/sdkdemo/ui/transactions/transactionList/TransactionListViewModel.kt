package br.com.stonesdk.sdkdemo.ui.transactions.transactionList

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.stone.sdk.android.error.StoneStatus
import br.com.stonesdk.sdkdemo.wrappers.TransactionListProviderWrapper
import co.stone.posmobile.sdk.callback.StoneResultCallback
import co.stone.posmobile.sdk.payment.domain.model.response.PaymentData
import co.stone.posmobile.sdk.transactionList.provider.TransactionListProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TransactionListViewModel(
    private val transactionListProvider : TransactionListProviderWrapper
) : ViewModel() {
    private val _uiState: MutableStateFlow<TransactionListUiModel> = MutableStateFlow(TransactionListUiModel())
    val uiState: StateFlow<TransactionListUiModel> = _uiState.asStateFlow()

    init {
        getTransactions()
    }

    private fun getTransactions() {
        viewModelScope.launch {
            transactionListProvider.getAllTransactions().collect{status ->
                when(status){
                    TransactionListProviderWrapper.TransactionListStatus.Loading -> {
                        _uiState.update { it.copy(loading = true) }
                    }
                    is TransactionListProviderWrapper.TransactionListStatus.Success ->{
                        val transactions =
                            status.transactions.sortedByDescending { it.transactionId }.map { transaction ->
                                Transaction(
                                    id = transaction.transactionId,
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
                    is TransactionListProviderWrapper.TransactionListStatus.Error -> {
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
        // Handle item click
    }
}

private fun Long.parseCentsToCurrency(): String {
    val value = this.toDouble() / 100
    return "R$ $value"
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

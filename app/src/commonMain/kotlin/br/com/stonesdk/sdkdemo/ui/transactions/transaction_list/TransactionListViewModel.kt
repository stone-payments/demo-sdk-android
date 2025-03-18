package br.com.stonesdk.sdkdemo.ui.transactions.transaction_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.stone.sdk.android.error.StoneStatus
import co.stone.posmobile.sdk.callback.StoneResultCallback
import co.stone.posmobile.sdk.payment.domain.model.response.PaymentData
import co.stone.posmobile.sdk.transactionList.provider.TransactionListProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TransactionListViewModel() : ViewModel() {

    private val _uiState: MutableStateFlow<TransactionListUiModel> =
        MutableStateFlow(TransactionListUiModel())
    val uiState: StateFlow<TransactionListUiModel> = _uiState.asStateFlow()

    init {
        getTransactions()
    }

    private fun getTransactions() {

        viewModelScope.launch {
            _uiState.update { it.copy(loading = true) }

            val provider = TransactionListProvider.create()
            provider.getAllTransactions(object : StoneResultCallback<List<PaymentData>> {
                override fun onError(stoneStatus: StoneStatus?, throwable: Throwable) {
                    _uiState.update {
                        it.copy(
                            transactions = emptyList(),
                            loading = false,
                            errorMessage = stoneStatus?.message ?: throwable.message
                        )
                    }
                }

                override fun onSuccess(result: List<PaymentData>) {
                    val transactions =
                        result.sortedByDescending { it.transactionId }
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
            })
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
    val id: String,
    val authorizedAmount: String,
    val authorizationDate: String,
    val atk: String?,
    val status: String
)

data class TransactionListUiModel(
    val loading: Boolean = false,
    val errorMessage: String? = null,
    val transactions: List<Transaction> = emptyList()
)
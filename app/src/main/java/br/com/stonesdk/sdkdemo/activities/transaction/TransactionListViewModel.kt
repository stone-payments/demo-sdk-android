package br.com.stonesdk.sdkdemo.activities.transaction

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

            transactionProvider.getAllTransactions()

            _uiState.update {
                it.copy(
                    transactions = listOf(
                        Transaction(
                            transactionId = "1",
                            transactionAmount = "R$ 100,00",
                            transactionStatus = "Aprovado"
                        ),
                        Transaction(
                            transactionId = "2",
                            transactionAmount = "R$ 200,00",
                            transactionStatus = "Reprovado"
                        ),
                        Transaction(
                            transactionId = "3",
                            transactionAmount = "R$ 300,00",
                            transactionStatus = "Aprovado"
                        )
                    )
                )
            }
        }
    }

    fun onItemClick(transaction: Transaction) {
        // Handle item click
    }
}

data class Transaction(
    val transactionId: String,
    val transactionAmount: String,
    val transactionStatus: String
)

data class TransactionListUiModel(
    val transactions: List<Transaction> = emptyList()
)
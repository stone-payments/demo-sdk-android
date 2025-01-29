package br.com.stonesdk.sdkdemo.activities.transaction.revert

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.stonesdk.sdkdemo.activities.main.ReversalProviderWrapper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TransactionRevertViewModel(
    private val reversalProviderWrapper: ReversalProviderWrapper
) : ViewModel() {

    private val _uiState: MutableStateFlow<TransactionRevertUiModel> =
        MutableStateFlow(TransactionRevertUiModel())
    val uiState: StateFlow<TransactionRevertUiModel> = _uiState.asStateFlow()

    init {
        revertTransactionsWithErrors()
    }

    private fun revertTransactionsWithErrors() {
        viewModelScope.launch {
            _uiState.value = TransactionRevertUiModel(loading = true)
            reversalProviderWrapper.reverseTransactions().collect {
                when (it) {
                    is ReversalProviderWrapper.TransactionRevertStatus.Success -> {
                        _uiState.value = TransactionRevertUiModel(
                            transactionsReverted = true,
                            loading = false
                        )
                    }

                    is ReversalProviderWrapper.TransactionRevertStatus.Error -> {
                        _uiState.value = TransactionRevertUiModel(
                            errorMessage = it.errorMessage,
                            loading = false
                        )
                    }
                }
            }
        }
    }

    data class TransactionRevertUiModel(
        val loading: Boolean = false,
        val errorMessage: String? = null,
        val transactionsReverted: Boolean = false,
    )

}
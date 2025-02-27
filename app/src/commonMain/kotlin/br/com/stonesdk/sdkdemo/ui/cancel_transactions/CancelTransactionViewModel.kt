package br.com.stonesdk.sdkdemo.ui.cancel_transactions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.stone.sdk.android.error.StoneStatus
import co.stone.posmobile.sdk.callback.StoneResultCallback
import co.stone.posmobile.sdk.reversal.provider.ReversalProvider
import kotlinx.coroutines.delay

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


sealed interface CancelTransactionState {
    data class Loading(val message: String) : CancelTransactionState
    data class Error(val code: String, val message: String) : CancelTransactionState
    data object Finish : CancelTransactionState
}

class CancelTransactionViewModel : ViewModel() {

    private val _uiState: MutableStateFlow<CancelTransactionState> =
        MutableStateFlow(CancelTransactionState.Loading("Cancelando transação com error"))
    val uiState: StateFlow<CancelTransactionState> = _uiState.asStateFlow()

    fun cancelTrasaction() {
        viewModelScope.launch {

            ReversalProvider.create().reverseTransactions(object : StoneResultCallback<Unit> {
                override fun onError(stoneStatus: StoneStatus?, throwable: Throwable) {
                    println(">>> stoneStatus: $stoneStatus, throwable: $throwable")
                    _uiState.update { CancelTransactionState.Error(stoneStatus?.code ?: "", stoneStatus?.message ?: "") }
                }

                override fun onSuccess(result: Unit) {
                    _uiState.update { CancelTransactionState.Finish }
                }
            })
        }
    }
}
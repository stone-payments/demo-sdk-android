package br.com.stonesdk.sdkdemo.ui.transactionList

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.stonesdk.sdkdemo.wrappers.EmailProviderWrapper
import br.com.stonesdk.sdkdemo.wrappers.TransactionListProviderWrapper.TransactionByIdStatus
import br.com.stonesdk.sdkdemo.wrappers.TransactionListProviderWrapper.TransactionListStatus
import br.com.stonesdk.sdkdemo.utils.parseCentsToCurrency
import br.com.stonesdk.sdkdemo.wrappers.TransactionListProviderWrapper
import co.stone.posmobile.sdk.payment.domain.model.response.PaymentData
import co.stone.posmobile.sdk.sendEmail.domain.model.EmailReceiptType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TransactionListViewModel(
    val transactionProvider: TransactionListProviderWrapper,
    val emailProviderWrapper: EmailProviderWrapper,
) : ViewModel() {
    private val _uiState: MutableStateFlow<TransactionListUiModel> = MutableStateFlow(TransactionListUiModel())

    private val _sendMailState = MutableStateFlow(SendMailModel())

    val uiState: StateFlow<TransactionListUiModel> =
        combine(_uiState, _sendMailState) { uiState, emailState ->
            val sendMailState = emailState.sendMailState
            val paymentData = emailState.paymentData

            when (sendMailState) {
                SendMailState.Idle -> {
                    if (paymentData != null) {
                        sendMail(paymentData, EmailReceiptType.MERCHANT)
                    }
                    uiState
                }

                SendMailState.Sending -> {
                    uiState.copy(
                        loading = true,
                    )
                }

                SendMailState.SendMerchantSuccess -> {
                    if (paymentData != null) {
                        sendMail(paymentData, EmailReceiptType.CLIENT)
                    }
                    uiState
                }

                SendMailState.SendClientSuccess -> {
                    _sendMailState.update { it.copy(sendMailState = SendMailState.Completed) }
                    uiState
                }

                SendMailState.Completed -> {
                    _sendMailState.update {
                        it.copy(
                            sendMailState = SendMailState.Idle,
                            paymentData = null,
                        )
                    }
                    uiState
                }

                is SendMailState.SendMailError -> {
                    uiState.copy(
                        loading = false,
                        errorMessage = sendMailState.error,
                    )
                }
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), TransactionListUiModel())

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
                            status.transactions.sortedByDescending { it.transactionId }.map { paymentData ->

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
                transactionProvider.getTransactionById(transactionId = transaction.id).first { TransactionByIdStatus.Loading != it }

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

            _sendMailState.update {
                it.copy(paymentData = paymentDataResult.transaction)
            }
        }
    }

    private fun sendMail(
        paymentData: PaymentData,
        receiptType: EmailReceiptType,
    ) {
        viewModelScope.launch {
            emailProviderWrapper
                .sendMail(
                    paymentData = paymentData,
                    receiptType = receiptType,
                ).collect { status ->
                    when (status) {
                        EmailProviderWrapper.EmailStatus.Loading -> {
                            _sendMailState.update { it.copy(sendMailState = SendMailState.Sending) }
                        }

                        is EmailProviderWrapper.EmailStatus.Success -> {
                            val state =
                                if (receiptType == EmailReceiptType.MERCHANT) {
                                    SendMailState.SendMerchantSuccess
                                } else {
                                    SendMailState.SendClientSuccess
                                }
                            _sendMailState.update { it.copy(sendMailState = state) }
                        }

                        is EmailProviderWrapper.EmailStatus.Error -> {
                            _sendMailState.update { it.copy(sendMailState = SendMailState.SendMailError(status.errorMessage)) }
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

data class SendMailModel(
    val sendMailState: SendMailState = SendMailState.Idle,
    val paymentData: PaymentData? = null,
)

sealed class SendMailState {
    data object Idle : SendMailState()

    data object Sending : SendMailState()

    data object SendMerchantSuccess : SendMailState()

    data object SendClientSuccess : SendMailState()

    data class SendMailError(
        val error: String? = null,
    ) : SendMailState()

    data object Completed : SendMailState()
}

package br.com.stonesdk.sdkdemo.activities.transaction

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.stonesdk.sdkdemo.activities.devices.DeviceInfoProviderWrapper
import br.com.stonesdk.sdkdemo.activities.manageStoneCode.ActivationProviderWrapper
import br.com.stonesdk.sdkdemo.activities.transaction.PaymentProviderWrapper.TransactionStatus
import co.stone.posmobile.sdk.payment.domain.model.CardPaymentMethod
import co.stone.posmobile.sdk.payment.domain.model.InstallmentTransaction
import co.stone.posmobile.sdk.payment.domain.model.PaymentInput
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TransactionViewModel(
    private val activationProviderWrapper: ActivationProviderWrapper,
    private val deviceInfoProviderWrapper: DeviceInfoProviderWrapper,
    private val installmentProvider: InstallmentProvider,
    private val paymentProviderWrapper: PaymentProviderWrapper
) : ViewModel() {

    private val _uiState: MutableStateFlow<TransactionUiModel> =
        MutableStateFlow(TransactionUiModel())
    val uiState: StateFlow<TransactionUiModel> = _uiState.asStateFlow()

    init {
        val isPosDevice = deviceInfoProviderWrapper.isPosDevice()

        val typeOfTransactions = if (isPosDevice) {
            listOf(
                TypeOfTransactionEnum.CREDIT,
                TypeOfTransactionEnum.DEBIT,
                TypeOfTransactionEnum.VOUCHER,
                TypeOfTransactionEnum.PIX
            )
        } else {
            listOf(
                TypeOfTransactionEnum.CREDIT,
                TypeOfTransactionEnum.DEBIT,
                TypeOfTransactionEnum.VOUCHER
            )
        }

        viewModelScope.launch {
            val activatedAffiliationCodes = activationProviderWrapper.getActivatedAffiliationCodes()
            val selectedAffiliationCode = activatedAffiliationCodes.firstOrNull().toString()
            _uiState.update {
                it.copy(
                    affiliationCodes = activatedAffiliationCodes,
                    selectedAffiliationCode = selectedAffiliationCode,
                    typeOfTransactions = typeOfTransactions
                )
            }
        }

        updateInstallments(TypeOfTransactionEnum.CREDIT)
    }

    fun onEvent(event: TransactionEvent) {
        when (event) {
            is TransactionEvent.UserInput -> {
                _uiState.update { it.copy(amount = event.amount) }
            }

            is TransactionEvent.CancelTransaction -> {
                //providerWrapper.cancelTransaction()
            }

            is TransactionEvent.OnInstallmentSelected -> onInstallmentSelected(event.installmentTransaction)
            is TransactionEvent.SendTransaction -> startTransaction()
            is TransactionEvent.OnAffiliationCodeSelected -> updateSelectedStoneCode(event.affiliationCode)
            is TransactionEvent.TypeOfTransaction -> updateTransactionType(event.type)
            is TransactionEvent.CheckBoxChanged -> {
                _uiState.update {
                    it.copy(shouldCaptureTransaction = event.isChecked)
                }
            }
        }
    }


    private fun updateInstallments(type: TypeOfTransactionEnum) {
        val installments = installmentProvider.getInstallment(
            transactionType = type,
            isPos = deviceInfoProviderWrapper.isPosDevice()
        )

        _uiState.update {
            it.copy(
                selectedTypeOfTransaction = type,
                installments = installments,
                selectedInstallment = installments.first()
            )
        }
    }

    fun startTransaction() {
        viewModelScope.launch {

            val amount = uiState.value.amount.toLongOrNull() ?: 0
            val captureTransaction = uiState.value.shouldCaptureTransaction
            val selectedInstallment = uiState.value.selectedInstallment
            val selectedAffiliationCode = uiState.value.selectedAffiliationCode
            val isContactlessEnabled = true
            val orderId = null

            val paymentInput = PaymentInput.CardPaymentInput(
                amount = amount,
                capture = captureTransaction,
                cardPaymentMethod = CardPaymentMethod.Credit(
                    installmentTransaction = selectedInstallment,
                ),
                affiliationCode = selectedAffiliationCode,
                isContactlessEnabled = isContactlessEnabled,
                orderId = orderId
            )

            paymentProviderWrapper.startPayment(paymentInput).collectLatest { status ->
                when (status) {
                    is TransactionStatus.Success -> {
                        _uiState.update {
                            it.copy(success = true, error = false)
                        }
                    }

                    is TransactionStatus.Error -> {
                        _uiState.update {
                            it.copy(success = false, error = true)
                        }
                    }

                    is TransactionStatus.StatusChanged -> {
                        val newLogMessage = status.action.toString()
                        val previousLogMessages: MutableList<String> =
                            uiState.value.logMessages.toMutableList()
                        previousLogMessages.add(newLogMessage)
                        _uiState.update {
                            it.copy(logMessages = previousLogMessages.toList())
                        }
                    }
                }
            }
        }
    }

    private fun updateTransactionType(type: TypeOfTransactionEnum) {
        updateInstallments(type)
    }

    private fun onInstallmentSelected(installmentTransaction: InstallmentTransaction) {
        _uiState.update {
            it.copy(selectedInstallment = installmentTransaction)
        }
    }

    private fun updateSelectedStoneCode(affiliationCode : String) {
        _uiState.update {
            it.copy(selectedAffiliationCode = affiliationCode)
        }
    }
}

fun List<InstallmentTransaction>.mapInstallmentsToPresentation(): List<String> {
    return this.map { installmentTransaction -> installmentTransaction.mapInstallmentToPresentation() }
}

fun InstallmentTransaction.mapInstallmentToPresentation(): String {
    val installmentCount = this.installmentNumber
    val installmentInterest = if (this.interest) "com juros" else "sem juros"
    return when (installmentCount == 1) {
        true -> "À Vista"
        false -> "${installmentCount}x $installmentInterest"
    }
}

data class TransactionUiModel(
    val error: Boolean = false,
    val success: Boolean = false,
    val amount: String = "",
    val typeOfTransactions: List<TypeOfTransactionEnum> = emptyList(),
    val selectedTypeOfTransaction: TypeOfTransactionEnum = TypeOfTransactionEnum.CREDIT,
    val installments: List<InstallmentTransaction> = emptyList(),
    val selectedInstallment: InstallmentTransaction = InstallmentTransaction.Merchant(1),
    val affiliationCodes: List<String> = emptyList(),
    val selectedAffiliationCode: String = "",
    val logMessages: List<String> = emptyList(),
    val shouldCaptureTransaction: Boolean = false
)

enum class TypeOfTransactionEnum(val displayName: String) {

    CREDIT("Crédito"), DEBIT("Débito"), VOUCHER("Voucher"), PIX("Pix");

    companion object {

        fun fromType(type: String): TypeOfTransactionEnum? {
            return entries.firstOrNull { it.name == type }
        }
    }
}

sealed interface TransactionEvent {
    data class UserInput(val amount: String) : TransactionEvent
    data class OnInstallmentSelected(val installmentTransaction: InstallmentTransaction) :
        TransactionEvent

    data class TypeOfTransaction(val type: TypeOfTransactionEnum) : TransactionEvent
    data class OnAffiliationCodeSelected(val affiliationCode: String) : TransactionEvent
    data object SendTransaction : TransactionEvent
    data object CancelTransaction : TransactionEvent
    data class CheckBoxChanged(val isChecked: Boolean) : TransactionEvent
}
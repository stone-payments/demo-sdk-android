package br.com.stonesdk.sdkdemo.activities.transaction

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.stonesdk.sdkdemo.activities.transaction.PaymentProviderWrapper.TransactionStatus
import co.stone.posmobile.sdk.payment.domain.model.CardPaymentMethod
import co.stone.posmobile.sdk.payment.domain.model.InstallmentTransaction
import co.stone.posmobile.sdk.payment.domain.model.PaymentInput
import kotlinx.coroutines.launch


class TransactionViewModel(
    private val installmentProvider: InstallmentProvider,
    private val paymentProviderWrapper: PaymentProviderWrapper,
) : ViewModel() {

    var viewState by mutableStateOf(TransactionUiModel())
        private set

    init {
        updateInstallments(TypeOfTransactionEnum.CREDIT)
    }

    fun onEvent(event: TransactionEvent) {
        when (event) {
            is TransactionEvent.UserInput -> viewState = viewState.copy(amount = event.amount)
            is TransactionEvent.CancelTransaction -> {
                //providerWrapper.cancelTransaction()
            }

            is TransactionEvent.InstallmentSelected -> onInstallmentSelected(event.installmentTransaction)
            is TransactionEvent.SendTransaction -> startTransaction()
            is TransactionEvent.StoneCodeItemClick -> updateSelectedStoneCode()
            is TransactionEvent.TypeOfTransaction -> updateTransactionType(event.type)
            is TransactionEvent.CheckBoxChanged -> viewState =
                viewState.copy(isCheckBox = event.isChecked)
        }
    }


    private fun updateInstallments(type: TypeOfTransactionEnum) {
        val installments = installmentProvider.getInstallment(type)

        viewState = viewState.copy(
            selectedTransaction = type, installments = installments
        )
    }

    fun startTransaction() {
        viewModelScope.launch {

            val paymentInput = PaymentInput.CardPaymentInput(
                amount = 100, capture = false, cardPaymentMethod = CardPaymentMethod.Credit(
                    installmentTransaction = viewState.selectedInstallment,
                ), affiliationCode = "846873720", isContactlessEnabled = true, orderId = null
            )

            paymentProviderWrapper.startPayment(paymentInput).let { status ->
                viewState = when (status) {
                    is TransactionStatus.Success -> viewState.copy(success = true, error = false)
                    is TransactionStatus.Error -> viewState.copy(success = false, error = true)
                    is TransactionStatus.StatusChanged -> viewState.copy(logMessages = status.action.toString())
                }
            }
        }
    }

    private fun updateTransactionType(type: TypeOfTransactionEnum) {
        updateInstallments(type)
    }

    private fun onInstallmentSelected(installmentTransaction: InstallmentTransaction) {
        viewState = viewState.copy(selectedInstallment = installmentTransaction)
    }

    private fun updateSelectedStoneCode() {
//        val stoneCode = sessionApplication.userModelList
//            .map { userModel -> userModel.stoneCode }
//            .toList()
////            Stone.getUserModel(position)
//        viewState = viewState.copy(selectedStoneCode = stoneCode)

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
    val installments: List<InstallmentTransaction> = emptyList(),
    val selectedTransaction: TypeOfTransactionEnum = TypeOfTransactionEnum.CREDIT,
    val selectedInstallment: InstallmentTransaction = InstallmentTransaction.Merchant(1),
    val amount: String = "",
    val selectedStoneCode: List<String> = emptyList(),
    val logMessages: String = "",
    val isCheckBox: Boolean = false
)

enum class TypeOfTransactionEnum {
    CREDIT, DEBIT, VOUCHER, PIX
}

sealed interface TransactionEvent {
    data class UserInput(val amount: String) : TransactionEvent
    data class InstallmentSelected(val installmentTransaction: InstallmentTransaction) :
        TransactionEvent

    data class TypeOfTransaction(val type: TypeOfTransactionEnum) : TransactionEvent
    data class StoneCodeItemClick(val position: Int) : TransactionEvent
    data object SendTransaction : TransactionEvent
    data object CancelTransaction : TransactionEvent
    data class CheckBoxChanged(val isChecked: Boolean) : TransactionEvent
}
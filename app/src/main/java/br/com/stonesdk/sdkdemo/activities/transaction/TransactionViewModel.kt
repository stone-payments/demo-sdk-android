package br.com.stonesdk.sdkdemo.activities.transaction

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.stonesdk.sdkdemo.activities.transaction.TransactionEvent.CancelTransaction
import br.com.stonesdk.sdkdemo.activities.transaction.TransactionEvent.CheckBoxChanged
import br.com.stonesdk.sdkdemo.activities.transaction.TransactionEvent.InstallmentSelected
import br.com.stonesdk.sdkdemo.activities.transaction.TransactionEvent.SendTransaction
import br.com.stonesdk.sdkdemo.activities.transaction.TransactionEvent.StoneCodeItemClick
import br.com.stonesdk.sdkdemo.activities.transaction.TransactionEvent.TypeOfTransaction
import br.com.stonesdk.sdkdemo.activities.transaction.TransactionEvent.UserInput
import kotlinx.coroutines.launch
import stone.application.SessionApplication
import stone.application.enums.InstalmentTransactionEnum
import stone.application.enums.TypeOfTransactionEnum
import stone.database.transaction.TransactionObject

class TransactionViewModel(
    private val installmentProvider: InstallmentProvider,
    // private val providerWrapper: TransactionProviderWrapper,
    private val transactionObject: TransactionObject,
    private val sessionApplication: SessionApplication,

    ) : ViewModel() {

    var viewState by mutableStateOf(TransactionUiModel())
        private set

    init {
        val installments = installmentProvider
            .getInstallment(transaction = TypeOfTransactionEnum.CREDIT)
            .mapInstallmentsToPresentation()
//
//        viewState = viewState.copy(
//            selectedTransaction = TypeOfTransactionEnum.CREDIT,
//            installmentList = installments
//        )
//        viewState = viewState.copy(
//            selectedTransaction = TypeOfTransactionEnum.CREDIT,
//            installmentList = installments,
//            selectedStoneCode =
//        )
        updateInstallments(TypeOfTransactionEnum.CREDIT)
        updateSelectedStoneCode()
    }

    //[]
    fun onEvent(event: TransactionEvent) {
        when (event) {
            is UserInput -> viewState = viewState.copy(amount = event.amount)
            CancelTransaction -> {
                //providerWrapper.cancelTransaction()
            }

            is InstallmentSelected -> onInstallmentSelected(event.position)
            SendTransaction -> startTransaction()
            is StoneCodeItemClick -> updateSelectedStoneCode()
            is TypeOfTransaction -> updateTransactionType(event.type)
            is CheckBoxChanged -> viewState = viewState.copy(isCheckBox = event.isChecked)
        }
    }

    private fun List<InstalmentTransactionEnum>.mapInstallmentsToPresentation(): List<String> {
        return this.map { installment ->
            when (installment == InstalmentTransactionEnum.ONE_INSTALMENT) {
                true -> "À Vista"
                false -> {
                    "${installment.count}x ${if (installment.interest) "com juros" else "sem juros"}"
                }
            }
        }
    }

    private fun updateInstallments(type: TypeOfTransactionEnum) {
        val installments = installmentProvider
            .getInstallment(type)
            .mapInstallmentsToPresentation()

        viewState = viewState.copy(
            selectedTransaction = type,
            installmentList = installments
        )
    }

    fun startTransaction() {
        viewModelScope.launch {
//            providerWrapper.startTransaction().apply { status ->
//                when (status) {
//                    is TransactionStatus.Success -> viewState = viewState.copy(success = true)
//                    is TransactionStatus.Error -> viewState = viewState.copy(error = true)
//                    is TransactionStatus.StatusChanged -> viewState = viewState.copy(
//                        logMessages = viewState.logMessages
//                        "\n" + status.action.name
//                    )
//                }
//            }
        }
    }

    private fun updateTransactionType(type: TypeOfTransactionEnum) {
        updateInstallments(type)
        transactionObject.typeOfTransaction = type
    }

    private fun onInstallmentSelected(position: Int) {
        val installment = InstalmentTransactionEnum.getAt(position)
        viewState = viewState.copy(selectedInstallment = installment)
        transactionObject.instalmentTransaction = installment
    }

    private fun updateSelectedStoneCode() {
        val stoneCode = sessionApplication.userModelList
            .map { userModel -> userModel.stoneCode }
            .toList()
//            Stone.getUserModel(position)
        viewState = viewState.copy(selectedStoneCode = stoneCode)
    }
}

data class TransactionUiModel(
    val error: Boolean = false,
    val success: Boolean = false,
    val installmentList: List<String> = emptyList(),
    val selectedTransaction: TypeOfTransactionEnum = TypeOfTransactionEnum.CREDIT,
    val selectedInstallment: InstalmentTransactionEnum = InstalmentTransactionEnum.ONE_INSTALMENT,
    val amount: String = "",
    val selectedStoneCode: List<String> = emptyList(),
    val logMessages: String = "",
    val isCheckBox: Boolean = false
)

sealed interface TransactionEvent {
    data class UserInput(val amount: String) : TransactionEvent
    data class InstallmentSelected(val position: Int) : TransactionEvent
    data class TypeOfTransaction(val type: TypeOfTransactionEnum) : TransactionEvent
    data class StoneCodeItemClick(val position: Int) : TransactionEvent
    data object SendTransaction : TransactionEvent
    data object CancelTransaction : TransactionEvent
    data class CheckBoxChanged(val isChecked: Boolean) : TransactionEvent
}
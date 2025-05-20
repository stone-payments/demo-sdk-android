@file:OptIn(ExperimentalLayoutApi::class)

package br.com.stonesdk.sdkdemo.ui.transactions

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.Checkbox
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.RadioButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import br.com.stonesdk.sdkdemo.ui.components.BaseSpinner
import br.com.stonesdk.sdkdemo.utils.parseCurrencyToCents
import co.stone.posmobile.sdk.payment.domain.model.InstallmentTransaction
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun TransactionScreen(
    viewModel: TransactionViewModel = koinViewModel()
) {
    val uiState = viewModel.uiState.collectAsState()

    val desiredAmount = remember { derivedStateOf { uiState.value.amount } }
    val transactionButtonEnabled = remember {
        derivedStateOf {
            desiredAmount.value.parseCurrencyToCents() > 0
        }
    }

    val typeOfTransactions = remember {
        derivedStateOf {
            uiState.value.typeOfTransactions
        }
    }
    val selectedTypeOfTransaction = remember {
        derivedStateOf {
            uiState.value.selectedTypeOfTransaction
        }
    }

    val installments = remember { derivedStateOf { uiState.value.installments } }
    val selectedInstallment = remember { derivedStateOf { uiState.value.selectedInstallment } }
    val showInstallmentSelection = remember {
        derivedStateOf {
            selectedTypeOfTransaction.value == TypeOfTransactionEnum.CREDIT
        }
    }

    val shouldCaptureTransaction =
        remember { derivedStateOf { uiState.value.shouldCaptureTransaction } }

    val affiliationCodes = remember { derivedStateOf { uiState.value.affiliationCodes } }
    val selectedAffiliationCode =
        remember { derivedStateOf { uiState.value.selectedAffiliationCode } }
    val showAffiliationCodeSelection = remember {
        derivedStateOf {
            affiliationCodes.value.size > 1
        }
    }



    TransactionContent(
        desiredAmount = desiredAmount.value,
        typeOfTransactions = typeOfTransactions.value,
        selectedTypeOfTransaction = selectedTypeOfTransaction.value,
        showInstallmentSelection = showInstallmentSelection.value,
        installments = installments.value,
        selectedInstallment = selectedInstallment.value,
        affiliationCodes = affiliationCodes.value,
        selectedAffiliationCode = selectedAffiliationCode.value,
        showAffiliationCodeSelection = showAffiliationCodeSelection.value,
        shouldCaptureTransaction = shouldCaptureTransaction.value,
        transactionButtonEnabled = transactionButtonEnabled.value,
        onEvent = viewModel::onEvent
    )

}

@Composable
fun TransactionContent(
    desiredAmount: String,
    typeOfTransactions: List<TypeOfTransactionEnum>,
    selectedTypeOfTransaction: TypeOfTransactionEnum,
    showInstallmentSelection: Boolean,
    installments: List<InstallmentTransaction>,
    selectedInstallment: InstallmentTransaction,
    affiliationCodes: List<String>,
    selectedAffiliationCode: String,
    showAffiliationCodeSelection: Boolean,
    shouldCaptureTransaction: Boolean,
    transactionButtonEnabled: Boolean,
    onEvent: (TransactionEvent) -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(4.dp)
            .imePadding(),
        verticalArrangement = Arrangement.Top
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = desiredAmount,
                readOnly = false,
                onValueChange = { amount -> onEvent(TransactionEvent.UserInput(amount)) },
                label = { Text(text = "Digite o valor") },
                modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 2.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            RadioButtonGroup(
                onEvent = onEvent,
                selectedTransactionType = selectedTypeOfTransaction,
                transactionTypes = typeOfTransactions
            )

            AnimatedVisibility(showInstallmentSelection) {
                Spacer(modifier = Modifier.width(4.dp))
                BaseSpinner(
                    title = "Nº de parcelas",
                    onItemSelected = { installment ->
                        onEvent(
                            TransactionEvent.OnInstallmentSelected(
                                installment
                            )
                        )
                    },
                    selectedElement = selectedInstallment,
                    elements = installments,
                    elementNaming = { installment -> installment.mapInstallmentToPresentation() }
                )
            }
        }

        AnimatedVisibility(showAffiliationCodeSelection) {
            Spacer(modifier = Modifier.width(4.dp))
            BaseSpinner(
                title = "Código de Afiliação",
                onItemSelected = { affiliationCode ->
                    onEvent(
                        TransactionEvent.OnAffiliationCodeSelected(
                            affiliationCode
                        )
                    )
                },
                selectedElement = selectedAffiliationCode,
                elements = affiliationCodes,
                elementNaming = { affiliationCode -> affiliationCode }
            )
        }

        CheckboxCapture(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            onEvent = onEvent,
            label = "Transação com Captura",
            checked = shouldCaptureTransaction
        )

        // push button to bottom of page
        Spacer(modifier = Modifier.weight(1f))

        Button(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            enabled = transactionButtonEnabled,
            onClick = { onEvent(TransactionEvent.SendTransaction) },
        ) {
            Text(text = "Enviar Transação")
        }
    }
}

@Composable
fun RadioButtonGroup(
    onEvent: (TransactionEvent) -> Unit,
    transactionTypes: List<TypeOfTransactionEnum>,
    selectedTransactionType: TypeOfTransactionEnum
) {

    FlowRow {
        transactionTypes.forEach { type ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                RadioButton(
                    selected = (type == selectedTransactionType),
                    onClick = { onEvent(TransactionEvent.TypeOfTransaction(type)) }
                )
                Text(
                    text = type.displayName,
                )
            }
        }
    }
}

@Composable
fun CheckboxCapture(
    checked: Boolean,
    onEvent: (TransactionEvent) -> Unit,
    label: String,
    modifier: Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = { isChecked -> onEvent(TransactionEvent.CheckBoxChanged(isChecked)) }
        )
        Text(text = label)
    }
}


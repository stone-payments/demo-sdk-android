@file:OptIn(ExperimentalLayoutApi::class)

package br.com.stonesdk.sdkdemo.activities.transaction

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import br.com.stonesdk.sdkdemo.R
import br.com.stonesdk.sdkdemo.activities.transaction.TransactionEvent.CancelTransaction
import br.com.stonesdk.sdkdemo.activities.transaction.TransactionEvent.InstallmentSelected
import br.com.stonesdk.sdkdemo.activities.transaction.TransactionEvent.SendTransaction
import br.com.stonesdk.sdkdemo.activities.transaction.TransactionEvent.TypeOfTransaction
import br.com.stonesdk.sdkdemo.activities.transaction.TransactionEvent.UserInput
import co.stone.posmobile.sdk.payment.domain.model.InstallmentTransaction
import org.koin.androidx.compose.koinViewModel

@Composable
internal fun TransactionScreen(
    viewModel: TransactionViewModel = koinViewModel()
) {
    val uiState = viewModel.uiState.collectAsState()

    val desiredAmount = remember { derivedStateOf { uiState.value.amount } }

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
    val showInstallmentSelection = remember {
        derivedStateOf {
            selectedTypeOfTransaction.value == TypeOfTransactionEnum.CREDIT
        }
    }

    val installments = remember { derivedStateOf { uiState.value.installments } }
    val selectedInstallment = remember { derivedStateOf { uiState.value.selectedInstallment } }

    val shouldCaptureTransaction = remember { derivedStateOf { uiState.value.shouldCaptureTransaction } }

    val stoneCodes = remember { derivedStateOf { uiState.value.stoneCodes } }
    val selectedStoneCode = remember { derivedStateOf { uiState.value.selectedStoneCode } }

    TransactionContent(
        desiredAmount = desiredAmount.value,
        typeOfTransactions = typeOfTransactions.value,
        selectedTypeOfTransaction = selectedTypeOfTransaction.value,
        showInstallmentSelection = showInstallmentSelection.value,
        installments = installments.value,
        selectedInstallment = selectedInstallment.value,
        stoneCodes = stoneCodes.value,
        selectedStoneCode = selectedStoneCode.value,
        shouldCaptureTransaction = shouldCaptureTransaction.value,
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
    stoneCodes: List<String>,
    selectedStoneCode: String,
    shouldCaptureTransaction: Boolean,
    onEvent: (TransactionEvent) -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(4.dp),
        verticalArrangement = Arrangement.Top
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = desiredAmount,
                readOnly = false,
                onValueChange = { amount -> onEvent(UserInput(amount)) },
                label = { Text(text = stringResource(id = R.string.transaction_enter_value)) },
                modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
        }

        Column (
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
                Spacer(modifier = Modifier.width(6.dp))
                Spinner(
                    title = "Nº de parcelas",
                    installments = installments,
                    selectedInstallment = selectedInstallment,
                    stoneCodes = emptyList(),
                    selectedStoneCode = selectedStoneCode,
                    onEvent = onEvent,
                    isStoneCode = false
                )
            }
        }

        Spinner(
            title = "Stone Code",
            modifier = Modifier.weight(1f),
            installments = installments,
            selectedInstallment = selectedInstallment,
            stoneCodes = emptyList(),
            selectedStoneCode = selectedStoneCode,
            onEvent = onEvent,
            isStoneCode = true
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        ) {
            CheckboxCapture(
                onEvent = onEvent,
                label = "Transação com Captura",
                checked = shouldCaptureTransaction
            )
        }

        Button(
            onClick = { onEvent(SendTransaction) },
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(vertical = 2.dp)
        ) {
            Text(text = stringResource(id = R.string.transaction_send_button))
        }

        Button(
            onClick = { onEvent(CancelTransaction) },
            modifier = Modifier.align(Alignment.CenterHorizontally)

        ) {
            Text(text = stringResource(id = R.string.cancel_transaction_button))
        }

    }

}

@Composable
fun RadioButtonGroup(
    onEvent: (TransactionEvent) -> Unit,
    transactionTypes : List<TypeOfTransactionEnum>,
    selectedTransactionType : TypeOfTransactionEnum
) {

    FlowRow {
        transactionTypes.forEach { type ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                RadioButton(
                    selected = (type == selectedTransactionType),
                    onClick = { onEvent(TypeOfTransaction(type)) }
                )
                Text(
                    text = type.displayName,
                )
            }
        }
    }
}


@Composable
fun Spinner(
    title: String,
    onEvent: (TransactionEvent) -> Unit,
    modifier: Modifier = Modifier,
    installments: List<InstallmentTransaction>,
    selectedInstallment : InstallmentTransaction,
    stoneCodes: List<String>,
    selectedStoneCode : String,
    isStoneCode: Boolean
) {
    var expanded by remember { mutableStateOf(false) }
    var selectedItem: String by remember {
        mutableStateOf(
            if (isStoneCode) selectedStoneCode
            else selectedInstallment.mapInstallmentToPresentation()
        )
    }

    Box(modifier = modifier) {
        OutlinedTextField(
            value = selectedItem,
            onValueChange = {},
            label = { Text(text = title) },
            readOnly = true,  // Impede edição manual
            trailingIcon = {
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = null,
                    Modifier.clickable { expanded = true }
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = true }
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            if (!isStoneCode) {
                installments.forEach { installment ->
                    DropdownMenuItem(
                        text = { Text(text = installment.mapInstallmentToPresentation()) },
                        onClick = {
                            selectedItem = installment.mapInstallmentToPresentation()
                            onEvent(InstallmentSelected(installment))
                            expanded = false
                        }
                    )
                }
            } else {
                stoneCodes.forEachIndexed { index, stoneCode ->
                    DropdownMenuItem(
                        text = { Text(text = stoneCode) },
                        onClick = {
                            selectedItem = stoneCode
                            onEvent(TransactionEvent.StoneCodeItemClick(index))
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun CheckboxCapture(
    checked: Boolean,
    onEvent: (TransactionEvent) -> Unit,
    label: String,
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Checkbox(
            checked = checked,
            onCheckedChange = { isChecked -> onEvent(TransactionEvent.CheckBoxChanged(isChecked)) }
        )
        Text(text = label)
    }
}


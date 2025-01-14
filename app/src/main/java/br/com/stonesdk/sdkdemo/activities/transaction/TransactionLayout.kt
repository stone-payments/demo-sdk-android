@file:OptIn(ExperimentalLayoutApi::class)

package br.com.stonesdk.sdkdemo.activities.transaction

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
import org.koin.androidx.compose.getViewModel

@Composable
internal fun TransactionScreen(
    viewModel: TransactionViewModel = getViewModel()
) {
    TransactionContent(
        model = viewModel.viewState,
        onEvent = viewModel::onEvent
    )

}

@Composable
fun TransactionContent(
    model: TransactionUiModel,
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
                value = "10",
                readOnly = false,
                onValueChange = { amount -> onEvent(UserInput(amount)) },
                label = { Text(text = stringResource(id = R.string.transaction_enter_value)) },
                modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 2.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            RadioButtonGroup(onEvent, typeSelected = model.selectedTransaction)
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Spinner(
                title = "Nº de parcelas",
                modifier = Modifier.weight(1f),
                installmentList = model.installmentList,
                onEvent = onEvent,
                stoneCodeList = emptyList(),
                isStoneCode = false
            )
            Spacer(modifier = Modifier.width(6.dp))
            Spinner(
                title = "Stone Code",
                modifier = Modifier.weight(1f),
                stoneCodeList = model.selectedStoneCode,
                installmentList = emptyList(),
                onEvent = onEvent,
                isStoneCode = true
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        ) {
            CheckboxCapture(
                onEvent = onEvent,
                label = "Transação com Captura",
                checked = model.isCheckBox
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
    typeSelected: TypeOfTransactionEnum
) {
    val transactionTypes = listOf(
        TypeOfTransactionEnum.DEBIT to "Débito",
        TypeOfTransactionEnum.CREDIT to "Crédito",
        TypeOfTransactionEnum.VOUCHER to "Voucher",
        TypeOfTransactionEnum.PIX to "Pix"
    )
    FlowRow {
        transactionTypes.forEach { (transactionType, text) ->

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                RadioButton(
                    selected = (transactionType == typeSelected),
                    onClick = { onEvent(TypeOfTransaction(transactionType)) }
                )
                Text(
                    text = text,
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
    installmentList: List<String>,
    stoneCodeList: List<String>,
    isStoneCode: Boolean
) {
    var expanded by remember { mutableStateOf(false) }
    var selectedItem by remember {
        mutableStateOf(
            if (isStoneCode) stoneCodeList.firstOrNull() ?: "" else installmentList.firstOrNull()
                ?: ""
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
                installmentList.forEachIndexed { index, installment ->
                    DropdownMenuItem(
                        text = { Text(text = installment) },
                        onClick = {
                            selectedItem = installment
                            onEvent(InstallmentSelected(index))
                            expanded = false
                        }
                    )
                }
            } else {
                stoneCodeList.forEachIndexed { index, stoneCode ->
                    DropdownMenuItem(
                        text = { Text(text = stoneCode) },
                        onClick = {
                            selectedItem = stoneCode
                            onEvent(TransactionEvent.StoneCodeItemClick(index))
                            expanded = false
                        })
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
            onCheckedChange = {isChecked -> onEvent(TransactionEvent.CheckBoxChanged(isChecked )) }
        )
        Text(text = label)
    }
}


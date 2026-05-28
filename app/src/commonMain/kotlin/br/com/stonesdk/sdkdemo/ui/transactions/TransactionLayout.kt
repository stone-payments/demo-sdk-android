@file:OptIn(ExperimentalLayoutApi::class)

package br.com.stonesdk.sdkdemo.ui.transactions

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.Checkbox
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.RadioButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import br.com.stonesdk.sdkdemo.ui.components.BaseSpinner
import br.com.stonesdk.sdkdemo.ui.components.MonospacedText
import br.com.stonesdk.sdkdemo.utils.parseCurrencyToCents
import co.stone.posmobile.sdk.payment.domain.model.InstallmentTransaction
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun TransactionScreen(
    viewModel: TransactionViewModel = koinViewModel()
) {
    val uiState = viewModel.uiState.collectAsState()
    val focusManager = LocalFocusManager.current

    val desiredAmount = remember { derivedStateOf { uiState.value.amount } }
    val errorMessages = remember { derivedStateOf { uiState.value.errorMessages } }
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

    TransactionContentFixed(
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
        errorMessages = errorMessages.value,
        onEvent = viewModel::onEvent,
        onDismissKeyboard = { focusManager.clearFocus() }
    )
}

@Composable
fun TransactionContentFixed(
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
    errorMessages: List<String>,
    onEvent: (TransactionEvent) -> Unit,
    onDismissKeyboard: () -> Unit
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
            .imePadding(),
        verticalArrangement = Arrangement.Top
    ) {
        // Campo de valor
        OutlinedTextField(
            value = desiredAmount,
            readOnly = false,
            onValueChange = { amount -> onEvent(TransactionEvent.UserInput(amount)) },
            label = { Text(text = "Digite o valor") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = { onDismissKeyboard() }
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Tipo de transação
        RadioButtonGroup(
            onEvent = onEvent,
            selectedTransactionType = selectedTypeOfTransaction,
            transactionTypes = typeOfTransactions
        )

        // Seleção de parcelas
        AnimatedVisibility(showInstallmentSelection) {
            Column {
                Spacer(modifier = Modifier.height(16.dp))
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

        // Código de afiliação
        AnimatedVisibility(showAffiliationCodeSelection) {
            Column {
                Spacer(modifier = Modifier.height(16.dp))
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
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Checkbox de captura
        CheckboxCapture(
            modifier = Modifier.fillMaxWidth(),
            onEvent = onEvent,
            label = "Transação com Captura",
            checked = shouldCaptureTransaction
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Botão de enviar
        Button(
            modifier = Modifier.fillMaxWidth(),
            enabled = transactionButtonEnabled,
            onClick = {
                onDismissKeyboard()
                onEvent(TransactionEvent.SendTransaction)
            },
        ) {
            Text(text = "Enviar Transação")
        }

        // SEÇÃO DE MENSAGENS DE ERRO - MELHORADA
        if (errorMessages.isNotEmpty()) {
            Spacer(modifier = Modifier.height(24.dp))

            // Título da seção de erros
            Text(
                text = "Erros encontrados:",
                style = MaterialTheme.typography.h6,
                color = MaterialTheme.colors.error,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // Card para destacar as mensagens de erro
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colors.error,
                        shape = RoundedCornerShape(8.dp)
                    ),
                backgroundColor = MaterialTheme.colors.error.copy(alpha = 0.1f),
                elevation = 4.dp
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    errorMessages.forEachIndexed { index, message ->
                        if (message.isNotBlank()) { // Verifica se a mensagem não está vazia
                            MonospacedText(
                                text = "• $message", // Adiciona bullet point
                                modifier = Modifier.padding(vertical = 4.dp)
                            )

                            // Adiciona divisor entre mensagens (exceto na última)
                            if (index < errorMessages.size - 1) {
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                        }
                    }
                }
            }
        }

        // Adiciona espaço extra no final para garantir que tudo seja visível
        Spacer(modifier = Modifier.height(32.dp))
    }
}

// VERSÃO ALTERNATIVA COM LAZYCOLUMN
@Composable
fun TransactionContentWithLazyColumnFixed(
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
    errorMessages: List<String>,
    onEvent: (TransactionEvent) -> Unit,
    onDismissKeyboard: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .imePadding(),
        verticalArrangement = Arrangement.Top
    ) {
        // Campo de valor
        item {
            OutlinedTextField(
                value = desiredAmount,
                readOnly = false,
                onValueChange = { amount -> onEvent(TransactionEvent.UserInput(amount)) },
                label = { Text(text = "Digite o valor") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = { onDismissKeyboard() }
                )
            )
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }

        // Tipo de transação
        item {
            RadioButtonGroup(
                onEvent = onEvent,
                selectedTransactionType = selectedTypeOfTransaction,
                transactionTypes = typeOfTransactions
            )
        }

        // Seleção de parcelas
        if (showInstallmentSelection) {
            item { Spacer(modifier = Modifier.height(16.dp)) }
            item {
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

        // Código de afiliação
        if (showAffiliationCodeSelection) {
            item { Spacer(modifier = Modifier.height(16.dp)) }
            item {
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
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }

        // Checkbox
        item {
            CheckboxCapture(
                modifier = Modifier.fillMaxWidth(),
                onEvent = onEvent,
                label = "Transação com Captura",
                checked = shouldCaptureTransaction
            )
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }

        // Botão
        item {
            Button(
                modifier = Modifier.fillMaxWidth(),
                enabled = transactionButtonEnabled,
                onClick = {
                    onDismissKeyboard()
                    onEvent(TransactionEvent.SendTransaction)
                },
            ) {
                Text(text = "Enviar Transação")
            }
        }

        // Seção de mensagens de erro melhorada
        if (errorMessages.isNotEmpty()) {
            item { Spacer(modifier = Modifier.height(24.dp)) }

            item {
                Text(
                    text = "Erros encontrados:",
                    style = MaterialTheme.typography.h6,
                    color = MaterialTheme.colors.error,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colors.error,
                            shape = RoundedCornerShape(8.dp)
                        ),
                    backgroundColor = MaterialTheme.colors.error.copy(alpha = 0.1f),
                    elevation = 4.dp
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        errorMessages.forEachIndexed { index, message ->
                            if (message.isNotBlank()) {
                                MonospacedText(
                                    text = "• $message",
                                    modifier = Modifier.padding(vertical = 4.dp)
                                )

                                if (index < errorMessages.size - 1) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                }
                            }
                        }
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(32.dp)) }
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
package br.com.stonesdk.sdkdemo.ui.transactionList

import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.com.stonesdk.sdkdemo.ui.components.TransactionListContent
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun TransactionListScreen(
    viewModel: TransactionListViewModel = koinViewModel(),
) {
    val uiModel = viewModel.uiState.collectAsStateWithLifecycle()

    val errorMessage = remember { derivedStateOf { uiModel.value.errorMessage } }
    val loading = remember { derivedStateOf { uiModel.value.loading } }
    val transactions = remember { derivedStateOf { uiModel.value.transactions } }

    TransactionListContent(
        loading = loading.value,
        errorMessage = errorMessage.value,
        transactions = transactions.value,
        onItemClick = viewModel::onItemClick,
    )
}
package br.com.stonesdk.sdkdemo.activities.transaction

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import org.koin.androidx.compose.koinViewModel

@Composable
fun TransactionListScreen(
    viewModel: TransactionListViewModel = koinViewModel()
) {

    val uiModel = viewModel.uiState.collectAsState()

    val errorMessage = remember { derivedStateOf { uiModel.value.errorMessage } }
    val loading = remember { derivedStateOf { uiModel.value.loading } }
    val transactions = remember { derivedStateOf { uiModel.value.transactions } }

    TransactionListContent(
        loading = loading.value,
        errorMessage = errorMessage.value,
        transactions = transactions.value,
        onItemClick = viewModel::onItemClick
    )
}
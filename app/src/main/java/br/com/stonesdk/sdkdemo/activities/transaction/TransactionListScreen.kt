package br.com.stonesdk.sdkdemo.activities.transaction

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import org.koin.androidx.compose.koinViewModel

@Composable
fun TransactionListScreen(
    viewModel: TransactionListViewModel = koinViewModel()
) {

    val uiModel = viewModel.uiState.collectAsState()
    val transactions = uiModel.value.transactions

    TransactionListContent(
        transactions = transactions,
        onItemClick = viewModel::onItemClick
    )
}
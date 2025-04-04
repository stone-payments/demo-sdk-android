package br.com.stonesdk.sdkdemo.activities.cancel

import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.com.stonesdk.sdkdemo.activities.transaction.list.TransactionListContent
import org.koin.androidx.compose.koinViewModel

@Composable
fun CancelScreen(
    viewModel: CancelViewModel = koinViewModel(),
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

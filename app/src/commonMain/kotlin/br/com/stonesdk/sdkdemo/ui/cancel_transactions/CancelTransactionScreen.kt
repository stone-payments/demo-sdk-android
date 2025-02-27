package br.com.stonesdk.sdkdemo.ui.cancel_transactions

import androidx.compose.material.Snackbar
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import br.com.stonesdk.sdkdemo.ui.components.LoadingContent
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun CancelTransactionsScreen(
    navController: NavController,
    viewModel: CancelTransactionViewModel = viewModel { CancelTransactionViewModel() },
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        viewModel.cancelTrasaction()
    }
    when (uiState) {
        is CancelTransactionState.Loading -> {
            with(uiState as CancelTransactionState.Loading) {
                LoadingContent(message)
            }
        }

        is CancelTransactionState.Finish -> {
            Snackbar {
                Text("Transações Revertidas com Sucesso")
            }

            coroutineScope.launch {
                delay(1000)
                navController.popBackStack()
            }
        }
        else -> {}
    }
}

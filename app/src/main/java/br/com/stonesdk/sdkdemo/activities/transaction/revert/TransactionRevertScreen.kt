package br.com.stonesdk.sdkdemo.activities.transaction.revert

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import org.koin.androidx.compose.koinViewModel

@Composable
fun TransactionRevertScreen(
    viewModel: TransactionRevertViewModel = koinViewModel()
) {

    val uiModel = viewModel.uiState.collectAsState()

    val errorMessage = remember { derivedStateOf { uiModel.value.errorMessage } }
    val loading = remember { derivedStateOf { uiModel.value.loading } }

    TransactionRevertContent(
        loading = loading.value,
        errorMessage = errorMessage.value
    )

}

// mainViewModel.revertTransactionsWithErrors()

//        val reversalProvider = ReversalProvider(this)
//        reversalProvider.dialogMessage = "Cancelando transações com erro"
//        reversalProvider.connectionCallback = object : StoneCallbackInterface {
//            override fun onSuccess() {
//                Toast.makeText(
//                    this@MainActivity,
//                    "Transações canceladas com sucesso",
//                    Toast.LENGTH_SHORT
//                ).show()
//            }
//
//            override fun onError() {
//                Toast.makeText(
//                    this@MainActivity,
//                    "Ocorreu um erro durante o cancelamento das tabelas: " + reversalProvider.listOfErrors,
//                    Toast.LENGTH_SHORT
//                ).show()
//            }
//        }
//        reversalProvider.execute()
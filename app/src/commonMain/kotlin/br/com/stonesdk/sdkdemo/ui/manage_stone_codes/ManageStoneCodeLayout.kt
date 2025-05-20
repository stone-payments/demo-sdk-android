
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import br.com.stonesdk.sdkdemo.ui.components.LoadingContent
import br.com.stonesdk.sdkdemo.ui.manage_stone_codes.ManageStoneCodeViewModel
import br.com.stonesdk.sdkdemo.ui.manage_stone_codes.MerchantData
import org.koin.compose.viewmodel.koinViewModel


@Composable
fun ManageStateScreen(
    viewModel: ManageStoneCodeViewModel = koinViewModel()
) {

    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.getMerchants()
    }

    when (uiState) {
        is ManageStoneCodeViewModel.ManageStoneCodeState.Loading -> {
            with(uiState as ManageStoneCodeViewModel.ManageStoneCodeState.Loading) {
                LoadingContent(message)
            }
        }

        is ManageStoneCodeViewModel.ManageStoneCodeState.Error -> {
            with(uiState as ManageStoneCodeViewModel.ManageStoneCodeState.Error) {
                Text("Error: $message")
            }
        }

        is ManageStoneCodeViewModel.ManageStoneCodeState.Finish -> {
            with(uiState as ManageStoneCodeViewModel.ManageStoneCodeState.Finish) {
                ManageStoneCodeContent(modifier = Modifier.fillMaxSize(), merchants, onMerchantDeactived = { affiliationCode ->
                    viewModel.deactivateMerchant(affiliationCode)
                },
                    onMerchantUpate = { affiliationCode ->
                        viewModel.updateMerchant(affiliationCode)
                    })
            }
        }

        else -> {}
    }
}


@Composable
fun ManageStoneCodeContent(
    modifier: Modifier = Modifier,
    merchants: List<MerchantData>,
    onMerchantDeactived: (affiliationCode: String) -> Unit,
    onMerchantUpate: (affiliationCode: String) -> Unit
) {
    Column(modifier = modifier) {
        LazyColumn {
            items(count = merchants.size, key = { index -> merchants[index].uuid }) { index ->
                Card {
                    Column {
                        Text("Merchant : ${merchants[index].displayName}")
                        Text("Legal Name : ${merchants[index].legalName}")
                        Text("Affiliation Code : ${merchants[index].affiliationCode}")

                        Row {
                            TextButton(onClick = { onMerchantDeactived(merchants[index].affiliationCode) }) {
                                Text("Desactivate")
                            }

                            TextButton(onClick = { onMerchantUpate(merchants[index].affiliationCode) }) {
                                Text("Update Merchant")
                            }
                        }

                    }
                }
            }
        }
    }
}




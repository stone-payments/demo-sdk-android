package br.com.stonesdk.sdkdemo.ui.transactions

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import br.com.stonesdk.sdkdemo.ui.components.DottedSpaceBetweenRowElements



@Composable
fun TransactionListScreen(
    viewModel: TransactionListViewModel = viewModel { TransactionListViewModel() }
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



@Composable
fun TransactionListContent(
    loading: Boolean = false,
    errorMessage: String? = null,
    transactions: List<Transaction>,
    onItemClick: (Transaction) -> Unit
) {

    AnimatedVisibility(
        visible = loading,
        enter = fadeIn(),
        exit = fadeOut(tween(900))
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    }

    AnimatedVisibility(
        visible = !loading,
        enter = fadeIn(tween(900)),
        exit = fadeOut()
    ) {
        errorMessage?.let {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = errorMessage,
                    modifier = Modifier.padding(16.dp),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
            }

        }
        LazyColumn {
            items(count = transactions.size, key = { transactions[it].id }) { index ->
                TransactionListItem(
                    id = transactions[index].id,
                    authorizedAmount = transactions[index].authorizedAmount,
                    authorizationDate = transactions[index].authorizationDate,
                    atk = transactions[index].atk,
                    status = transactions[index].status,
                    onItemSelected = { onItemClick(transactions[index]) }
                )
            }
        }
    }
}

@Composable
fun TransactionListItem(
    id: String,
    authorizedAmount: String,
    authorizationDate: String,
    atk: String?,
    status: String,
    onItemSelected: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier
        .clickable { onItemSelected() }
        .padding(8.dp)) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            DottedSpaceBetweenRowElements(
                startText = {
                    Text(
                        text = "ID: ${id.padStart(3, ' ')}",
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                },
                endText = {
                    Text(
                        text = authorizedAmount, fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                },
            )
        }
        Spacer(modifier = Modifier.size(8.dp))

        Text(
            "Status: $status",
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
        )

        atk?.let {
            Text(
                text = "ATK: $atk",
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
            )
        }

        Text(
            text = "Date: $authorizationDate",
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
        )

    }

}
package br.com.stonesdk.sdkdemo.activities.transaction

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun TransactionListContent(
    transactions: List<Transaction>,
    onItemClick: (Transaction) -> Unit
) {
    LazyColumn {
        items(count = transactions.size, key = { transactions[it].transactionId }) { index ->
            TransactionListItem(transactionId = transactions[index].transactionId,
                transactionAmount = transactions[index].transactionAmount,
                transactionStatus = transactions[index].transactionStatus,
                onItemSelected = { onItemClick(transactions[index]) })
        }
    }
}

@Composable
fun TransactionListItem(
    transactionId: String,
    transactionAmount: String,
    transactionStatus: String,
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
            Text(
                modifier = Modifier.wrapContentSize(Alignment.Center),
                text = "ID: $transactionId",
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
            )
            Spacer(modifier = Modifier.size(8.dp))
            Text(
                "Amount: $transactionAmount",
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
            )
        }
        Row {
            Text(
                "Status: $transactionStatus",
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
            )
        }
    }

}

@Composable
@Preview(showBackground = true, showSystemUi = true)
fun TransactionListItemPreview() {
    val transaction = Transaction(
        transactionId = "1", transactionAmount = "R$ 100,00", transactionStatus = "Aprovado"
    )

    TransactionListItem(
        transactionId = transaction.transactionId,
        transactionAmount = transaction.transactionAmount,
        transactionStatus = transaction.transactionStatus,
        onItemSelected = {}
    )
}
package br.com.stonesdk.sdkdemo

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import br.com.stonesdk.sdkdemo.ui.components.LoadingContent
import br.com.stonesdk.sdkdemo.ui.main.MainContent
import br.com.stonesdk.sdkdemo.ui.main.MainNavigationOption
import br.com.stonesdk.sdkdemo.ui.splashscreen.ActivateContent
import br.com.stonesdk.sdkdemo.ui.transactions.transactionList.Transaction
import br.com.stonesdk.sdkdemo.ui.transactions.transactionList.TransactionListItem

@Preview
@Composable
private fun Test() {
    LoadingContent("teste")
}

@Preview
@Composable
private fun Activate() {
    ActivateContent {}
}

// ---------

@Preview
@Composable
private fun MainPreview() {
    MainContent(
        generalItems = getGeneralOptions(),
        pinpadItems = getPinpadOptions(),
        posItems = getPosOptions(),
        onItemSelected = {},
    )
}

private fun getGeneralOptions(): List<MainNavigationOption> {
    return listOf(
        MainNavigationOption.GeneralListTransactions,
        MainNavigationOption.GeneralCancelErrorTransactions,
        MainNavigationOption.GeneralManageStoneCodes,
    )
}

private fun getPinpadOptions(): List<MainNavigationOption> {
    return listOf(
        MainNavigationOption.PinpadPairedDevices,
        MainNavigationOption.PinpadMakeTransaction,
        MainNavigationOption.PinpadShowMessage,
        MainNavigationOption.PinpadDisconnect,
    )
}

private fun getPosOptions(): List<MainNavigationOption> {
    val isPosDevice = true
    return if (isPosDevice) {
        listOf(
            MainNavigationOption.PosMakeTransaction,
            MainNavigationOption.PosValidateByCard,
            MainNavigationOption.PosPrinterProvider,
            MainNavigationOption.PosMifareProvider,
        )
    } else {
        emptyList()
    }
}

@Composable
@Preview(showBackground = true, showSystemUi = true)
fun TransactionListItemPreview() {
    val transaction =
        Transaction(
            id = 1,
            affiliationCode = "123",
            authorizedAmount = "R$ 100,00",
            authorizationDate = "2021-12-25T18:59:59.000Z",
            atk = "12345678901234",
            status = "Aprovado",
        )

    TransactionListItem(
        id = transaction.id.toString(),
        authorizedAmount = transaction.authorizedAmount,
        authorizationDate = transaction.authorizationDate,
        status = transaction.status,
        atk = transaction.atk,
        onItemSelected = {},
    )
}

fun foo() {
//    StoneStart.INSTANCE.init()
}

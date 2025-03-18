package br.com.stonesdk.sdkdemo

import ManageStateScreen
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import br.com.stonesdk.sdkdemo.ui.cancel_transactions.CancelTransactionsScreen
import br.com.stonesdk.sdkdemo.ui.display.DisplayMessageScreen
import br.com.stonesdk.sdkdemo.ui.main.MainScreen
import br.com.stonesdk.sdkdemo.ui.paired_devices.DeviceScreen
import br.com.stonesdk.sdkdemo.ui.splashscreen.ValidationScreen
import br.com.stonesdk.sdkdemo.ui.transactions.TransactionScreen
import br.com.stonesdk.sdkdemo.ui.transactions.transaction_list.TransactionListScreen
import br.com.stonesdk.sdkdemo.utils.AppInfo
import co.stone.posmobile.lib.commons.platform.PlatformContext
import org.jetbrains.compose.ui.tooling.preview.Preview


@Composable
@Preview
fun DemoApp(context: PlatformContext, appInfo: AppInfo) {
    MaterialTheme {
        val navController = rememberNavController()
        NavHost(navController = navController, startDestination = "splash-screen") {
            composable("splash-screen") {
                ValidationScreen(
                    context,
                    appInfo,
                    navController = navController
                )
            }
            composable("home") { MainScreen(navController) }
            composable("transactions-list") { TransactionListScreen() }
            composable("cancel-error-transactions") { CancelTransactionsScreen(navController) }
            composable("manage-stone-codes") { ManageStateScreen() }
            composable("paired-devices") { DeviceScreen() }
            composable("show-message") { DisplayMessageScreen() }
            composable("make-transaction") { TransactionScreen() }
        }
    }
}
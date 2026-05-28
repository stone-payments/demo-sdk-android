package br.com.stonesdk.sdkdemo

import ManageStateScreen
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContent
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import br.com.stonesdk.sdkdemo.routes.NavigationManager
import br.com.stonesdk.sdkdemo.routes.Route
import br.com.stonesdk.sdkdemo.theme.DemoSdkTheme
import br.com.stonesdk.sdkdemo.ui.cancel_transactions.CancelScreen
import br.com.stonesdk.sdkdemo.ui.display.DisplayMessageScreen
import br.com.stonesdk.sdkdemo.ui.main.MainScreen
import br.com.stonesdk.sdkdemo.ui.paired_devices.DeviceScreen
import br.com.stonesdk.sdkdemo.ui.splashscreen.ValidationScreen
import br.com.stonesdk.sdkdemo.ui.transactionList.TransactionListScreen
import br.com.stonesdk.sdkdemo.ui.transactions.TransactionScreen
import org.koin.compose.koinInject

@Composable
fun DemoApp() {
    DemoSdkTheme {

        val navigationManager = koinInject<NavigationManager>()
        val navController = rememberNavController()
        navigationManager.setNavController(navController)

        Scaffold(
            contentWindowInsets = WindowInsets.safeContent,
            modifier = Modifier.fillMaxSize()
        ) { innerPadding ->
            Surface(
                modifier = Modifier.padding(innerPadding)
            ) {

                NavHost(
                    navController = navController,
                    startDestination = Route.SplashScreen
                ) {
                    composable<Route.SplashScreen> { ValidationScreen() }

                    composable<Route.Home> { MainScreen() }

                    composable<Route.CommonListTransactions> { TransactionListScreen() }
                    composable<Route.CommonCancelErrorTransactions> { CancelScreen() }
                    composable<Route.CommonManageAffiliationCodes> { ManageStateScreen() }

                    composable<Route.PinpadPairedDevices> { DeviceScreen() }
                    composable<Route.PinpadMakeTransaction> { TransactionScreen() }
                    composable<Route.PinpadShowMessage> { DisplayMessageScreen() }

                    composable<Route.PosMakeTransaction> { Text("not implemented") }
                    composable<Route.PosValidateByCard> { Text("not implemented") }
                    composable<Route.PosPrinterProvider> { Text("not implemented") }
                    composable<Route.PosMifareProvider> { Text("not implemented") }

                }
            }
        }


    }
}

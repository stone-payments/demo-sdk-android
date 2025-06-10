package br.com.stonesdk.sdkdemo.routes

import kotlinx.serialization.Serializable

@Serializable
sealed class Route(
    val name: String,
) {

    @Serializable
    data object SplashScreen : Route(name = "Splash Screen")

    @Serializable
    data object Home : Route(name = "Home")

    @Serializable
    data object CommonListTransactions : Route(name = "Listar Transações")
    @Serializable
    data object CommonCancelErrorTransactions : Route(name = "Cancelar Transações com Erro")
    @Serializable
    data object CommonManageAffiliationCodes : Route(name = "Gerenciar Códigos Stone")

    @Serializable
    data object PinpadMakeTransaction : Route(name = "Realizar Transação [Pinpad]")
    @Serializable
    data object PinpadPairedDevices : Route(name = "Dispositivos Pareados")
    @Serializable
    data object PinpadShowMessage : Route(name = "Mostrar Mensagem [Pinpad]")

    @Serializable
    data object PosMakeTransaction : Route(name = "Realizar Transação [POS]")
    @Serializable
    data object PosValidateByCard : Route(name = "Validar Transações por Cartão")
    @Serializable
    data object PosPrinterProvider : Route(name = "Provedor de Impressão [POS]")
    @Serializable
    data object PosMifareProvider : Route(name = "Provedor Mifare [POS]")

    companion object Companion {

        fun getCommonRoutes(): List<Route> {
            return listOf(
                CommonListTransactions,
                CommonCancelErrorTransactions,
                CommonManageAffiliationCodes
            )
        }

        fun getPinpadRoutes(): List<Route> {
            return listOf(
                PinpadMakeTransaction,
                PinpadPairedDevices,
                PinpadShowMessage
            )
        }

        fun getPosRoutes(): List<Route> {
            return listOf(
                PosMakeTransaction,
                PosValidateByCard,
                PosPrinterProvider,
                PosMifareProvider
            )
        }
    }


}
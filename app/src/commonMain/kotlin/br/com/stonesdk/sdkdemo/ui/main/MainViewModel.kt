package br.com.stonesdk.sdkdemo.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
//import br.com.stone.sdk.android.error.StoneStatus
//import br.com.stonesdk.sdkdemo.R
//import br.com.stonesdk.sdkdemo.activities.devices.DeviceInfoProviderWrapper
//import br.com.stonesdk.sdkdemo.activities.manageStoneCode.ActivationProviderWrapper
//import co.stone.posmobile.sdk.callback.StoneResultCallback
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class MainViewModel(
//    private val activationProviderWrapper: ActivationProviderWrapper,
//    private val deviceInfoProviderWrapper : DeviceInfoProviderWrapper,
//    private val reversalProviderWrapper: ReversalProviderWrapper
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        MainUiState(
            isPosAndroid = false,
            generalNavigationOptions = getGeneralOptions(),
            pinpadNavigationOptions = getPinpadOptions(),
            posNavigationOptions = getPosOptions(),
            navigateToOption = null
        )
    )
    val uiState = _uiState.asStateFlow()

    fun navigateToOption(option: MainNavigationOption) {
        _uiState.value = _uiState.value.copy(navigateToOption = option)
    }

    fun doneNavigating() {
        _uiState.value = _uiState.value.copy(navigateToOption = null)
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
        )
    }

    private fun getPosOptions(): List<MainNavigationOption> {
        val isPosDevice = true
        return if (isPosDevice) {
            listOf(
                MainNavigationOption.PosMakeTransaction,
                MainNavigationOption.PosValidateByCard,
                MainNavigationOption.PosPrinterProvider,
                MainNavigationOption.PosMifareProvider
            )
        } else
            emptyList()
    }

    fun revertTransactionsWithErrors() {
        viewModelScope.launch {
//            reversalProviderWrapper.reverseTransactions(object : StoneResultCallback<Unit> {
//                override fun onSuccess(result: Unit) {
//                    // handle success
//                }
//
//                override fun onError(stoneStatus: StoneStatus?, throwable: Throwable) {
//                    // handle error
//                }
//            })
        }
    }

}

data class MainUiState(
    val isPosAndroid: Boolean,
    val generalNavigationOptions: List<MainNavigationOption>,
    val pinpadNavigationOptions: List<MainNavigationOption>,
    val posNavigationOptions: List<MainNavigationOption>,
    val navigateToOption: MainNavigationOption? = null
)


@OptIn(ExperimentalUuidApi::class)
sealed class MainNavigationOption (val key : Uuid = Uuid.random(), val name: String, val route : String?, val action : () -> Unit = {}) {
    data object GeneralListTransactions :
        MainNavigationOption(name= "Listar Transações", route = "transactions-list")

    data object GeneralCancelErrorTransactions :
        MainNavigationOption(name= "Cancelar Transações com Erro", route = "cancel-error-transactions")

    data object GeneralManageStoneCodes :
        MainNavigationOption(name= "Gerenciar Códigos Stone", route = "manage-stone-codes")

    data object PinpadPairedDevices :
        MainNavigationOption(name= "Dispositivos Pareados", route = "paired-devices")

    data object PinpadMakeTransaction :
        MainNavigationOption(name= "Realizar Transação [Pinpad]", route = "make-transaction")

    data object PinpadShowMessage :
        MainNavigationOption(name= "Mostrar Mensagem [Pinpad]", route = "show-message")

    data object PinpadDisconnect :
        MainNavigationOption(name= "Desconectar [Pinpad]", route = "disconnect")

    data object PosMakeTransaction :
        MainNavigationOption(name= "Realizar Transação [POS]", route = "make-transaction")

    data object PosValidateByCard : MainNavigationOption(name= "Validar Transações por Cartão", route = "validate-by-card")
    data object PosPrinterProvider :
        MainNavigationOption(name= "Provedor de Impressão [POS]", route = "printer-provider")

    data object PosMifareProvider : MainNavigationOption(name = "Provedor Mifare [POS]", route = "mifare-provider")
}

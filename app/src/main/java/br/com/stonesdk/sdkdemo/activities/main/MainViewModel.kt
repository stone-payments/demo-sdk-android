package br.com.stonesdk.sdkdemo.activities.main

import androidx.lifecycle.ViewModel
import br.com.stonesdk.sdkdemo.R
import br.com.stonesdk.sdkdemo.activities.devices.DeviceInfoProviderWrapper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class MainViewModel(
    private val deviceInfoProviderWrapper: DeviceInfoProviderWrapper,
) : ViewModel() {
    private val _uiState =
        MutableStateFlow(
            MainUiState(
                isPosAndroid = false,
                generalNavigationOptions = getGeneralOptions(),
                pinpadNavigationOptions = getPinpadOptions(),
                posNavigationOptions = getPosOptions(),
                navigateToOption = null,
            ),
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
            MainNavigationOption.GeneralCancelTransactions,
            MainNavigationOption.GeneralManageStoneCodes,
            MainNavigationOption.GeneralDeactivate,
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
        val isPosDevice = deviceInfoProviderWrapper.isPosDevice()
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
}

data class MainUiState(
    val isPosAndroid: Boolean,
    val generalNavigationOptions: List<MainNavigationOption>,
    val pinpadNavigationOptions: List<MainNavigationOption>,
    val posNavigationOptions: List<MainNavigationOption>,
    val navigateToOption: MainNavigationOption? = null,
)

sealed class MainNavigationOption(val nameResource: Int) {
    data object GeneralListTransactions :
        MainNavigationOption(R.string.main_options_generic_list_transactions)

    data object GeneralCancelErrorTransactions :
        MainNavigationOption(R.string.main_options_generic_cancel_transactions_with_error)

    data object GeneralCancelTransactions :
        MainNavigationOption(R.string.main_options_generic_cancel_transactions)

    data object GeneralManageStoneCodes :
        MainNavigationOption(R.string.main_options_generic_manage_stone_codes)

    data object GeneralDeactivate : MainNavigationOption(R.string.main_options_generic_deactivate)

    data object PinpadPairedDevices :
        MainNavigationOption(R.string.main_options_pinpad_paired_devices)

    data object PinpadMakeTransaction :
        MainNavigationOption(R.string.main_options_pinpad_make_transaction)

    data object PinpadShowMessage :
        MainNavigationOption(R.string.main_options_pinpad_show_display_message)

    data object PinpadDisconnect :
        MainNavigationOption(R.string.main_options_pinpad_disconnect_device)

    data object PosMakeTransaction :
        MainNavigationOption(R.string.main_options_pos_make_transaction)

    data object PosValidateByCard : MainNavigationOption(R.string.main_options_pos_card_validation)

    data object PosPrinterProvider :
        MainNavigationOption(R.string.main_options_pos_printer_provider)

    data object PosMifareProvider : MainNavigationOption(R.string.main_options_pos_mifare_provider)
}

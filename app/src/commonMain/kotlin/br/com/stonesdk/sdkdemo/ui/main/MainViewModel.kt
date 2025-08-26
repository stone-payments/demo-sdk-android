package br.com.stonesdk.sdkdemo.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.stonesdk.sdkdemo.routes.NavigationManager
import br.com.stonesdk.sdkdemo.routes.Route
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel(
    val navigationManager: NavigationManager,
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        MainUiState(
            isPosAndroid = false,
            commonNavigationOptions = Route.getCommonRoutes(),
            pinpadNavigationOptions = Route.getPinpadRoutes(),
            posNavigationOptions = getPosOptions(),
        )
    )
    val uiState = _uiState.asStateFlow()

    fun navigateToOption(option: Route) {
        navigationManager.navigate(option)
    }


    private fun getPosOptions(): List<Route> {
        val isPosDevice = false
        return if (isPosDevice) {
            Route.getPosRoutes()
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
    val commonNavigationOptions: List<Route>,
    val pinpadNavigationOptions: List<Route>,
    val posNavigationOptions: List<Route>,
)




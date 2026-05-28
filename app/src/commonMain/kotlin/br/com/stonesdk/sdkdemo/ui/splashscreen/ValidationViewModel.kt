package br.com.stonesdk.sdkdemo.ui.splashscreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.stonesdk.sdkdemo.routes.NavigationManager
import br.com.stonesdk.sdkdemo.routes.Route
import br.com.stonesdk.sdkdemo.wrappers.ActivationProviderWrapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ValidationViewModel(
    private val activationProvider: ActivationProviderWrapper,
    private val navigationManager: NavigationManager,
) : ViewModel() {

    private val _uiState: MutableStateFlow<ValidationUiModel> =
        MutableStateFlow(ValidationUiModel(SplashScreenState.Idle))
    val uiState = _uiState.asStateFlow()

    fun activate(stoneCode: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.update { it.copy(state = SplashScreenState.Loading) }
            when (val activationResult = activationProvider.activate(stoneCode)) {
                ActivationProviderWrapper.ActivationStatus.Activated -> {
                    _uiState.update { it.copy(state = SplashScreenState.Activated) }
                }

                is ActivationProviderWrapper.ActivationStatus.Error -> {
                    _uiState.update { it.copy(state = SplashScreenState.Error(stoneCode, activationResult.errorMessage)) }
                }
            }
        }
    }

    fun checkNeedToActivate() {
        viewModelScope.launch {
            if (activationProvider.getActivatedAffiliationCodes().isEmpty()) {
                _uiState.update { it.copy(state = SplashScreenState.NotActivated) }
            } else {
                _uiState.update { it.copy(state = SplashScreenState.Activated) }
            }
        }
    }

    fun navigateToHomeScreen() {
        navigationManager.navigateClearingStack(Route.Home)
    }
}

data class ValidationUiModel(
    val state: SplashScreenState,
)

sealed interface SplashScreenState {
    data object Idle : SplashScreenState

    data object Loading : SplashScreenState

    data object Activated : SplashScreenState

    data object NotActivated : SplashScreenState

    data class Error(val code: String, val message: String) : SplashScreenState
}

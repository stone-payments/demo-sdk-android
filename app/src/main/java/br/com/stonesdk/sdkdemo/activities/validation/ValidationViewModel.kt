package br.com.stonesdk.sdkdemo.activities.validation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.stonesdk.sdkdemo.activities.manageStoneCode.ActivationProviderWrapper
import br.com.stonesdk.sdkdemo.activities.validation.ValidationStoneCodeEffects.NavigateToMain
import br.com.stonesdk.sdkdemo.activities.validation.ValidationStoneCodeEvent.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import stone.environment.Environment

class ValidationViewModel(
    private val providerWrapper: ActivationProviderWrapper,
    private val appInitializer: AppInitializer
) : ViewModel() {

    var viewState by mutableStateOf(ValidationStoneCodeUiModel())
    private val _sideEffects = MutableStateFlow<ValidationStoneCodeEffects?>(null)
    val sideEffects: StateFlow<ValidationStoneCodeEffects?> = _sideEffects

    init {
        checkApp()
    }

    fun onEvent(event: ValidationStoneCodeEvent) {
        when (event) {
            is UserInput -> viewState = viewState.copy(
                stoneCodeToBeValidated = event.stoneCode
            )

            is EnvironmentSelected -> viewState = viewState.copy(
                selectedEnvironment = event.environment
            )

            is Activate -> activateStoneCode()
            is Permission -> checkApp()
        }
    }


    private fun activateStoneCode() {
        viewModelScope.launch {
            viewState = viewState.copy(activationInProgress = true)
            val isSuccess = providerWrapper.activate(viewState.stoneCodeToBeValidated)
            if (isSuccess) {
                _sideEffects.emit(NavigateToMain)
            }
        }
    }

    private fun checkApp() {
        viewModelScope.launch {
            val success = appInitializer.initiateApp()
            if (success) {
                _sideEffects.emit(NavigateToMain)
            } else {
                viewState = viewState.copy(loading = false)
            }
        }
    }
}

data class ValidationStoneCodeUiModel(
    val stoneCodeToBeValidated: String = "",
    val selectedEnvironment: Environment = Environment.PRODUCTION,
    val activationInProgress: Boolean = false,
    val loading: Boolean = true
)

sealed interface ValidationStoneCodeEvent {
    data class UserInput(val stoneCode: String) : ValidationStoneCodeEvent
    data class EnvironmentSelected(val environment: Environment) : ValidationStoneCodeEvent
    data object Activate : ValidationStoneCodeEvent
    data object Permission: ValidationStoneCodeEvent
}

sealed interface ValidationStoneCodeEffects {
    data object NavigateToMain : ValidationStoneCodeEffects
}
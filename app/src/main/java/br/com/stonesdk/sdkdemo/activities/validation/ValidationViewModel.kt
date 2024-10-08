package br.com.stonesdk.sdkdemo.activities.validation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.stonesdk.sdkdemo.activities.manageStoneCode.ActivationProviderWrapper
import br.com.stonesdk.sdkdemo.activities.validation.ValidationStoneCodeEffects.NavigateToMain
import br.com.stonesdk.sdkdemo.activities.validation.ValidationStoneCodeEvent.Activate
import br.com.stonesdk.sdkdemo.activities.validation.ValidationStoneCodeEvent.EnvironmentReturned
import br.com.stonesdk.sdkdemo.activities.validation.ValidationStoneCodeEvent.Permission
import br.com.stonesdk.sdkdemo.activities.validation.ValidationStoneCodeEvent.UserInput
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import stone.environment.Environment
import stone.utils.Stone

class ValidationViewModel(
    private val providerWrapper: ActivationProviderWrapper,
    private val appInitializer: AppInitializer,
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

            is EnvironmentReturned -> viewState = viewState.copy(
                getEnvironment = event.environment
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
    val getEnvironment: Environment = Stone.getEnvironment(),
    val activationInProgress: Boolean = false,
    val loading: Boolean = true
)

sealed interface ValidationStoneCodeEvent {
    data class UserInput(val stoneCode: String) : ValidationStoneCodeEvent
    data class EnvironmentReturned(val environment: Environment) : ValidationStoneCodeEvent
    data object Activate : ValidationStoneCodeEvent
    data object Permission : ValidationStoneCodeEvent
}

sealed interface ValidationStoneCodeEffects {
    data object NavigateToMain : ValidationStoneCodeEffects
}
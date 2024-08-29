package br.com.stonesdk.sdkdemo.activities.validation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.stonesdk.sdkdemo.activities.manageStoneCode.ActivationProviderWrapper
import br.com.stonesdk.sdkdemo.activities.validation.ValidationViewModel.ValidationStoneCodeEvent.Activated
import br.com.stonesdk.sdkdemo.activities.validation.ValidationViewModel.ValidationStoneCodeEvent.EnvironmentSelected
import br.com.stonesdk.sdkdemo.activities.validation.ValidationViewModel.ValidationStoneCodeEvent.UserInput
import kotlinx.coroutines.launch
import stone.environment.Environment

class ValidationViewModel(
    private val providerWrapper: ActivationProviderWrapper,

    ) : ViewModel() {

    var viewState by mutableStateOf(ValidationStoneCodeUiModel())

    init {

    }

    fun onEvent(event: ValidationStoneCodeEvent) {
        when (event) {

            is UserInput -> viewState = viewState.copy(stoneCodeToBeValidated = event.stoneCode)
            is EnvironmentSelected -> viewState =
                viewState.copy(selectedEnvironment = event.environment)

            is Activated -> TODO()
        }
    }


    private fun activateStoneCode() {
        viewModelScope.launch {
            viewState = viewState.copy(activationInProgress = true)
            val isSuccess = providerWrapper.activate(viewState.stoneCodeToBeValidated)
            if (isSuccess){

            }

        }
    }

    data class ValidationStoneCodeUiModel(
        val stoneCodeToBeValidated: String = "",
        val selectedEnvironment: Environment = Environment.PRODUCTION,
        val activationInProgress: Boolean = false



    )


    sealed interface ValidationStoneCodeEvent {
        data class UserInput(val stoneCode: String) : ValidationStoneCodeEvent
        data class EnvironmentSelected(val environment: Environment) : ValidationStoneCodeEvent
        data object Activated : ValidationStoneCodeEvent
    }
}
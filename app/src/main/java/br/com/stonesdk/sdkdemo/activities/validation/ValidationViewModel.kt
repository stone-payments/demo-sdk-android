package br.com.stonesdk.sdkdemo.activities.validation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.stonesdk.sdkdemo.activities.manageStoneCode.ActivationProviderWrapper
import br.com.stonesdk.sdkdemo.activities.validation.ValidationStoneCodeEvent.Activate
import br.com.stonesdk.sdkdemo.activities.validation.ValidationStoneCodeEvent.Permission
import br.com.stonesdk.sdkdemo.activities.validation.ValidationStoneCodeEvent.UserInput
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ValidationViewModel(
    private val activationProviderWrapper: ActivationProviderWrapper,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ValidationStoneCodeUiModel())
    val uiState: StateFlow<ValidationStoneCodeUiModel> = _uiState.asStateFlow()

    init {
        checkApp()
    }

    fun onEvent(event: ValidationStoneCodeEvent) {
        when (event) {
            is UserInput -> {
                _uiState.update {
                    it.copy(
                        stoneCodeToBeValidated = event.stoneCode
                    )
                }
            }

            is Activate -> activateStoneCode()
            is Permission -> checkApp()
        }
    }

    private fun activateStoneCode() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    activationInProgress = true
                )
            }
            val isSuccess =
                activationProviderWrapper.activate(_uiState.value.stoneCodeToBeValidated)
            if (isSuccess) {
                _uiState.update {
                    it.copy(
                        activationInProgress = false,
                        navigateToMain = true
                    )
                }
            } else {
                // handle activation error
                _uiState.update {
                    it.copy(
                        activationInProgress = false
                    )
                }
            }
        }
    }

    private fun checkApp() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    loading = true
                )
            }
            val list = activationProviderWrapper.getActivatedStoneCodes()
            if (list.isNotEmpty()) {
                _uiState.update {
                    it.copy(
                        loading = false,
                        navigateToMain = true
                    )
                }
            } else {
                _uiState.update {
                    it.copy(
                        loading = false,
                        navigateToActivation = true
                    )
                }
            }
        }
    }

    fun doneNavigateMain() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    navigateToMain = false
                )
            }
        }
    }

    fun doneNavigateActivation() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    navigateToActivation = false
                )
            }
        }
    }
}

data class ValidationStoneCodeUiModel(
    val stoneCodeToBeValidated: String = "",
    val activationInProgress: Boolean = false,
    val loading: Boolean = true,
    val navigateToMain: Boolean = false,
    val navigateToActivation: Boolean = false
)

sealed interface ValidationStoneCodeEvent {
    data class UserInput(val stoneCode: String) : ValidationStoneCodeEvent
    data object Activate : ValidationStoneCodeEvent
    data object Permission : ValidationStoneCodeEvent
}

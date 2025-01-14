package br.com.stonesdk.sdkdemo.activities.manageStoneCode

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.stonesdk.sdkdemo.activities.manageStoneCode.ManageStoneCodeEvent.*
import kotlinx.coroutines.launch

class ManageStoneCodeViewModel(
    private val activationProviderWrapper: ActivationProviderWrapper,
) : ViewModel() {

    var viewState by mutableStateOf(ManageStoneCodeUiModel())
        private set

    init {
        listStoneCodesActivated()
    }

    fun onEvent(event: ManageStoneCodeEvent) {
        when (event) {
            is ActivateStoneCode -> activateStoneCode()
            is UserInput -> viewState = viewState.copy(stoneCodeToBeActivated = event.stoneCode)
            AddStoneCode -> viewState = viewState.copy(showBottomSheet = true)
            OnDismiss -> viewState = viewState.copy(showBottomSheet = false)
            is StoneCodeItemClick -> deactivateStoneCode(position = event.position)
        }
    }

    private fun activateStoneCode() {
        viewModelScope.launch {
            viewState = viewState.copy(activationInProgress = true)
            val isSuccess = activationProviderWrapper.activate(viewState.stoneCodeToBeActivated)

            if (isSuccess) {
                listStoneCodesActivated()
            }

            viewState = viewState.copy(
                error = !isSuccess,
                activationInProgress = false,
                showBottomSheet = !isSuccess,
                stoneCodeToBeActivated = if (isSuccess) "" else viewState.stoneCodeToBeActivated
            )
        }
    }

    private fun deactivateStoneCode(position: Int) {
        viewModelScope.launch {
            val isSuccess = activationProviderWrapper.deactivate(viewState.stoneCodesActivated[position])

            if (isSuccess) {
                listStoneCodesActivated()
            }
        }
    }

    private fun listStoneCodesActivated() {
        viewModelScope.launch {
            val activatedStoneCodes = activationProviderWrapper.getActivatedStoneCodes()
            viewState = viewState.copy(stoneCodesActivated = activatedStoneCodes)
        }
    }
}

data class ManageStoneCodeUiModel(
    val error: Boolean = false,
    val stoneCodeToBeActivated: String = "",
    val stoneCodesActivated: List<String> = emptyList(),
    val showBottomSheet: Boolean = false,
    val activationInProgress: Boolean = false
)

sealed interface ManageStoneCodeEvent {
    data class UserInput(val stoneCode: String) : ManageStoneCodeEvent
    data object AddStoneCode : ManageStoneCodeEvent
    data object OnDismiss : ManageStoneCodeEvent
    data object ActivateStoneCode : ManageStoneCodeEvent
    data class StoneCodeItemClick(val position: Int) : ManageStoneCodeEvent
}
package br.com.stonesdk.sdkdemo.activities.manageStoneCode

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.stonesdk.sdkdemo.activities.manageStoneCode.ManageAffiliationCodeEvent.*
import kotlinx.coroutines.launch

class ManageStoneCodeViewModel(
    private val activationProviderWrapper: ActivationProviderWrapper,
) : ViewModel() {

    var viewState by mutableStateOf(ManageStoneCodeUiModel())
        private set

    init {
        listActivatedAffiliationCodes()
    }

    fun onEvent(event: ManageAffiliationCodeEvent) {
        when (event) {
            is ActivateAffiliationCode -> activateStoneCode()
            is UserInput -> viewState = viewState.copy(stoneCodeToBeActivated = event.affiliationCode)
            AddAffiliationCode -> viewState = viewState.copy(showBottomSheet = true)
            OnDismiss -> viewState = viewState.copy(showBottomSheet = false)
            is AffiliationCodeItemClick -> deactivateAffiliationCode(position = event.position)
        }
    }

    private fun activateStoneCode() {
        viewModelScope.launch {
            viewState = viewState.copy(activationInProgress = true)
            val isSuccess = activationProviderWrapper.activate(viewState.stoneCodeToBeActivated)

            if (isSuccess) {
                listActivatedAffiliationCodes()
            }

            viewState = viewState.copy(
                error = !isSuccess,
                activationInProgress = false,
                showBottomSheet = !isSuccess,
                stoneCodeToBeActivated = if (isSuccess) "" else viewState.stoneCodeToBeActivated
            )
        }
    }

    private fun deactivateAffiliationCode(position: Int) {
        viewModelScope.launch {
            val isSuccess = activationProviderWrapper.deactivate(viewState.stoneCodesActivated[position])

            if (isSuccess) {
                listActivatedAffiliationCodes()
            }
        }
    }

    private fun listActivatedAffiliationCodes() {
        viewModelScope.launch {
            val activatedStoneCodes = activationProviderWrapper.getActivatedAffiliationCodes()
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

sealed interface ManageAffiliationCodeEvent {
    data class UserInput(val affiliationCode: String) : ManageAffiliationCodeEvent
    data object AddAffiliationCode : ManageAffiliationCodeEvent
    data object OnDismiss : ManageAffiliationCodeEvent
    data object ActivateAffiliationCode : ManageAffiliationCodeEvent
    data class AffiliationCodeItemClick(val position: Int) : ManageAffiliationCodeEvent
}
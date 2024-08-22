package br.com.stonesdk.sdkdemo.activities.manageStoneCode

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import br.com.stonesdk.sdkdemo.activities.manageStoneCode.ManageStoneCodeEvent.ActivateStoneCode
import br.com.stonesdk.sdkdemo.activities.manageStoneCode.ManageStoneCodeEvent.UserInput
import stone.application.SessionApplication
import stone.application.interfaces.StoneCallbackInterface
import stone.providers.ActiveApplicationProvider

class ManageStoneCodeViewModel(
    private val application: SessionApplication,
    private val provider: ActiveApplicationProvider,
) : ViewModel() {


    var viewState by mutableStateOf(
        ManageStoneCodeUiModel()
    )
        private set

    init {
        listStoneCodesActivated()
    }

    fun onEvent(event: ManageStoneCodeEvent) {
        when (event) {
            ActivateStoneCode -> activateStoneCode()
            is UserInput -> viewState = viewState.copy(stoneCodeToBeActivated = event.stoneCode)
        }
    }

    private fun activateStoneCode() {
        provider.connectionCallback = object : StoneCallbackInterface {
            override fun onSuccess() {
                listStoneCodesActivated()
                viewState = viewState.copy(error = false)
            }

            override fun onError() {
                viewState = viewState.copy(error = true)
            }
        }
    }


    private fun listStoneCodesActivated() {
        val stoneCodes = application.userModelList
            .map { userModel -> userModel.stoneCode }

        viewState = viewState.copy(
            stoneCodesActivated = stoneCodes
        )
    }
}

data class ManageStoneCodeUiModel(
    val error: Boolean = false,
    val stoneCodeToBeActivated: String = "",
    val stoneCodesActivated: List<String> = emptyList(),
)

sealed interface ManageStoneCodeEvent {
    data class UserInput(val stoneCode: String) : ManageStoneCodeEvent
    data object ActivateStoneCode : ManageStoneCodeEvent
}
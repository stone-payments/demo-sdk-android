package br.com.stonesdk.sdkdemo.ui.display

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.stone.sdk.android.error.StoneStatus
import co.stone.posmobile.sdk.callback.StoneResultCallback
import co.stone.posmobile.sdk.display.provider.DisplayProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class DisplayMessageViewModel : ViewModel() {

    private val _uiState: MutableStateFlow<DisplayMessageUiModel> =
        MutableStateFlow(DisplayMessageUiModel())
    val uiState: StateFlow<DisplayMessageUiModel> = _uiState.asStateFlow()

    fun displayMessage(message: String) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    message = message,
                )
            }
            val provider = DisplayProvider.create()
            provider.show(message, object : StoneResultCallback<Unit> {
                override fun onError(stoneStatus: StoneStatus?, throwable: Throwable) {
                    val errorMessages = _uiState.value.errorMessages.toMutableList()
                    val newErrorMessage =
                        "${getCurrentFormattedTimestamp()}: $message - ${stoneStatus?.message ?: throwable.message}"
                    errorMessages.add(0, newErrorMessage)

                    _uiState.value = DisplayMessageUiModel(
                        errorMessages = errorMessages
                    )
                }

                override fun onSuccess(result: Unit) {}
            })
        }
    }

}

private fun getCurrentFormattedTimestamp(): String {
    val now = Clock.System.now()
    val timeZone = TimeZone.currentSystemDefault()
    val localDateTime = now.toLocalDateTime(timeZone)
    return localDateTime.toString()
}

data class DisplayMessageUiModel(
    val message: String = "",
    val errorMessages: List<String> = emptyList()
)
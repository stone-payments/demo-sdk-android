package br.com.stonesdk.sdkdemo.ui.display

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.stonesdk.sdkdemo.wrappers.DisplayMessageStatus
import br.com.stonesdk.sdkdemo.wrappers.DisplayProviderWrapper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class DisplayMessageViewModel(
    private val displayProvider: DisplayProviderWrapper
) : ViewModel() {

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
            displayProvider.displayMessage(message).let { status ->
                when (status) {
                    is DisplayMessageStatus.Success -> {
                        // Do Nothing
                    }

                    is DisplayMessageStatus.Error -> {
                        val errorMessages = _uiState.value.errorMessages.toMutableList()
                        val newErrorMessage =
                            "${getCurrentFormattedTimestamp()}: $message - ${status.errorMessage}"
                        errorMessages.add(0, newErrorMessage)

                        _uiState.value = DisplayMessageUiModel(
                            errorMessages = errorMessages
                        )
                    }
                }
            }
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
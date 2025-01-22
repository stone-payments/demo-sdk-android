package br.com.stonesdk.sdkdemo.activities.display

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.stonesdk.sdkdemo.utils.getCurrentDateTime
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DisplayMessageViewModel(
    private val displayMessageProviderWrapper: DisplayMessageProviderWrapper
) : ViewModel() {

    private val _uiState: MutableStateFlow<DisplayMessageUiModel> =
        MutableStateFlow(DisplayMessageUiModel())
    val uiState: StateFlow<DisplayMessageUiModel> = _uiState.asStateFlow()

    fun displayMessage(message: String) {
        viewModelScope.launch {
            try {
                _uiState.update {
                    it.copy(
                        message = message,
                    )
                }
                displayMessageProviderWrapper.displayMessage(message)
            } catch (e: Exception) {
                val errorMessages = _uiState.value.errorMessages.toMutableList()
                val newErrorMessage = "${getCurrentDateTime()}: $message - ${e.message ?: "Erro desconhecido"}"
                errorMessages.add(0, newErrorMessage)

                _uiState.value = DisplayMessageUiModel(
                    errorMessages = errorMessages
                )
            }
        }
    }

}

data class DisplayMessageUiModel(
    val message: String = "",
    val errorMessages: List<String> = emptyList()
)
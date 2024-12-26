package br.com.stonesdk.sdkdemo.activities.main

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class MainViewModel(
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        MainUiState(
            isPosAndroid = false
        )
    )
    val uiState = _uiState.asStateFlow()



}

data class MainUiState(
    val isPosAndroid: Boolean
)
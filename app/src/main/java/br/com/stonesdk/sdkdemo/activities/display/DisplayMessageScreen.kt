package br.com.stonesdk.sdkdemo.activities.display

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import org.koin.androidx.compose.koinViewModel

@Composable
fun DisplayMessageScreen(
    viewModel: DisplayMessageViewModel = koinViewModel()
) {

    val uiModel = viewModel.uiState.collectAsState()

    val errorMessages = remember { derivedStateOf { uiModel.value.errorMessages } }

    DisplayMessageContent(
        onDisplayMessage = { message -> viewModel.displayMessage(message) },
        errorMessages = errorMessages.value
    )
}
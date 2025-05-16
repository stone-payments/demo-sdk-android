package br.com.stonesdk.sdkdemo.ui.display

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Button
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import br.com.stonesdk.sdkdemo.ui.components.MonospacedText

private const val DISPLAY_MESSAGE_BUTTON_TEXT = "Mostrar Mensagem"

@Composable
fun DisplayMessageScreen(
    viewModel: DisplayMessageViewModel = viewModel { DisplayMessageViewModel() }
) {

    val uiModel = viewModel.uiState.collectAsState()

    val errorMessages = remember { derivedStateOf { uiModel.value.errorMessages } }

    DisplayMessageContent(
        onDisplayMessage = { message -> viewModel.displayMessage(message) },
        errorMessages = errorMessages.value
    )
}

@Composable
fun DisplayMessageContent(
    onDisplayMessage: (String) -> Unit,
    modifier: Modifier = Modifier,
    errorMessages: List<String> = emptyList()
) {

    var message by remember { mutableStateOf("Hello, World!") }

    Column(
        modifier = modifier
            .fillMaxSize()
    ) {
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            value = message,
            onValueChange = { newMessage ->
                message = newMessage
            }
        )

        Button(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            onClick = { onDisplayMessage(message) },
        ) {
            Text(DISPLAY_MESSAGE_BUTTON_TEXT)
        }

        LazyColumn {
            items(
                count = errorMessages.size,
                key = { index -> index }
            ) { index ->
                MonospacedText(
                    text = errorMessages
                        .getOrNull(index)
                        .orEmpty(),
                    modifier = Modifier.padding(8.dp)
                )
            }
        }
    }
}


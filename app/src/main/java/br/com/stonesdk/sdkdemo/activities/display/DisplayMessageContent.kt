package br.com.stonesdk.sdkdemo.activities.display

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import br.com.stonesdk.sdkdemo.R
import br.com.stonesdk.sdkdemo.ui.components.MonospacedText

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
            Text(text = stringResource(id = R.string.display_message_button))
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
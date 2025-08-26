package br.com.stonesdk.sdkdemo.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import br.com.stonesdk.sdkdemo.theme.DemoSdkTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun LoadingContent(message: String? = null) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator()
        message?.let {
            Text(
                modifier = Modifier.padding(20.dp),
                text = message
            )
        }
    }
}

@Composable
@Preview
fun LoadingContentPreview() {
    DemoSdkTheme {
        LoadingContent("Loading, please wait...")
    }
}
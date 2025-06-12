package br.com.stonesdk.sdkdemo.previews

import android.content.res.Configuration
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import br.com.stonesdk.sdkdemo.routes.Route
import br.com.stonesdk.sdkdemo.theme.DemoSdkTheme
import br.com.stonesdk.sdkdemo.ui.components.LoadingContent
import br.com.stonesdk.sdkdemo.ui.main.MainContent
import br.com.stonesdk.sdkdemo.ui.splashscreen.ActivateContent

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun LoadingContentPreview() {
    DemoSdkTheme {
        LoadingContent("Loading...")
    }
}

@Preview(
    showBackground = true,
    showSystemUi = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun ActivateContentPreview() {
    DemoSdkTheme {
        ActivateContent {}
    }
}

@Preview(
    showBackground = true,
    showSystemUi = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun ActivateContentPreview2() {
    DemoSdkTheme {
        Text("asdf")
        TextField(
            value = "input",
            onValueChange = { value -> },
            modifier =
                Modifier
                    .width(170.dp),
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun MainPreview() {
    DemoSdkTheme {
        MainContent(
            generalItems = Route.getCommonRoutes(),
            pinpadItems = Route.getPinpadRoutes(),
            posItems = Route.getPosRoutes(),
            onItemSelected = {},
        )
    }
}

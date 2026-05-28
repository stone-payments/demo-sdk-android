package br.com.stonesdk.sdkdemo.ui.components

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.sp
import br.com.stonesdk.sdkdemo.theme.DemoSdkTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun MonospacedText(
    text: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = text,
        fontFamily = FontFamily.Monospace,
        fontSize = 12.sp,
        modifier = modifier
    )
}

@Composable
@Preview
fun MonospacedTextPreview() {
    DemoSdkTheme {
        MonospacedText("Example of monospaced text in a preview.")
    }
}
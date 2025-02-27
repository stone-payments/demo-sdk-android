package br.com.stonesdk.sdkdemo.activities.main

import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun MainContent(
    generalItems: List<MainNavigationOption>,
    pinpadItems: List<MainNavigationOption>,
    posItems: List<MainNavigationOption>,
    onItemSelected: (MainNavigationOption) -> Unit
) {

    val ctx = LocalContext.current

    LazyColumn {
        renderSectionIfNotEmpty(
            title = "Geral",
            elements = generalItems,
            onItemSelected = onItemSelected,
            context = ctx
        )
        renderSectionIfNotEmpty(
            title = "Pinpad",
            elements = pinpadItems,
            onItemSelected = onItemSelected,
            context = ctx
        )
        renderSectionIfNotEmpty(
            title = "POS",
            elements = posItems,
            onItemSelected = onItemSelected,
            context = ctx
        )
    }
}

fun LazyListScope.renderSectionIfNotEmpty(
    title: String,
    elements: List<MainNavigationOption>,
    onItemSelected: (MainNavigationOption) -> Unit,
    context: Context
) {

    if (elements.isEmpty()) return

    item {
        StickHeader(title)
    }

    items(elements) {
        SelectableItem(
            text = context.getString(it.nameResource),
            onItemSelected = { onItemSelected(it) })
    }

}

@Composable
fun StickHeader(header: String) {
    Text(
        text = header,
        style = TextStyle(
            fontWeight = FontWeight.Bold, fontSize = 18.sp
        ),
        modifier = Modifier.padding(8.dp),
    )
}

@Composable
fun SelectableItem(
    text: String,
    onItemSelected: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Text(text = text,
        modifier = modifier
            .fillMaxWidth()
            .clickable { onItemSelected() }
            .padding(8.dp),
        textAlign = TextAlign.Start)
}


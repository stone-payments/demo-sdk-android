package br.com.stonesdk.sdkdemo.activities.main

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun MainContent(
    generalItems: List<String>,
    pinpadItems: List<String>,
    posItems: List<String>,
    onItemSelected: () -> Unit
) {
    LazyColumn {
        renderSectionIfNotEmpty("Geral", generalItems, onItemSelected)
        renderSectionIfNotEmpty("Pinpad", pinpadItems, onItemSelected)
        renderSectionIfNotEmpty("POS", posItems, onItemSelected)
    }
}

fun LazyListScope.renderSectionIfNotEmpty(
    title: String, elements: List<String>, onItemSelected: () -> Unit
) {

    if (elements.isEmpty()) return

    item {
        StickHeader(title)
    }

    items(elements) {
        SelectableItem(text = it, onItemSelected = onItemSelected)
    }

}

@Composable
fun StickHeader(header: String) {
    Text(
        text = header, style = TextStyle(
            fontWeight = FontWeight.Bold, fontSize = 18.sp
        ), modifier = Modifier.padding(bottom = 4.dp)
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
            .clickableRipple { onItemSelected() }
            .padding(8.dp),
        textAlign = TextAlign.Start)
}

@SuppressLint("ModifierFactoryUnreferencedReceiver")
fun Modifier.clickableRipple(
    bounded: Boolean = true, onClick: () -> Unit
): Modifier = composed {
    clickable(
        interactionSource = remember { MutableInteractionSource() },
        indication = rememberRipple(bounded = bounded),
        onClick = onClick,
        role = Role.Button
    )
}
package br.com.stonesdk.sdkdemo.ui.main


import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import org.koin.compose.viewmodel.koinViewModel
import kotlin.uuid.ExperimentalUuidApi


@Composable
fun MainScreen(
    navController: NavController,
    viewModel : MainViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    MainContent(
        generalItems = uiState.generalNavigationOptions,
        pinpadItems = uiState.pinpadNavigationOptions,
        posItems = uiState.posNavigationOptions,
        onItemSelected = {select ->
            select.route?.let { navController.navigate(it) }
        }
    )
}



@Composable
internal fun MainContent(
    generalItems: List<MainNavigationOption>,
    pinpadItems: List<MainNavigationOption>,
    posItems: List<MainNavigationOption>,
    onItemSelected: (MainNavigationOption) -> Unit
) {


    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        renderSectionIfNotEmpty(
            title = "Geral",
            elements = generalItems,
            onItemSelected = onItemSelected,
        )
        renderSectionIfNotEmpty(
            title = "Pinpad",
            elements = pinpadItems,
            onItemSelected = onItemSelected,
        )
        renderSectionIfNotEmpty(
            title = "POS",
            elements = posItems,
            onItemSelected = onItemSelected,
        )
    }

}

@OptIn(ExperimentalUuidApi::class)
fun LazyListScope.renderSectionIfNotEmpty(
    title: String,
    elements: List<MainNavigationOption>,
    onItemSelected: (MainNavigationOption) -> Unit,
) {

    if (elements.isEmpty()) return

    item {
        StickHeader(title)
    }

    items(
        count = elements.size,
        key = { elements[it].key },
        contentType = { elements[it].name }
    ) {
        SelectableItem(
            text = elements[it].name,
            onItemSelected = { onItemSelected(elements[it]) })
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


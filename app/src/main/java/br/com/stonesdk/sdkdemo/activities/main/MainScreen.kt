package br.com.stonesdk.sdkdemo.activities.main

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.com.stonesdk.sdkdemo.R
import org.koin.androidx.compose.getViewModel

@Composable
fun MainScreen(
    viewModel: MainViewModel = getViewModel(),
) {

    val context = LocalContext.current

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val generalItems = context.resources.getStringArray(R.array.main_options_generic).toList()

    val pinpadItems = context.resources.getStringArray(R.array.main_options_pinpad).toList()

    val posItems = context.resources.getStringArray(R.array.main_options_pos).toList()
    val posOptions = getOptionsIfPosAndroid(uiState.isPosAndroid, posItems)

    MainContent(
        generalItems = generalItems,
        pinpadItems = pinpadItems,
        posItems = posOptions,
        onItemSelected = {}
    )
}

@Composable
private fun getOptionsIfPosAndroid(
    isPosAndroid: Boolean,
    posItems: List<String>
): List<String> {
    return if (isPosAndroid) posItems
    else emptyList()
}
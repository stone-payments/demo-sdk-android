package br.com.stonesdk.sdkdemo.activities.main

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import br.com.stonesdk.sdkdemo.R
import org.koin.androidx.compose.getViewModel

@Composable
fun MainScreen(
    viewModel: MainViewModel = getViewModel(),
) {

    val context = LocalContext.current
    val generalItems = context.resources.getStringArray(R.array.main_options_generic).toList()
    val pinpadItems = context.resources.getStringArray(R.array.main_options_pinpad).toList()
    val posItems = context.resources.getStringArray(R.array.main_options_pos).toList()

    MainContent(
        generalItems = generalItems,
        pinpadItems = pinpadItems,
        posItems = posItems,
        onItemSelected = {}
    )
}
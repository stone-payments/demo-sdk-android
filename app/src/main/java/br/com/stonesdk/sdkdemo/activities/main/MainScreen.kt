package br.com.stonesdk.sdkdemo.activities.main

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun MainScreen(
    viewModel: MainViewModel,
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val navigateOption = uiState.navigateToOption
    LaunchedEffect(navigateOption) {
        if (navigateOption != null) {
            viewModel.doneNavigating()
            // navigateOption.navigate(context)
        }
    }

    MainContent(
        generalItems = uiState.generalNavigationOptions,
        pinpadItems = uiState.pinpadNavigationOptions,
        posItems = uiState.posNavigationOptions,
        onItemSelected = viewModel::navigateToOption
    )
}

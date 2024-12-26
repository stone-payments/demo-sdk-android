package br.com.stonesdk.sdkdemo.activities.main

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.androidx.compose.getViewModel

@Composable
fun MainScreen(
    viewModel: MainViewModel = getViewModel(),
) {

    val context = LocalContext.current

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    MainContent(
        generalItems = uiState.generalNavigationOptions,
        pinpadItems = uiState.pinpadNavigationOptions,
        posItems = uiState.posNavigationOptions,
        onItemSelected = viewModel::navigateToOption
    )
}

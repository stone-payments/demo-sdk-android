package br.com.stonesdk.sdkdemo.ui.splashscreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import br.com.stonesdk.sdkdemo.ui.components.LoadingContent
import br.com.stonesdk.sdkdemo.ui.main.MainNavigationOption
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun ValidationScreen(
    viewModel: ValidationViewModel = koinViewModel(),
    navController: NavController,
) {
    val uiModel by viewModel.uiState.collectAsStateWithLifecycle()

    val uiState = remember { derivedStateOf { uiModel.state } }

    when (uiState.value) {
        is SplashScreenState.Idle -> {
            viewModel.checkNeedToActivate()
        }

        is SplashScreenState.Loading -> {
            LoadingContent("")
        }

        is SplashScreenState.Activated -> {
            navController.navigate("home"){
                popUpTo("splash-screen") { inclusive = true }
            }
        }

        is SplashScreenState.NotActivated -> {
            ActivateContent {
                viewModel.activate(it)
            }
        }

        is SplashScreenState.Error -> {
            with(uiState.value as SplashScreenState.Error) {
                Column(modifier = Modifier.fillMaxSize()) {
                    Text("Error: $message")
                    Button(onClick = {
                        viewModel.activate(code)
                    }) {
                        viewModel.checkNeedToActivate()
                    }
                }
            }
        }
    }
}

@Composable
internal fun ActivateContent(
    onEvent: (String) -> Unit,
) {
    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = "Digite o Stone Code",
            fontSize = 18.sp,
            modifier = Modifier.align(Alignment.CenterHorizontally),
        )

        Spacer(modifier = Modifier.height(8.dp))

        var input by remember { mutableStateOf("") }
        TextField(
            value = input,
            onValueChange = { value ->
                input = value
            },
            modifier =
                Modifier
                    .width(170.dp)
                    .align(Alignment.CenterHorizontally),
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(onClick = {
            println(">>>>> input: $input")
            onEvent(input.trimIndent())
        }) {
            Text(text = "Ativar")
        }
    }
}

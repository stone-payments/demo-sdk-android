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
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import br.com.stonesdk.sdkdemo.ui.components.LoadingContent
import br.com.stonesdk.sdkdemo.utils.AppInfo
import co.stone.posmobile.lib.commons.platform.PlatformContext
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
internal fun ValidationScreen(
    context: PlatformContext,
    appInfo: AppInfo,
    viewModel: ValidationViewModel = viewModel { ValidationViewModel() },
    navController: NavController
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.initializeSDK(context,appInfo)
    }

    when (uiState) {
        is SplashScreenState.Loading -> {
            with(uiState as SplashScreenState.Loading) {
                LoadingContent(message)
            }
        }

        is SplashScreenState.Activated -> {
            println("teste activated")
            navController.navigate("home")
        }

        is SplashScreenState.NotActivated -> {
            ActivateContent {
                viewModel.activate(it)
            }
        }

        is SplashScreenState.Error -> {
            with(uiState as SplashScreenState.Error) {
                Column(modifier = Modifier.fillMaxSize()) {
                    Text("Error: $code - $message")
                    Button(onClick = {
                        viewModel.initializeSDK(context,appInfo)
                    }) {
                        Text(text = "Tentar novamente")
                    }

                }
            }
        }
    }
}


@Preview


@Preview
@Composable
internal fun ActivateContent(
    onEvent: (String) -> Unit,
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text = "Digite o Stone Code",
            fontSize = 18.sp,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(8.dp))

        var input by remember { mutableStateOf("") }
        TextField(
            value = input,
            onValueChange = { value ->
                input = value
            },
            modifier = Modifier
                .width(170.dp)
                .align(Alignment.CenterHorizontally),
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            colors = TextFieldDefaults.textFieldColors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            )
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
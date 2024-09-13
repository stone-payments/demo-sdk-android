package br.com.stonesdk.sdkdemo.activities.validation

import android.Manifest
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import br.com.stonesdk.sdkdemo.activities.validation.ValidationStoneCodeEvent.UserInput
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import org.koin.androidx.compose.getViewModel

@Composable
internal fun ValidationScreen(
    viewModel: ValidationViewModel = getViewModel(),
    navigateToMain: () -> Unit
) {

    val effects by viewModel.sideEffects.collectAsState()

    LaunchedEffect(effects) {
        effects?.let { event ->
            if (event == ValidationStoneCodeEffects.NavigateToMain) {
                navigateToMain()
            }
        }
    }
    val viewState = viewModel.viewState
    if (viewState.loading) {
        LinearProgressIndicator(
            modifier = Modifier.fillMaxWidth()
        )
    } else {
        ValidationContent(
            onEvent = viewModel::onEvent,
            model = viewModel.viewState
        )
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
internal fun ValidationContent(
    onEvent: (ValidationStoneCodeEvent) -> Unit,
    model: ValidationStoneCodeUiModel
) {
    var requestPermission by remember { mutableStateOf(false) }
    val permissionState = rememberPermissionState(Manifest.permission.READ_EXTERNAL_STORAGE)

    if (!permissionState.status.isGranted) {
        RequestStoragePermission(
            onPermissionGranted = {
                requestPermission = false
                onEvent(ValidationStoneCodeEvent.Permission)
            },
            permissionState = permissionState
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Ambiente",
            style = MaterialTheme.typography.titleMedium,
            fontSize = 18.sp,
            modifier = Modifier.padding(vertical = 8.dp)
        )
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp)

        ) {
            Text(
                text = "${model.getEnvironment}",
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.CenterHorizontally),
                style = MaterialTheme.typography.bodySmall
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Digite o Stone Code",
            fontSize = 18.sp,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = model.stoneCodeToBeValidated,
            onValueChange = { stoneCode -> onEvent(UserInput(stoneCode)) },
            modifier = Modifier
                .width(170.dp)
                .align(Alignment.CenterHorizontally),
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            )
        )


        Spacer(modifier = Modifier.height(24.dp))

        Button(onClick = {
            onEvent(ValidationStoneCodeEvent.Activate)
        }) {
            Text(text = "Ativar")
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@ExperimentalPermissionsApi
@Composable
fun RequestStoragePermission(
    onPermissionGranted: () -> Unit,
    permissionState: PermissionState
) {
    LaunchedEffect(permissionState) {
        when {
            permissionState.status.isGranted -> onPermissionGranted()
            else -> permissionState.launchPermissionRequest()
        }

    }
}
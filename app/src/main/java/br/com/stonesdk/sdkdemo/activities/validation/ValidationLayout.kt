package br.com.stonesdk.sdkdemo.activities.validation

import android.Manifest
import android.app.Activity
import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import br.com.stonesdk.sdkdemo.activities.validation.ValidationStoneCodeEvent.UserInput
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import org.koin.androidx.compose.getViewModel
import stone.environment.Environment

@OptIn(ExperimentalMaterial3Api::class)
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

    if (requestPermission || permissionState.status.isGranted) {
        RequestStoragePermission(
            onPermissionGranted = { /*TODO*/ },
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

        EnvironmentSpinner(
            selectedEnvironment = model.selectedEnvironment,
            onEnvironmentSelected = { environment ->
                onEvent(ValidationStoneCodeEvent.EnvironmentSelected(environment))
            }
        )

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
                .width(180.dp)
                .align(Alignment.CenterHorizontally),
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(onClick = {
            onEvent(ValidationStoneCodeEvent.Activate)
        }) {
            Text(text = "Ativar")
        }
    }
}

@Composable
fun EnvironmentSpinner(
    selectedEnvironment: Environment,
    onEnvironmentSelected: (Environment) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        TextField(
            value = selectedEnvironment.name,
            onValueChange = {},
            readOnly = true,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = true },
            trailingIcon = {
                IconButton(
                    content = {
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = null
                        )
                    },
                    onClick = {
                        expanded = !expanded
                    }
                )
            }
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            Environment.entries.forEach { environment ->
                DropdownMenuItem(
                    text = { Text(text = environment.name) },
                    onClick = {
                        onEnvironmentSelected(environment)
                        expanded = false
                    }
                )
            }

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

private fun checkPermission(context: Context, permission: String): Int {
    return ContextCompat.checkSelfPermission(context, permission)
}

private fun shouldShowRequestPermissionRationale(context: Context, permission: String): Boolean {
    return ActivityCompat.shouldShowRequestPermissionRationale(context as Activity, permission)
}
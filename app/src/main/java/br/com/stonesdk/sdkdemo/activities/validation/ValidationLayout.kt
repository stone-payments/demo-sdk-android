package br.com.stonesdk.sdkdemo.activities.validation

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
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.stonesdk.sdkdemo.activities.validation.ValidationViewModel.ValidationStoneCodeEvent
import br.com.stonesdk.sdkdemo.activities.validation.ValidationViewModel.ValidationStoneCodeEvent.UserInput
import br.com.stonesdk.sdkdemo.activities.validation.ValidationViewModel.ValidationStoneCodeUiModel
import org.koin.androidx.compose.getViewModel
import stone.environment.Environment

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ValidationScreen(
    viewModel: ValidationViewModel = getViewModel()
) {
    ValidationContent(
        onEvent = viewModel::onEvent,
        model = viewModel.viewState
    )

}

@Composable
internal fun ValidationContent(
    onEvent: (ValidationStoneCodeEvent) -> Unit,
    model: ValidationStoneCodeUiModel
) {

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
            modifier = Modifier.padding(top = 20.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

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
            ValidationStoneCodeEvent.Activated
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
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = null
                )
            }
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        )
        {
            Environment.entries.forEach { enviroment ->
                DropdownMenuItem(
                    text = { enviroment.name },
                    onClick = {
                        onEnvironmentSelected(enviroment)
                        expanded = false
                    }
                )
            }

        }
    }

}
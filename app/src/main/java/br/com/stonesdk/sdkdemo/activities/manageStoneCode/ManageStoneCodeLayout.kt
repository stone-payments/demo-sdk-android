package br.com.stonesdk.sdkdemo.activities.manageStoneCode

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.koin.androidx.compose.getViewModel


@Composable
fun ManageStoneCodeScreen(
    viewModel: ManageStoneCodeViewModel = getViewModel()
) {
    ManageStoneCodeContent(
        model = viewModel.viewState,
        onEvent = viewModel::onEvent
    )
}

@Composable
fun ManageStoneCodeContent(
    model: ManageStoneCodeUiModel,
    onEvent: (ManageStoneCodeEvent) -> Unit,
) {
    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxSize()
    ) {
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text(text = "Digite o stone code")
            },
            value = model.stoneCodeToBeActivated,
            onValueChange = { stoneCode ->
                onEvent(ManageStoneCodeEvent.UserInput(stoneCode))
            }
        )
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = {
                onEvent(ManageStoneCodeEvent.ActivateStoneCode)
            },
            enabled = model.stoneCodesActivated.isNotEmpty(),
            content = {
                Text(text = "Ativar")
            }
        )

        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "StoneCodes ativados")
        LazyColumn(modifier = Modifier.weight(1f)) {
            itemsIndexed(
                items = model.stoneCodesActivated,
                key = { index, stoneCode -> "$index$stoneCode" }
            ) { _, stoneCode ->
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Left,
                    text = stoneCode,
                    lineHeight = 32.sp,
                    fontSize = 18.sp
                )
            }
        }
    }
}
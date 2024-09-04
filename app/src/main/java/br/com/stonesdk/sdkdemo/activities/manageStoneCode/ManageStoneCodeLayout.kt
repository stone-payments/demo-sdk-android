package br.com.stonesdk.sdkdemo.activities.manageStoneCode

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.stonesdk.sdkdemo.activities.manageStoneCode.ManageStoneCodeEvent.ActivateStoneCode
import br.com.stonesdk.sdkdemo.activities.manageStoneCode.ManageStoneCodeEvent.AddStoneCode
import br.com.stonesdk.sdkdemo.activities.manageStoneCode.ManageStoneCodeEvent.OnDismiss
import br.com.stonesdk.sdkdemo.activities.manageStoneCode.ManageStoneCodeEvent.StoneCodeItemClick
import br.com.stonesdk.sdkdemo.activities.manageStoneCode.ManageStoneCodeEvent.UserInput
import org.koin.androidx.compose.getViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ManageStoneCodeScreen(
    viewModel: ManageStoneCodeViewModel = getViewModel()
) {

    if (viewModel.viewState.showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { viewModel.onEvent(OnDismiss) },
            content = {
                BottomSheetContent(
                    model = viewModel.viewState,
                    onEvent = viewModel::onEvent
                )
            }
        )
    }

    ManageStoneCodeContent(
        model = viewModel.viewState,
        onEvent = viewModel::onEvent,
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ManageStoneCodeContent(
    model: ManageStoneCodeUiModel,
    onEvent: (ManageStoneCodeEvent) -> Unit,
) {
    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .fillMaxSize()
    ) {

        Spacer(modifier = Modifier.height(16.dp))

        if (model.stoneCodesActivated.isEmpty()) {
            Text(
                modifier = Modifier
                    .padding(top = 40.dp)
                    .fillMaxWidth(),
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
                text = "Seus Stone codes ativados aparecerão aqui"
            )
        } else {
            Text(
                modifier = Modifier.padding(bottom = 8.dp),
                text = "Stone codes ativados",
                fontSize = 20.sp,
                fontWeight = FontWeight.W500
            )
        }
        LazyColumn(modifier = Modifier.weight(1f)) {
            itemsIndexed(
                items = model.stoneCodesActivated,
                key = { index, stoneCode -> "$index$stoneCode" },
                itemContent = { index, stoneCode ->
                    Column {
                        StoneCodeActivatedItem(
                            modifier = Modifier
                                .animateItemPlacement()
                                .clickable {
                                    onEvent(StoneCodeItemClick(position = index))
                                },
                            stoneCode = stoneCode,
                        )
                    }
                    if (model.stoneCodesActivated.lastIndex > index) {
                        HorizontalDivider()
                    }
                }
            )
        }
        Button(modifier = Modifier.align(Alignment.CenterHorizontally),
            onClick = { onEvent(AddStoneCode) },
            content = {
                Text(text = "Adicionar Stone Code")
            }
        )
    }
}

@Composable
private fun StoneCodeActivatedItem(
    modifier: Modifier = Modifier,
    stoneCode: String,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.heightIn(min = 44.dp)
    ) {
        Text(
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Left,
            text = stoneCode,
            fontSize = 18.sp
        )
        Icon(
            modifier = Modifier
                .size(18.dp)
                .padding(start = 4.dp),
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null
        )
    }
}

@Composable
private fun BottomSheetContent(
    model: ManageStoneCodeUiModel, onEvent: (ManageStoneCodeEvent) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text(text = "Digite o stone code")
            },
            value = model.stoneCodeToBeActivated,
            onValueChange = { stoneCode -> onEvent(UserInput(stoneCode)) },
            singleLine = true,
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
            keyboardActions = KeyboardActions(
                onDone = { onEvent(ActivateStoneCode) }
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .navigationBarsPadding(),
            onClick = { onEvent(ActivateStoneCode) },
            enabled = model.stoneCodeToBeActivated.isNotEmpty() && !model.activationInProgress,
            content = {
                if (model.activationInProgress) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .size(20.dp)
                            .padding(end = 4.dp)
                    )
                }
                Text(text = "Ativar")
            }
        )
    }
}
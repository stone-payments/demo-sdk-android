package br.com.stonesdk.sdkdemo.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp


@Composable
fun <T> BaseSpinner(
    title: String,
    onItemSelected: (T) -> Unit,
    selectedElement: T,
    elements: List<T>,
    elementNaming: (T) -> String,
    modifier: Modifier = Modifier,
) {

    val isDropDownExpanded = remember {
        mutableStateOf(false)
    }

    var selectedItem: T by remember {
        mutableStateOf(
            selectedElement
        )
    }

    Column(modifier = modifier) {
        Header(
            title = title,
            selectedElement = elementNaming(selectedItem),
        ) {
            isDropDownExpanded.value = true
        }
        DropdownMenu(
            expanded = isDropDownExpanded.value,
            onDismissRequest = { isDropDownExpanded.value = false }
        ) {
            elements.forEach {
                DropdownMenuItem(
                    text = { Text(text = elementNaming(it)) },
                    onClick = {
                        selectedItem = it
                        onItemSelected(it)
                        isDropDownExpanded.value = false
                    }
                )
            }
        }

    }
}

@Composable
private fun Header(
    title: String,
    selectedElement: String,
    onClick: () -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .border(
                border = BorderStroke(
                    width = 1.dp,
                    color = Color.LightGray
                ),
                shape = RoundedCornerShape(4.dp)
            )
            .clickable(
                onClick = onClick,
                role = Role.Button,
            )
    ) {
        Text(
            text = selectedElement,
            modifier = Modifier.padding(16.dp)
        )
        Spacer(modifier = Modifier.weight(1f))
        Icon(
            imageVector = Icons.Default.ArrowDropDown,
            contentDescription = null
        )
    }
}
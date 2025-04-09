package br.com.stonesdk.sdkdemo.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.UiComposable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.unit.dp

@Composable
fun DottedSpaceBetweenRowElements(
    startText: @Composable @UiComposable () -> Unit,
    endText: @Composable @UiComposable () -> Unit,
    modifier: Modifier = Modifier,
    dotSpacing: Float = 8f,
    lineThickness: Float = 4f
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {

        startText()

        Canvas(
            modifier = Modifier
                .height(1.dp)
                .weight(1f)
        ) {
            val canvasWidth = size.width
            val canvasHeight = size.height

            val pathEffect = PathEffect.dashPathEffect(
                intervals = floatArrayOf(dotSpacing, dotSpacing), phase = 0f
            )

            drawLine(
                color = Color.Black,
                start = Offset(x = 0f, y = canvasHeight / 2),
                end = Offset(x = canvasWidth, y = canvasHeight / 2),
                strokeWidth = lineThickness,
                pathEffect = pathEffect
            )
        }

        endText()
    }
}
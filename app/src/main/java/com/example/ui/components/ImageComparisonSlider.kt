package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.CyberPurple

@Composable
fun ImageComparisonSlider(
    originalImage: Any?,
    generatedImage: Any?,
    modifier: Modifier = Modifier
) {
    var sliderPosition by remember { mutableFloatStateOf(0.5f) }

    BoxWithConstraints(
        modifier = modifier
            .clipToBounds()
            .pointerInput(Unit) {
                detectHorizontalDragGestures { change, dragAmount ->
                    change.consume()
                    val newPos = sliderPosition + (dragAmount / size.width.toFloat())
                    sliderPosition = newPos.coerceIn(0.05f, 0.95f)
                }
            }
    ) {
        val widthPx = constraints.maxWidth.toFloat()
        val density = LocalDensity.current

        // 1. Base Generated Image (Right side / full width)
        AsyncImage(
            model = generatedImage,
            contentDescription = "Hasil AI",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Label Generated
        Surface(
            color = Color.Black.copy(alpha = 0.6f),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(8.dp)
        ) {
            Text(
                text = "Hasil AI",
                color = CyberCyan,
                fontSize = 11.sp,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
            )
        }

        // 2. Clipped Original Image (Left side)
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(sliderPosition)
                .clipToBounds()
        ) {
            AsyncImage(
                model = originalImage,
                contentDescription = "Gambar Asli",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxHeight()
                    .width(with(density) { widthPx.toDp() })
            )

            Surface(
                color = Color.Black.copy(alpha = 0.6f),
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(8.dp)
            ) {
                Text(
                    text = "Asli",
                    color = Color.White,
                    fontSize = 11.sp,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
        }

        // 3. Slider Handle Line
        val handleOffsetDp = with(density) { (widthPx * sliderPosition).toDp() }

        Box(
            modifier = Modifier
                .fillMaxHeight()
                .offset(x = handleOffsetDp - 1.5.dp)
                .width(3.dp)
                .background(CyberPurple)
        )

        Box(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .offset(x = handleOffsetDp - 18.dp)
                .size(36.dp)
                .background(CyberPurple, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.SwapHoriz,
                contentDescription = "Geser perbandingan",
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

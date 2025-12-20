package com.example.postagestampdiary.ui.album

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.*
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.postagestampdiary.data.local.StampEntity

@Composable
fun StampCard(
    stamp: StampEntity,
    onClick: () -> Unit,
    showInfo: Boolean = true,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .aspectRatio(3f / 4f)
            .clip(StampShape())
            .clickable { onClick() }
            .background(Color.White)
    ) {
        // Image
        AsyncImage(
            model = stamp.imagePath,
            contentDescription = stamp.location,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Info Overlay
        if (showInfo) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .background(Color.White.copy(alpha = 0.95f))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Column {
                    Text(
                        text = stamp.location,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        maxLines = 1
                    )
                    Text(
                        text = formatSimpleDate(stamp.createdAt),
                        fontSize = 8.sp,
                        color = Color.Gray
                    )
                }
            }
        }
        
        // Border
        Box(
            modifier = Modifier
                .fillMaxSize()
                .drawBehind {
                    val outline = StampShape().createOutline(size, layoutDirection, this)
                    if (outline is Outline.Generic) {
                        drawPath(
                            path = outline.path,
                            color = Color.LightGray.copy(alpha = 0.2f),
                            style = androidx.compose.ui.graphics.drawscope.Stroke(width = 1.dp.toPx())
                        )
                    }
                }
        )
    }
}

private fun formatSimpleDate(timestamp: Long): String {
    val date = java.util.Date(timestamp)
    val format = java.text.SimpleDateFormat("yyyy.MM.dd", java.util.Locale.getDefault())
    return format.format(date)
}

class StampShape : Shape {
    override fun createOutline(
        size: androidx.compose.ui.geometry.Size,
        layoutDirection: LayoutDirection,
        density: androidx.compose.ui.unit.Density
    ): Outline {
        val path = Path()
        val width = size.width
        val height = size.height
        
        // Approximation of the complex clipPath from the design
        // Instead of hardcoding 100+ points, we can generate them
        val stepX = width / 50f
        val stepY = height / 50f
        val indent = stepX // Depth of the perforation
        
        path.moveTo(0f, indent)
        
        // Top edge
        for (i in 0 until 50) {
            val x = i * stepX
            if (i % 2 == 0) {
                path.lineTo(x, indent)
                path.lineTo(x + stepX, indent)
            } else {
                path.lineTo(x, 0f)
                path.lineTo(x + stepX, 0f)
            }
        }
        
        // Right edge
        for (i in 0 until 50) {
            val y = i * stepY
            if (i % 2 == 0) {
                path.lineTo(width - indent, y)
                path.lineTo(width - indent, y + stepY)
            } else {
                path.lineTo(width, y)
                path.lineTo(width, y + stepY)
            }
        }
        
        // Bottom edge
        for (i in 50 downTo 1) {
            val x = i * stepX
            if (i % 2 == 0) {
                path.lineTo(x, height - indent)
                path.lineTo(x - stepX, height - indent)
            } else {
                path.lineTo(x, height)
                path.lineTo(x - stepX, height)
            }
        }
        
        // Left edge
        for (i in 50 downTo 1) {
            val y = i * stepY
            if (i % 2 == 0) {
                path.lineTo(indent, y)
                path.lineTo(indent, y - stepY)
            } else {
                path.lineTo(0f, y)
                path.lineTo(0f, y - stepY)
            }
        }
        
        path.close()
        return Outline.Generic(path)
    }
}

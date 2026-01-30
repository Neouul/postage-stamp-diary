package com.neouul.postagestampdiary.ui.album

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.neouul.postagestampdiary.data.local.StampEntity

@OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)
@Composable
fun StampCard(
    stamp: StampEntity,
    onClick: () -> Unit,
    onLongClick: (() -> Unit)? = null,
    showInfo: Boolean = true,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .aspectRatio(3f / 4f)
            .clip(StampShape(stamp.frameType))
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            )
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
                    val outline = StampShape(stamp.frameType).createOutline(size, layoutDirection, this)
                    if (outline is Outline.Generic) {
                        drawPath(
                            path = outline.path,
                            color = Color.LightGray.copy(alpha = 0.2f),
                            style = Stroke(width = 1.dp.toPx())
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



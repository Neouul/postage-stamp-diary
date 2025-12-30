package com.neouul.postagestampdiary.ui.detail

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil.compose.AsyncImage
import com.neouul.postagestampdiary.data.local.StampEntity
import com.neouul.postagestampdiary.data.repository.StampRepository
import com.neouul.postagestampdiary.ui.album.StampShape
import com.neouul.postagestampdiary.ui.theme.Primary
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val repository: StampRepository
) : ViewModel() {

    fun getStamp(id: Long): Flow<StampEntity?> {
        return repository.getStampByIdFlow(id)
    }

    fun updateMemo(stamp: StampEntity, memo: String) {
        viewModelScope.launch {
            repository.updateStamp(stamp.copy(memo = memo))
        }
    }

    fun deleteStamp(stamp: StampEntity, onComplete: () -> Unit) {
        viewModelScope.launch {
            repository.deleteStamp(stamp)
            onComplete()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    stampId: Long,
    onNavigateBack: () -> Unit,
    viewModel: DetailViewModel = hiltViewModel()
) {
    val stamp by viewModel.getStamp(stampId).collectAsState(initial = null)
    var isFlipped by remember { mutableStateOf(false) }
    var memoText by remember { mutableStateOf("") }
    var showDeleteDialog by remember { mutableStateOf(false) }
    
    LaunchedEffect(stamp) {
        stamp?.memo?.let { memoText = it }
    }

    val rotation by animateFloatAsState(
        targetValue = if (isFlipped) 180f else 0f,
        animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing),
        label = "flipRotation"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Stamp Detail", fontSize = 16.sp) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { showDeleteDialog = true }) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete")
                    }
                    IconButton(onClick = { /* Share */ }) {
                        Icon(Icons.Default.Share, contentDescription = "Share")
                    }
                }
            )
        },
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Button(
                    onClick = {
                        stamp?.let { viewModel.updateMemo(it, memoText) }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(4.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Primary)
                ) {
                    Text("Save")
                }
            }
        },
        containerColor = Color(0xFFF9F9F9)
    ) { paddingValues ->
        stamp?.let { currentStamp ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(3f / 4f)
                        .graphicsLayer {
                            rotationY = rotation
                            cameraDistance = 12f * density
                        }
                        .clickable { isFlipped = !isFlipped }
                ) {
                    if (rotation <= 90f) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(StampShape(currentStamp.frameType))
                                .background(Color.White)
                        ) {
                            AsyncImage(
                                model = currentStamp.imagePath,
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                            Box(
                                modifier = Modifier
                                    .align(Alignment.BottomCenter)
                                    .fillMaxWidth()
                                    .background(Color.White.copy(alpha = 0.9f))
                                    .padding(vertical = 12.dp, horizontal = 16.dp)
                            ) {
                                Column {
                                    Text(
                                        text = currentStamp.location,
                                        fontSize = 12.sp,
                                        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                                        color = Primary
                                    )
                                    Text(
                                        text = formatSimpleDate(currentStamp.createdAt),
                                        fontSize = 10.sp,
                                        color = Color.Gray
                                    )
                                }
                            }
                        }
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .graphicsLayer { rotationY = 180f }
                                .clip(StampShape(currentStamp.frameType))
                                .background(Color.White)
                                .padding(24.dp)
                        ) {
                            Column(modifier = Modifier.fillMaxSize()) {
                                InfoItem(label = "Date", value = formatSimpleDate(currentStamp.createdAt))
                                InfoItem(label = "Location", value = currentStamp.location)
                                InfoItem(label = "Category", value = currentStamp.category)
                                
                                Text(
                                    text = "Memo",
                                    fontSize = 12.sp,
                                    color = Color.Gray,
                                    modifier = Modifier.padding(top = 16.dp, bottom = 4.dp)
                                )
                                OutlinedTextField(
                                    value = memoText,
                                    onValueChange = { memoText = it },
                                    modifier = Modifier.fillMaxSize(),
                                    placeholder = { Text("Write your memory here...") },
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = Primary,
                                        unfocusedBorderColor = Color.LightGray
                                    )
                                )
                            }
                        }
                    }
                }
                
                Text(
                    text = "Tap to flip the stamp",
                    modifier = Modifier.padding(top = 16.dp),
                    fontSize = 12.sp,
                    color = Color.LightGray
                )
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Stamp") },
            text = { Text("Are you sure you want to delete this stamp?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        stamp?.let { 
                            viewModel.deleteStamp(it) {
                                onNavigateBack()
                            }
                        }
                    }
                ) {
                    Text("Delete", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun InfoItem(label: String, value: String) {
    Column(modifier = Modifier.padding(bottom = 12.dp)) {
        Text(text = label, fontSize = 12.sp, color = Color.Gray)
        Text(text = value, fontSize = 16.sp, color = Primary)
    }
}

private fun formatSimpleDate(timestamp: Long): String {
    val date = java.util.Date(timestamp)
    val format = java.text.SimpleDateFormat("yyyy.MM.dd", java.util.Locale.getDefault())
    return format.format(date)
}

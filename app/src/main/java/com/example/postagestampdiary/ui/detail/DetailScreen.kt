package com.example.postagestampdiary.ui.detail

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.postagestampdiary.data.local.StampEntity
import com.example.postagestampdiary.data.repository.StampRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val repository: StampRepository
) : ViewModel() {
    var stamp by mutableStateOf<StampEntity?>(null)
        private set

    fun loadStamp(id: Long) {
        viewModelScope.launch {
            stamp = repository.getStampById(id)
        }
    }
}

@Composable
fun DetailScreen(
    stampId: Long,
    viewModel: DetailViewModel = hiltViewModel()
) {
    LaunchedEffect(stampId) {
        viewModel.loadStamp(stampId)
    }

    Scaffold { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            viewModel.stamp?.let { stamp ->
                Image(
                    painter = rememberAsyncImagePainter(
                        ImageRequest.Builder(LocalContext.current)
                            .data(File(stamp.imagePath))
                            .build()
                    ),
                    contentDescription = "Detail Stamp",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.fillMaxSize(0.8f)
                )
            } ?: Text("Loading...")
        }
    }
}

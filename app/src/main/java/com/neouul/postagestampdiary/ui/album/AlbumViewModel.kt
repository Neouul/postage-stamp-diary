package com.neouul.postagestampdiary.ui.album

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neouul.postagestampdiary.data.local.StampEntity
import com.neouul.postagestampdiary.data.repository.StampRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class AlbumViewModel @Inject constructor(
    repository: StampRepository
) : ViewModel() {
    val stamps: StateFlow<List<StampEntity>> = repository.allStamps
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
}

package com.example.postagestampdiary.ui.settings

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor() : ViewModel() {
    private val _showInfoOnAlbum = MutableStateFlow(true)
    val showInfoOnAlbum: StateFlow<Boolean> = _showInfoOnAlbum.asStateFlow()

    private val _darkMode = MutableStateFlow(false)
    val darkMode: StateFlow<Boolean> = _darkMode.asStateFlow()

    private val _defaultFrameStyle = MutableStateFlow("Classic")
    val defaultFrameStyle: StateFlow<String> = _defaultFrameStyle.asStateFlow()

    fun toggleShowInfo() {
        _showInfoOnAlbum.value = !_showInfoOnAlbum.value
    }

    fun toggleDarkMode() {
        _darkMode.value = !_darkMode.value
    }

    fun cycleFrameStyle() {
        val styles = listOf("Classic", "Modern", "Vintage", "Minimal")
        val currentIndex = styles.indexOf(_defaultFrameStyle.value)
        val nextIndex = (currentIndex + 1) % styles.size
        _defaultFrameStyle.value = styles[nextIndex]
    }
}

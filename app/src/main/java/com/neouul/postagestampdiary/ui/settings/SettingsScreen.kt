package com.neouul.postagestampdiary.ui.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.neouul.postagestampdiary.ui.theme.Primary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val showInfoOnAlbum by viewModel.showInfoOnAlbum.collectAsState()
    val darkMode by viewModel.darkMode.collectAsState()
    val defaultFrameStyle by viewModel.defaultFrameStyle.collectAsState()

    Scaffold(
        topBar = {
            Column {
                Spacer(modifier = Modifier.statusBarsPadding())
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Primary
                        )
                    }
                    Text(
                        text = "Settings",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        color = Primary
                    )
                    Box(modifier = Modifier.size(48.dp)) // Spacer to center title
                }
                HorizontalDivider(color = Color(0xFFF1F1F1))
            }
        },
        containerColor = Color.White
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp)
        ) {
            // Show Info on Album
            SettingsToggleItem(
                title = "Show Info on Album",
                description = "Display date and location on stamps",
                checked = showInfoOnAlbum,
                onCheckedChange = { viewModel.toggleShowInfo() }
            )

            // Default Frame Style
            SettingsClickItem(
                title = "Default Frame Style",
                value = defaultFrameStyle,
                onClick = { viewModel.cycleFrameStyle() }
            )

            // Backup & Restore
            SettingsClickItem(
                title = "Backup & Restore",
                value = "Save your stamps to cloud",
                onClick = { /* Implement Backup */ }
            )

            // Dark Mode
            SettingsToggleItem(
                title = "Dark Mode",
                description = "Switch to dark theme",
                checked = darkMode,
                onCheckedChange = { viewModel.toggleDarkMode() }
            )

            // App Info
            Spacer(modifier = Modifier.weight(1f))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Momento",
                    fontSize = 14.sp,
                    color = Color.LightGray,
                    fontFamily = FontFamily.Serif
                )
                Text(
                    text = "Version 1.0.0",
                    fontSize = 12.sp,
                    color = Color.LightGray
                )
            }
        }
    }
}

@Composable
fun SettingsToggleItem(
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, fontSize = 16.sp, color = Primary)
                Text(text = description, fontSize = 14.sp, color = Color.Gray)
            }
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = Primary,
                    uncheckedThumbColor = Color.White,
                    uncheckedTrackColor = Color(0xFFE5E5E5),
                    uncheckedBorderColor = Color.Transparent
                )
            )
        }
        HorizontalDivider(color = Color(0xFFF1F1F1))
    }
}

@Composable
fun SettingsClickItem(
    title: String,
    value: String,
    onClick: () -> Unit
) {
    Column(modifier = Modifier.clickable { onClick() }) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, fontSize = 16.sp, color = Primary)
                Text(text = value, fontSize = 14.sp, color = Color.Gray)
            }
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = Color.LightGray
            )
        }
        HorizontalDivider(color = Color(0xFFF1F1F1))
    }
}

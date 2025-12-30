package com.neouul.postagestampdiary.ui.album

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.neouul.postagestampdiary.ui.theme.Background
import com.neouul.postagestampdiary.ui.theme.Primary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlbumScreen(
    onNavigateToCamera: () -> Unit,
    onNavigateToDetail: (Long) -> Unit,
    onNavigateToSettings: () -> Unit,
    viewModel: AlbumViewModel = hiltViewModel()
) {
    val stamps by viewModel.stamps.collectAsState()
    var activeCategory by remember { mutableStateOf("All") }
    var showFilterMenu by remember { mutableStateOf(false) }
    var visibleMenuStampId by remember { mutableStateOf<Long?>(null) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var stampToDelete by remember { mutableStateOf<com.neouul.postagestampdiary.data.local.StampEntity?>(null) }
    
    val filteredStamps = if (activeCategory == "All") {
        stamps
    } else {
        stamps.filter { it.category == activeCategory }
    }

    Scaffold(
        topBar = {
            Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 32.dp, bottom = 16.dp)
                ) {
                    // Title: Momento (Serif style)
                    Text(
                        text = "Momento",
                        modifier = Modifier.align(Alignment.Center),
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Light,
                        letterSpacing = 2.sp,
                        color = Primary,
                        style = TextStyle(
                            fontFamily = FontFamily.Serif
                        )
                    )

                    // Icons on the right
                    Row(
                        modifier = Modifier.align(Alignment.CenterEnd),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box {
                            IconButton(onClick = { showFilterMenu = !showFilterMenu }) {
                                Icon(
                                    imageVector = Icons.Default.FilterList,
                                    contentDescription = "Filter",
                                    tint = Color.Gray
                                )
                            }
                            DropdownMenu(
                                expanded = showFilterMenu,
                                onDismissRequest = { showFilterMenu = false }
                            ) {
                                listOf("All", "Travel", "Food", "Daily").forEach { category ->
                                    DropdownMenuItem(
                                        text = { Text(category) },
                                        onClick = {
                                            activeCategory = category
                                            showFilterMenu = false
                                        }
                                    )
                                }
                            }
                        }
                        IconButton(onClick = onNavigateToSettings) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "Settings",
                                tint = Color.Gray
                            )
                        }
                    }
                }
                
                // Active Filter Indicator
                if (activeCategory != "All") {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Showing: $activeCategory",
                            fontSize = 12.sp,
                            color = Color.LightGray
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Clear",
                            fontSize = 12.sp,
                            color = Primary,
                            modifier = Modifier.clickable { activeCategory = "All" },
                            style = TextStyle(
                                textDecoration = TextDecoration.Underline
                            )
                        )
                    }
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToCamera,
                containerColor = Color.White,
                contentColor = Primary,
                shape = androidx.compose.foundation.shape.CircleShape,
                modifier = Modifier
                    .padding(bottom = 16.dp, end = 16.dp)
                    .size(56.dp)
                    .drawBehind {
                        drawCircle(
                            color = Color.LightGray.copy(alpha = 0.5f),
                            radius = size.width / 2,
                            style = Stroke(width = 1.dp.toPx())
                        )
                    },
                elevation = FloatingActionButtonDefaults.elevation(4.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Stamp")
            }
        },
        containerColor = Background
    ) { paddingValues ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            items(filteredStamps) { stamp ->
                Box {
                    StampCard(
                        stamp = stamp,
                        onClick = { onNavigateToDetail(stamp.id) },
                        onLongClick = { visibleMenuStampId = stamp.id }
                    )
                    
                    DropdownMenu(
                        expanded = visibleMenuStampId == stamp.id,
                        onDismissRequest = { visibleMenuStampId = null }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Delete", color = Color.Red) },
                            onClick = {
                                visibleMenuStampId = null
                                stampToDelete = stamp
                                showDeleteDialog = true
                            }
                        )
                    }
                }
            }
        }

        if (showDeleteDialog && stampToDelete != null) {
            AlertDialog(
                onDismissRequest = { 
                    showDeleteDialog = false 
                    stampToDelete = null
                },
                title = { Text("Delete Stamp") },
                text = { Text("Are you sure you want to delete this stamp?") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            stampToDelete?.let { 
                                viewModel.deleteStamp(it)
                            }
                            showDeleteDialog = false
                            stampToDelete = null
                        }
                    ) {
                        Text("Delete", color = Color.Red)
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { 
                            showDeleteDialog = false 
                            stampToDelete = null
                        }
                    ) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

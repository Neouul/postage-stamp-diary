package com.example.postagestampdiary

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.postagestampdiary.ui.theme.PostageStampDiaryTheme
import dagger.hilt.android.AndroidEntryPoint

import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.postagestampdiary.ui.album.AlbumScreen
import com.example.postagestampdiary.ui.camera.CameraScreen
import com.example.postagestampdiary.ui.detail.DetailScreen

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PostageStampDiaryTheme {
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = "home") {
                    composable("home") {
                        AlbumScreen(
                            onNavigateToCamera = { navController.navigate("camera") },
                            onNavigateToDetail = { id -> navController.navigate("detail/$id") }
                        )
                    }
                    composable("camera") {
                        CameraScreen(
                            onNavigateBack = { navController.popBackStack() }
                        )
                    }
                    composable(
                        route = "detail/{stampId}",
                        arguments = listOf(navArgument("stampId") { type = NavType.LongType })
                    ) { backStackEntry ->
                        val stampId = backStackEntry.arguments?.getLong("stampId") ?: -1L
                        DetailScreen(stampId = stampId)
                    }
                }
            }
        }
    }
}
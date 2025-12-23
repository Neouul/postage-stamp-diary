package com.neouul.postagestampdiary

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.neouul.postagestampdiary.ui.theme.PostageStampDiaryTheme
import dagger.hilt.android.AndroidEntryPoint

import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.neouul.postagestampdiary.ui.album.AlbumScreen
import com.neouul.postagestampdiary.ui.camera.CameraScreen
import com.neouul.postagestampdiary.ui.detail.DetailScreen
import com.neouul.postagestampdiary.ui.settings.SettingsScreen

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
                            onNavigateToDetail = { id -> navController.navigate("detail/$id") },
                            onNavigateToSettings = { navController.navigate("settings") }
                        )
                    }
                    composable("camera") {
                        CameraScreen(
                            onNavigateBack = { navController.popBackStack() },
                            onCaptureSuccess = { navController.popBackStack() },
                            onNavigateToSettings = { navController.navigate("settings") }
                        )
                    }
                    composable("settings") {
                        SettingsScreen(
                            onNavigateBack = { navController.popBackStack() }
                        )
                    }
                    composable(
                        route = "detail/{stampId}",
                        arguments = listOf(navArgument("stampId") { type = NavType.LongType })
                    ) { backStackEntry ->
                        val stampId = backStackEntry.arguments?.getLong("stampId") ?: -1L
                        DetailScreen(
                            stampId = stampId,
                            onNavigateBack = { navController.popBackStack() }
                        )
                    }
                }
            }
        }
    }
}
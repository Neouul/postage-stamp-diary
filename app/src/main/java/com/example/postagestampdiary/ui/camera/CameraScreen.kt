package com.example.postagestampdiary.ui.camera

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.view.ViewGroup
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.compose.ui.graphics.PathOperation
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.postagestampdiary.ui.theme.OldLace
import java.io.File
import java.util.concurrent.Executor

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.material3.Text
import android.Manifest

@Composable
fun CameraScreen(
    onNavigateBack: () -> Unit,
    viewModel: CameraViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var hasCameraPermission by remember { mutableStateOf(false) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted ->
            hasCameraPermission = granted
        }
    )

    LaunchedEffect(Unit) {
        launcher.launch(Manifest.permission.CAMERA)
    }
    
    if (hasCameraPermission) {
        val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
        val imageCapture = remember { 
            ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                .build() 
        }

        Box(modifier = Modifier.fillMaxSize()) {
            AndroidView(
                factory = { ctx ->
                    PreviewView(ctx).apply {
                        layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )
                        scaleType = PreviewView.ScaleType.FILL_CENTER
                    }
                },
                update = { previewView ->
                    val cameraProvider = cameraProviderFuture.get()
                    val preview = Preview.Builder().build().also {
                        it.setSurfaceProvider(previewView.surfaceProvider)
                    }
                    
                    try {
                        cameraProvider.unbindAll()
                        cameraProvider.bindToLifecycle(
                            lifecycleOwner,
                            CameraSelector.DEFAULT_BACK_CAMERA,
                            preview,
                            imageCapture
                        )
                    } catch (exc: Exception) {
                        Log.e("CameraScreen", "Use case binding failed", exc)
                    }
                },
                modifier = Modifier.fillMaxSize()
            )

            // Stamp Frame Overlay
            StampOverlay(modifier = Modifier.fillMaxSize())

            // Capture Button
            IconButton(
                onClick = {
                    takePhoto(
                        context = context,
                        imageCapture = imageCapture,
                        executor = ContextCompat.getMainExecutor(context),
                        onImageCaptured = { bitmap ->
                            viewModel.saveStamp(bitmap) {
                                onNavigateBack()
                            }
                        }
                    )
                },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 32.dp)
                    .size(72.dp)
                    .background(OldLace, shape = androidx.compose.foundation.shape.CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.PhotoCamera,
                    contentDescription = "Take Photo",
                    tint = Color.Black
                )
            }
        }
    } else {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(text = "Camera permission is required to take photos.")
        }
    }
}

@Composable
fun StampOverlay(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height
        
        // Stamp dimensions (relative to screen, same logic as before)
        // Adjust these to change the frame size if needed
        val framePadding = 40.dp.toPx()
        val stampWidth = width - (framePadding * 2)
        // Aspect ratio for stamp (e.g., 4:5 or square?)
        // Let's keep it somewhat rectangular or match viewport
        // For now, let's use a fixed padding from edges
        val stampHeight = height - (framePadding * 4) // More padding top/bottom for UI
        
        val left = (width - stampWidth) / 2
        val top = (height - stampHeight) / 2
        val right = left + stampWidth
        val bottom = top + stampHeight
        
        val holeRadius = stampWidth * 0.02f
        val spacing = holeRadius * 3

        // 1. Create Path for the Stamp Shape (Rectangle - Holes)
        val stampPath = Path().apply {
            addRect(androidx.compose.ui.geometry.Rect(left, top, right, bottom))
        }
        
        val holesPath = Path().apply {
            // Top & Bottom
            val numHolesX = (stampWidth / spacing).toInt()
            for (i in 0 until numHolesX) {
                val cx = left + i * spacing + spacing / 2
                addOval(androidx.compose.ui.geometry.Rect(
                    center = Offset(cx, top),
                    radius = holeRadius
                ))
                addOval(androidx.compose.ui.geometry.Rect(
                    center = Offset(cx, bottom),
                    radius = holeRadius
                ))
            }
            
            // Left & Right
            val numHolesY = (stampHeight / spacing).toInt()
            for (i in 0 until numHolesY) {
                val cy = top + i * spacing + spacing / 2
                addOval(androidx.compose.ui.geometry.Rect(
                    center = Offset(left, cy),
                    radius = holeRadius
                ))
                addOval(androidx.compose.ui.geometry.Rect(
                    center = Offset(right, cy),
                    radius = holeRadius
                ))
            }
        }

        // Combine to get the actual Stamp Shape (Body - Holes)
        // Note: We need to use PathOperation.Difference
        val finalStampShape = Path.combine(
            androidx.compose.ui.graphics.PathOperation.Difference,
            stampPath,
            holesPath
        )

        // 2. Create Path for the Full Screen Overlay
        val screenPath = Path().apply {
            addRect(androidx.compose.ui.geometry.Rect(0f, 0f, width, height))
        }

        // 3. Create the Scrim Path (Screen - StampShape)
        // This effectively makes the StampShape transparent, and everything else dark
        val scrimPath = Path.combine(
            androidx.compose.ui.graphics.PathOperation.Difference,
            screenPath,
            finalStampShape
        )

        // 4. Draw the Scrim
        drawPath(
            path = scrimPath,
            color = Color.Black.copy(alpha = 0.7f) // Darker overlap
        )
        
        // Optional: Draw a thin white border around the stamp shape for better visibility
        drawPath(
            path = finalStampShape,
            color = Color.White.copy(alpha = 0.5f),
            style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2.dp.toPx())
        )
    }
}

private fun takePhoto(
    context: Context,
    imageCapture: ImageCapture,
    executor: Executor,
    onImageCaptured: (Bitmap) -> Unit
) {
    val photoFile = File.createTempFile("temp", ".jpg", context.cacheDir)
    val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

    imageCapture.takePicture(
        outputOptions,
        executor,
        object : ImageCapture.OnImageSavedCallback {
            override fun onError(exc: ImageCaptureException) {
                Log.e("CameraScreen", "Photo capture failed: ${exc.message}", exc)
            }

            override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                // Decode bitmap
                val bitmap = BitmapFactory.decodeFile(photoFile.absolutePath)
                // Resize for optimization (e.g., 1080x1080 effectively)
                // Basic scaling for now
                val scaledBitmap = Bitmap.createScaledBitmap(bitmap, 1080, (1080 * bitmap.height / bitmap.width), true)
                onImageCaptured(scaledBitmap)
                photoFile.delete() // clean temp
            }
        }
    )
}

package com.neouul.postagestampdiary.ui.camera

import android.Manifest
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.exifinterface.media.ExifInterface
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathOperation
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.LifecycleOwner
import java.io.File
import java.util.concurrent.Executor

@Composable
fun CameraScreen(
    onNavigateBack: () -> Unit,
    onCaptureSuccess: () -> Unit,
    onNavigateToSettings: () -> Unit,
    viewModel: CameraViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val previewView = remember { PreviewView(context) }
    val imageCapture = remember { ImageCapture.Builder().build() }
    
    var flashEnabled by remember { mutableStateOf(false) }
    var activeFrameStyle by remember { mutableStateOf("Classic") }
    val frameStyles = listOf("Classic", "Modern", "Vintage", "Minimal")

    // Request permissions
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            startCamera(context, lifecycleOwner, previewView, imageCapture)
        }
    }

    LaunchedEffect(Unit) {
        permissionLauncher.launch(Manifest.permission.CAMERA)
    }

    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        // Camera Preview
        AndroidView(
            factory = { previewView },
            modifier = Modifier.fillMaxSize()
        )

        // Masked Overlay
        StampOverlay()

        // Top Bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.verticalGradient(listOf(Color.Black.copy(alpha = 0.5f), Color.Transparent)))
                .padding(top = 32.dp, start = 16.dp, end = 16.dp, bottom = 16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onNavigateBack,
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color.White.copy(alpha = 0.1f), CircleShape)
                ) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    IconButton(
                        onClick = { flashEnabled = !flashEnabled },
                        modifier = Modifier
                            .size(40.dp)
                            .background(if (flashEnabled) Color.White.copy(alpha = 0.3f) else Color.White.copy(alpha = 0.1f), CircleShape)
                    ) {
                        Icon(Icons.Default.FlashOn, contentDescription = "Flash", tint = Color.White)
                    }
                    IconButton(
                        onClick = onNavigateToSettings,
                        modifier = Modifier
                            .size(40.dp)
                            .background(Color.White.copy(alpha = 0.1f), CircleShape)
                    ) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings", tint = Color.White)
                    }
                }
            }
        }

        // Bottom Controls
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .background(Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = 0.5f))))
                .padding(bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Frame Style Selector
            LazyRow(
                contentPadding = PaddingValues(horizontal = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.padding(bottom = 24.dp)
            ) {
                items(frameStyles) { style ->
                    val isSelected = activeFrameStyle == style
                    Surface(
                        onClick = { activeFrameStyle = style },
                        shape = CircleShape,
                        color = if (isSelected) Color.White else Color.White.copy(alpha = 0.1f),
                        contentColor = if (isSelected) Color.Black else Color.White,
                        modifier = Modifier.height(36.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(horizontal = 16.dp)) {
                            Text(text = style, fontSize = 14.sp)
                        }
                    }
                }
            }

            // Shutter Button
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.3f))
                    .padding(4.dp)
                    .clip(CircleShape)
                    .background(Color.White)
                    .clickable {
                        captureImage(context, imageCapture, ContextCompat.getMainExecutor(context)) { bitmap ->
                            viewModel.saveStamp(bitmap, onComplete = onCaptureSuccess)
                        }
                    }
            )
        }
    }
}

private fun startCamera(
    context: Context,
    lifecycleOwner: LifecycleOwner,
    previewView: PreviewView,
    imageCapture: ImageCapture
) {
    val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
    cameraProviderFuture.addListener({
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
    }, ContextCompat.getMainExecutor(context))
}

private fun captureImage(
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
                val bitmap = BitmapFactory.decodeFile(photoFile.absolutePath)
                
                // Handle Rotation
                val exif = ExifInterface(photoFile.absolutePath)
                val orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
                val matrix = android.graphics.Matrix()
                when (orientation) {
                    ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(90f)
                    ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(180f)
                    ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(270f)
                }
                
                val rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
                
                // Crop to 3:4 Vertical
                val targetAspect = 3f / 4f
                val currentAspect = rotatedBitmap.width.toFloat() / rotatedBitmap.height.toFloat()
                
                val finalBitmap = if (currentAspect > targetAspect) {
                    // Landscape or wider than 3:4 -> Crop sides
                    val targetWidth = (rotatedBitmap.height * targetAspect).toInt()
                    val offset = (rotatedBitmap.width - targetWidth) / 2
                    Bitmap.createBitmap(rotatedBitmap, offset, 0, targetWidth, rotatedBitmap.height)
                } else {
                    // Narrower than 3:4 -> Crop top/bottom
                    val targetHeight = (rotatedBitmap.width / targetAspect).toInt()
                    val offset = (rotatedBitmap.height - targetHeight) / 2
                    Bitmap.createBitmap(rotatedBitmap, 0, offset, rotatedBitmap.width, targetHeight)
                }
                
                onImageCaptured(finalBitmap)
                photoFile.delete()
            }
        }
    )
}

@Composable
fun StampOverlay(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height
        
        val framePadding = 40.dp.toPx()
        val stampWidth = width - (framePadding * 2)
        val stampHeight = stampWidth * (4f / 3f)
        
        val left = (width - stampWidth) / 2
        val top = (height - stampHeight) / 2
        val right = left + stampWidth
        val bottom = top + stampHeight
        
        val holeRadius = stampWidth * 0.02f
        val spacing = holeRadius * 3

        val stampPath = Path().apply {
            addRect(Rect(left, top, right, bottom))
        }
        
        val holesPath = Path().apply {
            val numHolesX = (stampWidth / spacing).toInt()
            for (i in 0 until numHolesX) {
                val cx = left + i * spacing + spacing / 2
                addOval(Rect(center = Offset(cx, top), radius = holeRadius))
                addOval(Rect(center = Offset(cx, bottom), radius = holeRadius))
            }
            
            val numHolesY = (stampHeight / spacing).toInt()
            for (i in 0 until numHolesY) {
                val cy = top + i * spacing + spacing / 2
                addOval(Rect(center = Offset(left, cy), radius = holeRadius))
                addOval(Rect(center = Offset(right, cy), radius = holeRadius))
            }
        }

        val finalStampShape = Path.combine(
            PathOperation.Difference,
            stampPath,
            holesPath
        )

        val screenPath = Path().apply {
            addRect(Rect(0f, 0f, width, height))
        }

        val scrimPath = Path.combine(
            PathOperation.Difference,
            screenPath,
            finalStampShape
        )

        drawPath(
            path = scrimPath,
            color = Color.Black.copy(alpha = 0.7f)
        )
        
        drawPath(
            path = finalStampShape,
            color = Color.White.copy(alpha = 0.5f),
            style = Stroke(width = 2.dp.toPx())
        )
    }
}

package com.example.postagestampdiary.ui.camera

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.RectF
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.postagestampdiary.data.repository.StampRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.min

@HiltViewModel
class CameraViewModel @Inject constructor(
    private val repository: StampRepository
) : ViewModel() {

    fun saveStamp(
        bitmap: Bitmap, 
        location: String = "Travel", 
        category: String = "Daily",
        onComplete: () -> Unit
    ) {
        viewModelScope.launch {
            // Save clean bitmap. UI will handle the stamp perforation effect.
            repository.saveStamp(bitmap, null, location, category)
            onComplete()
        }
    }

    private fun applyStampMask(source: Bitmap): Bitmap {
        // Optimization: Ensure bitmap is not too huge before processing
        val maxDimension = 1080
        val scale = if (source.width > maxDimension || source.height > maxDimension) {
            min(maxDimension.toFloat() / source.width, maxDimension.toFloat() / source.height)
        } else {
            1f
        }
        
        val width = (source.width * scale).toInt()
        val height = (source.height * scale).toInt()
        
        // Re-scale source if needed
        val scaledSource = if (scale != 1f) {
            Bitmap.createScaledBitmap(source, width, height, true)
        } else {
            source
        }

        val output = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(output)
        
        val paint = Paint()
        paint.isAntiAlias = true
        
        // Draw Stamp Shape (Rectangle with holes)
        val path = Path()
        val holeRadius = width * 0.02f
        val spacing = holeRadius * 3
        
        // Draw main body
        path.addRect(0f, 0f, width.toFloat(), height.toFloat(), Path.Direction.CW)
        
        // Punch holes
        val holePaint = Paint().apply {
            xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_OUT)
            isAntiAlias = true
        }

        // Draw source content first
        canvas.drawBitmap(scaledSource, 0f, 0f, null)
        
        // Erase holes
        // Top & Bottom
        for (i in 0 until (width / spacing).toInt()) {
            val cx = i * spacing + spacing / 2
            canvas.drawCircle(cx, 0f, holeRadius, holePaint)
            canvas.drawCircle(cx, height.toFloat(), holeRadius, holePaint)
        }
        // Left & Right
         for (i in 0 until (height / spacing).toInt()) {
            val cy = i * spacing + spacing / 2
            canvas.drawCircle(0f, cy, holeRadius, holePaint)
            canvas.drawCircle(width.toFloat(), cy, holeRadius, holePaint)
        }

        return output
    }
}

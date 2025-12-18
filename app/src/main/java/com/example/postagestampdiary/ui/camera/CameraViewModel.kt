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

    fun saveStamp(bitmap: Bitmap, onComplete: () -> Unit) {
        viewModelScope.launch {
            // Apply Stamp Mask before saving
            val maskedBitmap = applyStampMask(bitmap)
            repository.saveStamp(maskedBitmap, null)
            onComplete()
        }
    }

    private fun applyStampMask(source: Bitmap): Bitmap {
        val width = source.width
        val height = source.height
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

        canvas.drawPath(path, paint)
        
        // Improve masking logic later: currently just drawing source on top for simplicity,
        // but real stamp effect needs punch holes.
        // Simplified Logic: 
        // 1. Draw source
        canvas.drawBitmap(source, 0f, 0f, null)
        
        // 2. Erase holes
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

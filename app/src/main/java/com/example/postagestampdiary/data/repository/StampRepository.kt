package com.example.postagestampdiary.data.repository

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.example.postagestampdiary.data.local.StampDao
import com.example.postagestampdiary.data.local.StampEntity
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StampRepository @Inject constructor(
    private val stampDao: StampDao,
    @ApplicationContext private val context: Context
) {

    val allStamps: Flow<List<StampEntity>> = stampDao.getAllStamps()

    suspend fun getStampById(id: Long): StampEntity? {
        return stampDao.getStampById(id)
    }


    suspend fun saveStamp(bitmap: Bitmap, memo: String?): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                // Use PNG as requested by user
                val filename = "stamp_${UUID.randomUUID()}.png"
                val file = File(context.filesDir, filename)
                
                FileOutputStream(file).use { out ->
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
                }

                val stamp = StampEntity(
                    imagePath = file.absolutePath,
                    memo = memo
                )
                stampDao.insertStamp(stamp)
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun deleteStamp(stamp: StampEntity) {
        withContext(Dispatchers.IO) {
            val file = File(stamp.imagePath)
            if (file.exists()) {
                file.delete()
            }
            stampDao.deleteStamp(stamp)
        }
    }
}

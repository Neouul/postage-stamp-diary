package com.example.postagestampdiary.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface StampDao {
    @Query("SELECT * FROM stamps ORDER BY createdAt DESC")
    fun getAllStamps(): Flow<List<StampEntity>>

    @Query("SELECT * FROM stamps WHERE id = :id")
    suspend fun getStampById(id: Long): StampEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStamp(stamp: StampEntity)

    @Delete
    suspend fun deleteStamp(stamp: StampEntity)
}

package com.example.postagestampdiary.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "stamps")
data class StampEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val imagePath: String,
    val frameType: String = "default",
    val memo: String? = null,
    val location: String = "Unknown",
    val category: String = "Daily",
    val createdAt: Long = System.currentTimeMillis()
)

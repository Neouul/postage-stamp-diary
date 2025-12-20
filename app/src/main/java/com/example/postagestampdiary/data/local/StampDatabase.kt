package com.example.postagestampdiary.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [StampEntity::class], version = 2, exportSchema = false)
abstract class StampDatabase : RoomDatabase() {
    abstract fun stampDao(): StampDao
}

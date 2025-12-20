package com.example.postagestampdiary.di

import android.content.Context
import androidx.room.Room
import com.example.postagestampdiary.data.local.StampDao
import com.example.postagestampdiary.data.local.StampDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): StampDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            StampDatabase::class.java,
            "stamp_database"
        ).fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideStampDao(database: StampDatabase): StampDao {
        return database.stampDao()
    }
}

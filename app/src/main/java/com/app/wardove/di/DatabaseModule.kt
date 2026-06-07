package com.app.wardove.di

import android.content.Context
import androidx.room.Room
import com.app.wardove.data.local.WardoveDatabase
import com.app.wardove.data.local.dao.ClothingDao
import com.app.wardove.data.local.dao.LaundryDao
import com.app.wardove.data.local.dao.WearLogDao
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
    fun provideWardoveDatabase(
        @ApplicationContext context: Context
    ): WardoveDatabase = Room.databaseBuilder(
        context,
        WardoveDatabase::class.java,
        WardoveDatabase.DATABASE_NAME
    )
        .addMigrations(WardoveDatabase.MIGRATION_1_2)
        .fallbackToDestructiveMigration(dropAllTables = true)
        .build()

    @Provides
    fun provideClothingDao(db: WardoveDatabase): ClothingDao = db.clothingDao()

    @Provides
    fun provideWearLogDao(db: WardoveDatabase): WearLogDao = db.wearLogDao()

    @Provides
    fun provideLaundryDao(db: WardoveDatabase): LaundryDao = db.laundryDao()
}

package com.app.wardove.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton

private val Context.settingsDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "wardove_settings"
)

private val Context.appLockDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "wardove_app_lock"
)

@Module
@InstallIn(SingletonComponent::class)
object SettingsModule {

    @Provides
    @Singleton
    fun provideSettingsDataStore(
        @ApplicationContext context: Context
    ): DataStore<Preferences> = context.settingsDataStore

    @Provides
    @Singleton
    @Named("appLockDataStore")
    fun provideAppLockDataStore(
        @ApplicationContext context: Context
    ): DataStore<Preferences> = context.appLockDataStore
}

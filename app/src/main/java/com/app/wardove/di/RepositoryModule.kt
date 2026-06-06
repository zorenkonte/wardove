package com.app.wardove.di

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    // Repositories use @Inject constructors with @Singleton — Hilt provides them directly.
    // This module exists as a wiring seam for future bindings (e.g. interface → impl).
}

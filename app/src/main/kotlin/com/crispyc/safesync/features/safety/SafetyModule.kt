package com.crispyc.safesync.features.safety

import com.crispyc.safesync.core.mesh.BleMeshManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SafetyModule {

    @Provides
    @Singleton
    fun provideSafetyManager(
        @dagger.hilt.android.qualifiers.ApplicationContext context: android.content.Context
    ): SafetyManager = SafetyManager(context)
}

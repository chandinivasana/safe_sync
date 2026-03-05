package com.crispyc.safesync.core.db

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import net.sqlcipher.database.SQLiteDatabase
import net.sqlcipher.database.SupportFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): SafeSyncDatabase {
        val passphrase = SQLiteDatabase.getBytes("your-secure-passphrase".toCharArray())
        val factory = SupportFactory(passphrase)
        
        return Room.databaseBuilder(
            context,
            SafeSyncDatabase::class.java,
            "safesync_db"
        )
        .openHelperFactory(factory)
        .fallbackToDestructiveMigration()
        .build()
    }

    @Provides
    fun provideUserProfileDao(database: SafeSyncDatabase) = database.userProfileDao()

    @Provides
    fun provideSafetyEventDao(database: SafeSyncDatabase) = database.safetyEventDao()

    @Provides
    fun provideMeshMessageDao(database: SafeSyncDatabase) = database.meshMessageDao()

    @Provides
    fun provideJournalDao(database: SafeSyncDatabase) = database.journalDao()
}

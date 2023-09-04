package ru.bedayev.voicerecorder.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ru.bedayev.voicerecorder.database.RecordDatabase
import ru.bedayev.voicerecorder.database.RecordDatabaseDao
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DBModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): RecordDatabase{
        return Room.databaseBuilder(
            context = context,
            klass = RecordDatabase::class.java,
            name = RecordDatabase.DB_NAME
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideDao(
        database: RecordDatabase
    ): RecordDatabaseDao = database.recordDao()
}
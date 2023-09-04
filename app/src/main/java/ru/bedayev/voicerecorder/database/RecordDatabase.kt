package ru.bedayev.voicerecorder.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [
        RecordingItem::class
    ],
    version = 1,
    exportSchema = false
)
abstract class RecordDatabase : RoomDatabase() {
    abstract fun recordDao(): RecordDatabaseDao

    companion object{
        const val DB_NAME = "voice_record_database"
    }
}
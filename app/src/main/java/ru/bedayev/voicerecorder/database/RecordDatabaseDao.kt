package ru.bedayev.voicerecorder.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface RecordDatabaseDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(record: RecordingItem)

    @Update
    suspend fun update(record: RecordingItem)

    @Query("SELECT * FROM recording_table WHERE id = :key")
    suspend fun getRecord(key: Long?): RecordingItem?

    @Query("DELETE FROM recording_table")
    suspend fun clearAll()

    @Query("DELETE FROM recording_table WHERE id = :key")
    suspend fun removeRecord(key: Long?)

    @Query("SELECT * FROM recording_table ORDER BY id DESC")
    fun getAllRecords(): Flow<List<RecordingItem>>

    @Query("SELECT COUNT(*) FROM recording_table")
    fun getCount(): Flow<Int>

}
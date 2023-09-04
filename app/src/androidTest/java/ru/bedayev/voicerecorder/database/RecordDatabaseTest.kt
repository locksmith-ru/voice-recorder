package ru.bedayev.voicerecorder.database

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class RecordDatabaseTest {

    private lateinit var recordDatabaseDao: RecordDatabaseDao
    private lateinit var recordDatabase: RecordDatabase

    @Before
    fun createDb(){
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        recordDatabase = Room
            .inMemoryDatabaseBuilder(context, RecordDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        recordDatabaseDao = recordDatabase.recordDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb(){
        recordDatabase.close()
    }

    @Test
    @Throws(Exception::class)
    fun testDataBase(){
        runTest {
            recordDatabaseDao.insert(RecordingItem())
            val getCount = recordDatabaseDao.getCount()
            assertEquals(1, getCount.first())
        }
    }

}
package ru.bedayev.voicerecorder.removedialog

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.bedayev.voicerecorder.R
import ru.bedayev.voicerecorder.database.RecordDatabaseDao
import timber.log.Timber
import java.io.File
import javax.inject.Inject

@HiltViewModel
class RemoveViewModel @Inject constructor(
    private val databaseDao: RecordDatabaseDao
) : ViewModel() {

    private val job = Job()
    private val handlerException = CoroutineExceptionHandler { _, e ->
        Timber.e("An error has occurred: ${e.message}", e)
    }
    private val uiScope = CoroutineScope(Dispatchers.Main + job + handlerException)

    fun removeItem(itemId: Long) {
        uiScope.launch {
            withContext(Dispatchers.IO) {
                kotlin.runCatching {
                    databaseDao.removeRecord(key = itemId)
                }
            }
        }
    }

    fun removeFile(filePath: String, context: Context) {
        val file = File(filePath)
        if (file.exists()) {
            file.delete()
            Toast.makeText(
                context,
                R.string.file_deleted_text,
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onCleared() {
        super.onCleared()
        job.cancel()
    }
}
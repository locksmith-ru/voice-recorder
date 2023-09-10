package ru.bedayev.voicerecorder.recordlist

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import ru.bedayev.voicerecorder.database.RecordDatabaseDao
import ru.bedayev.voicerecorder.database.RecordingItem
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ListRecordViewModel @Inject constructor(
    dataSource: RecordDatabaseDao
) : ViewModel() {
    private val exceptionHandler: CoroutineExceptionHandler = CoroutineExceptionHandler { _, e ->
        Timber.e("ListRecordViewModel error:${e.message}", e)
    }
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.IO + exceptionHandler)
    val records: Flow<List<RecordingItem>> = dataSource.getAllRecords()
        .stateIn(
            scope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )
}
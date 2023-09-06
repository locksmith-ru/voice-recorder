package ru.bedayev.voicerecorder.recordlist

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import ru.bedayev.voicerecorder.database.RecordDatabaseDao
import ru.bedayev.voicerecorder.database.RecordingItem
import javax.inject.Inject

@HiltViewModel
class ListRecordViewModel @Inject constructor(
    dataSource: RecordDatabaseDao
) : ViewModel() {

    val records: Flow<List<RecordingItem>> = dataSource.getAllRecords()
}
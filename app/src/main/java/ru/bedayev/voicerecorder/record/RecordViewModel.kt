package ru.bedayev.voicerecorder.record

import android.content.Context
import android.os.CountDownTimer
import android.os.SystemClock
import androidx.annotation.IntegerRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.bedayev.voicerecorder.R
import java.util.Locale
import java.util.concurrent.TimeUnit
import javax.inject.Inject

private const val TRIGGER_TIME = "TRIGGER_AT"
private const val SECOND: Long = 1_000L

@HiltViewModel
class RecordViewModel @Inject constructor(
    @ApplicationContext context: Context
): ViewModel() {

    private val prefs =
        context.getSharedPreferences("ru.bedayev.voicerecorder", Context.MODE_PRIVATE)

    private val _elapsedTime: MutableStateFlow<String> = MutableStateFlow("")
    val elapsedTime = _elapsedTime.asStateFlow()

    private val _imageResource: MutableStateFlow<Int> = MutableStateFlow(R.drawable.ic_mic_white_36)
    val imageResource = _imageResource.asStateFlow()

    private lateinit var timer: CountDownTimer

    init {
        createTimer()
    }

    private fun timeFormatter(time: Long): String{
        return String.format(
            Locale.getDefault(),
            "%02d:%02d:%02d",
            TimeUnit.MILLISECONDS.toHours(time)%60,
            TimeUnit.MILLISECONDS.toMinutes(time)%60,
            TimeUnit.MILLISECONDS.toSeconds(time)%60
        )
    }

    fun setImageResource(@IntegerRes res: Int){
        _imageResource.value = res
    }

    fun stopTimer(){
        if (this::timer.isInitialized){
            timer.cancel()
        }
        resetTimer()
    }

    fun startTimer(){
        val triggerTime = SystemClock.elapsedRealtime()
        viewModelScope.launch {
            saveTime(triggerTime)
            createTimer()
        }
    }

    private suspend fun loadTime(): Long =
        withContext(Dispatchers.IO){
        prefs.getLong(TRIGGER_TIME, 0L)
    }

    private suspend fun saveTime(triggerTime: Long) =
        withContext(Dispatchers.IO){
        prefs.edit().putLong(TRIGGER_TIME, triggerTime).apply()
    }

    fun resetTimer(){
        _elapsedTime.value = timeFormatter(0)
        viewModelScope.launch { saveTime(0) }
    }

    private fun createTimer(){
        viewModelScope.launch {
            val triggerTime = loadTime()
            timer = object : CountDownTimer(triggerTime, SECOND){
                override fun onTick(millisUntilFinished: Long) {
                    _elapsedTime.value =
                        timeFormatter(SystemClock.elapsedRealtime() - triggerTime)
                }

                override fun onFinish() {
                    resetTimer()
                }

            }
            timer.start()
        }
    }

}
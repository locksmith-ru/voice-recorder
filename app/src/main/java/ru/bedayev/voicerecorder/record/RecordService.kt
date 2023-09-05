package ru.bedayev.voicerecorder.record

import android.annotation.SuppressLint
import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.media.MediaRecorder
import android.os.Build
import android.os.IBinder
import android.widget.Toast
import androidx.core.app.NotificationCompat
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.bedayev.voicerecorder.MainActivity
import ru.bedayev.voicerecorder.R
import ru.bedayev.voicerecorder.database.RecordDatabaseDao
import ru.bedayev.voicerecorder.database.RecordingItem
import timber.log.Timber
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import javax.inject.Inject

@AndroidEntryPoint
class RecordService : Service() {

    @Inject lateinit var mDatabase: RecordDatabaseDao

    private var mFileName: String? = null
    private var mFilePath: String? = null
    private var mCountRecords: Int? = null

    private var mRecorder: MediaRecorder? = null

    private var mStartingTimeMillis: Long = 0
    private var mElapsedTimeMillis: Long = 0

    private val mJob = Job()
    private val mHandler = CoroutineExceptionHandler { _, e ->
        Timber.e("an error has occurred: ${e.message}", e)
    }
    private val mUiScope = CoroutineScope(Dispatchers.Main + mJob + mHandler)

    private var isRunning: Boolean = false

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        /*isRunning = true
        val broadCastIntent = Intent()
        broadCastIntent.action = "ru.bedayev.voicerecorder.record.RecordService.ACTION_VOICE_RECORD"
        broadCastIntent.putExtra("isRunning", isRunning)
        LocalBroadcastManager.getInstance(this).sendBroadcast(broadCastIntent)*/

        mCountRecords = intent?.extras?.getInt("COUNT")
        startRecording()
        Timber.d("onStartCommand")
        return START_NOT_STICKY
    }

    private fun startRecording(){
        setFileNameAndPath()
        @Suppress("DEPRECATION")
        mRecorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            MediaRecorder(this)
        }else MediaRecorder()
        mRecorder?.setAudioSource(MediaRecorder.AudioSource.MIC)
        mRecorder?.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
        mRecorder?.setOutputFile(mFilePath)
        mRecorder?.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
        mRecorder?.setAudioChannels(1)
        mRecorder?.setAudioEncodingBitRate(192000)

        try {
            mRecorder?.prepare()
            mRecorder?.start()
            mStartingTimeMillis = System.currentTimeMillis()
            startForeground(1, createNotification())
        } catch (e: IOException){
            Timber.e("prepare failed:${e.message}", e)
        }
    }

    private fun createNotification(): Notification{
        val mBuilder: NotificationCompat.Builder =
            NotificationCompat.Builder(applicationContext,
                getString(R.string.notification_channel_id))
                .setSmallIcon(R.drawable.ic_mic_white_36)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(getString(R.string.notification_recording))
                .setOngoing(true)

        val flag = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S){
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        }else { 0 }

        mBuilder.setContentIntent(
            PendingIntent.getActivities(
                applicationContext, 0, arrayOf(
                    Intent(
                        applicationContext,
                        MainActivity::class.java
                    )
                ), flag
            )
        )

        return mBuilder.build()
    }

    @SuppressLint("SimpleDateFormat")
    private fun setFileNameAndPath(){
        var count = 0
        var f: File
        val suffix = ".mp4"
        val dateTime = SimpleDateFormat("yyyy_MM_dd_HH_mm_ss")
            .format(System.currentTimeMillis())
        do {
            mFileName = (getString(R.string.default_file_name) + "_" + dateTime + count + suffix)
            mFilePath = application.getExternalFilesDir(null)?.absolutePath
            mFilePath += "/$mFileName"

            count++
            f = File(mFilePath)
        }while (f.exists() && !f.isDirectory)
    }

    private fun stopRecording(){
        val recordingItem: RecordingItem = RecordingItem()
        mRecorder?.stop()
        mElapsedTimeMillis = System.currentTimeMillis() - mStartingTimeMillis
        mRecorder?.release()
        Toast.makeText(this,
            getString(R.string.toast_recording_finish),
            Toast.LENGTH_SHORT
        ).show()

        recordingItem.name = mFileName.toString()
        recordingItem.filePath = mFilePath.toString()
        recordingItem.length = mElapsedTimeMillis
        recordingItem.time = System.currentTimeMillis()

        mRecorder = null

        try {
            mUiScope.launch {
                withContext(Dispatchers.IO){
                    mDatabase.insert(record = recordingItem)
                    Timber.d("save record to database, file path=${recordingItem.filePath}")
                }
            }
        }catch (e: Exception){
            Timber.e("an error has occurred: ${e.message}", e)
        }
    }

    override fun onDestroy() {

        /*isRunning = false
        val broadCastIntent = Intent()
        broadCastIntent.action = "ru.bedayev.voicerecorder.record.RecordService.ACTION_VOICE_RECORD"
        broadCastIntent.putExtra("isRunning", isRunning)
        LocalBroadcastManager.getInstance(this).sendBroadcast(broadCastIntent)*/

        if (mRecorder != null)
            stopRecording()
        super.onDestroy()
    }

}
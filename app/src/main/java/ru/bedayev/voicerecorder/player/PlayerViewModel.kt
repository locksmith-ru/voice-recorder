package ru.bedayev.voicerecorder.player

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.media3.common.AudioAttributes
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.common.util.Util
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

private const val MIN_SDK_INT = 23

@UnstableApi
class PlayerViewModel(
    itemPath: String,
    application: Application
) : AndroidViewModel(application), DefaultLifecycleObserver {

    private val _playerFlow = MutableStateFlow<Player?>(null)
    val playerFlow = _playerFlow.asStateFlow()

    private var contentPosition: Long = 0L
    private var playWhenReady = true

    var itemPath: String? = itemPath

    init {
        setObserver()
    }

    private fun setObserver() {
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
    }

    private fun removeObserver(){
        ProcessLifecycleOwner.get().lifecycle.removeObserver(this)
    }

    override fun onStart(owner: LifecycleOwner) {
        super.onStart(owner)
        if (Util.SDK_INT > MIN_SDK_INT)
            setUpPlayer()
    }

    override fun onResume(owner: LifecycleOwner) {
        super.onResume(owner)
        if (Util.SDK_INT <= MIN_SDK_INT || _playerFlow.value == null)
            setUpPlayer()
    }

    override fun onPause(owner: LifecycleOwner) {
        super.onPause(owner)
        if (Util.SDK_INT <= MIN_SDK_INT)
            releaseExoPlayer()
    }

    override fun onStop(owner: LifecycleOwner) {
        super.onStop(owner)
        if (Util.SDK_INT > MIN_SDK_INT)
            releaseExoPlayer()
    }

    private fun setUpPlayer() {
        val dataSourceFactory: DefaultDataSource.Factory =
            DefaultDataSource.Factory(getApplication())

        val mediaSource = ProgressiveMediaSource.Factory(dataSourceFactory)
            .createMediaSource(MediaItem.fromUri(Uri.parse(itemPath)))

        val player = ExoPlayer.Builder(getApplication())
            .setAudioAttributes(AudioAttributes.DEFAULT, /* handleAudioFocus= */ true)
            .build()
        player.setMediaSource(mediaSource)
        player.playWhenReady = playWhenReady
        player.seekTo(contentPosition)
        player.prepare()

        this._playerFlow.value = player
    }

    private fun releaseExoPlayer() {
        val player = _playerFlow.value ?: return
        this._playerFlow.value = null
        contentPosition = player.contentPosition
        playWhenReady = player.playWhenReady
        player.release()
    }

    override fun onCleared() {
        super.onCleared()
        releaseExoPlayer()
        removeObserver()
    }

}
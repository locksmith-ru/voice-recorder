package ru.bedayev.voicerecorder.player

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.media3.common.util.UnstableApi

class PlayerViewModelFactory(
    private val mediaPath: String,
    private val application: Application
) : ViewModelProvider.Factory{
    @Suppress("UNCHECKED_CAST")
    @UnstableApi
    override fun <T : ViewModel>  create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PlayerViewModel::class.java))
            return PlayerViewModel(itemPath = mediaPath, application = application) as T
        throw IllegalArgumentException("unknown ViewModel class")
    }
}
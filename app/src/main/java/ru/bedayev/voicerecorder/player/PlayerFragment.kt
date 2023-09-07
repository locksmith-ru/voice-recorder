package ru.bedayev.voicerecorder.player

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.media3.common.util.UnstableApi
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import ru.bedayev.voicerecorder.databinding.PlayerFragmentBinding

@AndroidEntryPoint
class PlayerFragment : DialogFragment() {

    private var _binding: PlayerFragmentBinding? = null
    private val binding
        get() = _binding!!

    private lateinit var viewModel: PlayerViewModel

    private var itemPath: String? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        itemPath = arguments?.getString(ARG_ITEM_PATH)
        return super.onCreateDialog(savedInstanceState)
    }

    @UnstableApi
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = PlayerFragmentBinding.inflate(layoutInflater)
        val application = requireNotNull(this.activity).application
        itemPath?.let { path ->
            viewModel = ViewModelProvider(
                owner = this,
                factory = PlayerViewModelFactory(mediaPath = path, application = application)
            )[PlayerViewModel::class.java]
        } ?: error("Media path not initialized!!!")
        return binding.root
    }

    @UnstableApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.itemPath = itemPath
        binding.playerView.showTimeoutMs = 0
        lifecycleScope.launch {
            viewModel.playerFlow.flowWithLifecycle(
                lifecycle = lifecycle, minActiveState = Lifecycle.State.CREATED
            ).collect { player ->
                player?.let {
                    binding.playerView.player = it
                }
            }
        }
    }

    @UnstableApi
    override fun onDestroyView() {
        super.onDestroyView()
        binding.playerView.player = null
        _binding = null
    }

    companion object {
        private const val ARG_ITEM_PATH = "recording_item_path"

        @JvmStatic
        fun newInstance(itemPath: String?): PlayerFragment =
            PlayerFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_ITEM_PATH, itemPath)
                }
            }
    }
}
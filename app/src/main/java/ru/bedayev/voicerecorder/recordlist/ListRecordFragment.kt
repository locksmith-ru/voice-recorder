package ru.bedayev.voicerecorder.recordlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import ru.bedayev.voicerecorder.databinding.FragmentListRecordBinding
import ru.bedayev.voicerecorder.player.PlayerFragment
import ru.bedayev.voicerecorder.removedialog.RemoveDialogFragment

@AndroidEntryPoint
class ListRecordFragment : Fragment() {

    private var _binding: FragmentListRecordBinding? = null
    private val binding
        get() = _binding!!

    private val viewModel by viewModels<ListRecordViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentListRecordBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.listRecordViewModel = viewModel
        val adapter = ListRecordAdapter(
            { playRecord(filePath = it) },
            { id, path -> removeRecord(id = id, filePath = path) }
        )
        binding.recyclerView.adapter = adapter
        lifecycleScope.launch {
            viewModel.records.flowWithLifecycle(
                lifecycle = lifecycle, minActiveState = Lifecycle.State.CREATED
            ).collect { recordList ->
                adapter.data = recordList
            }
        }
        binding.lifecycleOwner = this
    }

    private fun playRecord(filePath: String) {
        val playerFragment = PlayerFragment.newInstance(itemPath = filePath)
        val transaction = requireActivity()
            .supportFragmentManager
            .beginTransaction()
        playerFragment.show(transaction, "dialog_playback")
    }

    private fun removeRecord(id: Long, filePath: String?) {
        val removeFragment = RemoveDialogFragment.newInstance(itemId = id, itemPath = filePath)
        val transaction: FragmentTransaction = requireActivity()
            .supportFragmentManager
            .beginTransaction()
        removeFragment.show(transaction, "dialog_remove")
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}
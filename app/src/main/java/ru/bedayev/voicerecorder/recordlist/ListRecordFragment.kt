package ru.bedayev.voicerecorder.recordlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import ru.bedayev.voicerecorder.databinding.FragmentListRecordBinding

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
        val adapter: ListRecordAdapter = ListRecordAdapter()
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

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}
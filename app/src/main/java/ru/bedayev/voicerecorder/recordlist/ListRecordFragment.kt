package ru.bedayev.voicerecorder.recordlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import dagger.hilt.android.AndroidEntryPoint
import ru.bedayev.voicerecorder.R

@AndroidEntryPoint
class ListRecordFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_list_record, container, false)
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            ListRecordFragment().apply {
                arguments = Bundle().apply {}
            }
    }
}
package ru.bedayev.voicerecorder.record

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import ru.bedayev.voicerecorder.R

class RecordFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_record, container, false)
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            RecordFragment().apply {
                arguments = Bundle().apply {}
            }
    }
}
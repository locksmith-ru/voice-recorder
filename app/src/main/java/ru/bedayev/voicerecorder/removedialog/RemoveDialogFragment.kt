package ru.bedayev.voicerecorder.removedialog

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import ru.bedayev.voicerecorder.R
import timber.log.Timber

@AndroidEntryPoint
class RemoveDialogFragment : DialogFragment() {

    private val viewModel by viewModels<RemoveViewModel>()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val itemPath = arguments?.getString(ARG_ITEM_PATH)
        val itemId = arguments?.getLong(ARG_ITEM_ID)
        return AlertDialog.Builder(activity)
            .setTitle(R.string.dialog_title_delete)
            .setMessage(R.string.dialog_text_delete)
            .setPositiveButton(R.string.dialog_action_yes) { dialog, which ->
                try {
                    itemId?.let { viewModel.removeItem(it) }
                    itemPath?.let { viewModel.removeFile(it, requireContext()) }
                } catch (e: java.lang.Exception) {
                    Timber.e("deleteFileDialog", "exception", e)
                }
                dialog.cancel()
            }
            .setNegativeButton(
                R.string.dialog_action_no
            ) { dialog, which ->
                dialog.cancel()
            }
            .create()
    }

    companion object {
        private const val ARG_ITEM_PATH = "recording_item_path"
        private const val ARG_ITEM_ID = "recording_item_id"

        @JvmStatic
        fun newInstance(itemId: Long, itemPath: String?) =
            RemoveDialogFragment().apply {
                arguments = Bundle().apply {
                    putLong(ARG_ITEM_ID, itemId)
                    putString(ARG_ITEM_PATH, itemPath)
                }
            }
    }
}
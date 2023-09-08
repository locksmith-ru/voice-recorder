package ru.bedayev.voicerecorder.recordlist

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.bedayev.voicerecorder.database.RecordingItem
import ru.bedayev.voicerecorder.databinding.ListItemRecordBinding
import ru.bedayev.voicerecorder.ui.ListRecordCard
import ru.bedayev.voicerecorder.ui.theme.VoiceRecorderTheme
import kotlin.text.*

class ListRecordAdapter(
    private val onItemClicked: (String) -> Unit,
    private val onRemoveItem: (Long, String?) -> Unit
) : RecyclerView.Adapter<ListRecordAdapter.ViewHolder>() {

    var data: List<RecordingItem> = emptyList()
        @SuppressLint("NotifyDataSetChanged")
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        return ViewHolder(
            ListItemRecordBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item: RecordingItem? = data.getOrNull(position)
        item?.let { record ->
            holder.binding.composeView.setContent {
                VoiceRecorderTheme {
                    ListRecordCard(
                        recordingItem = record,
                        onItemClick = onItemClicked,
                        onRemove = onRemoveItem
                    )
                }
            }
        }
    }

    override fun getItemCount(): Int = data.size

    class ViewHolder(
        val binding: ListItemRecordBinding
    ) : RecyclerView.ViewHolder(binding.root)
}
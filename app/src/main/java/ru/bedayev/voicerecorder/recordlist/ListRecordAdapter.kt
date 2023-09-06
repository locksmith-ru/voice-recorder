package ru.bedayev.voicerecorder.recordlist

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.bedayev.voicerecorder.database.RecordingItem
import ru.bedayev.voicerecorder.databinding.ListItemRecordBinding
import java.util.Locale
import java.util.concurrent.TimeUnit
import kotlin.text.*

class ListRecordAdapter : RecyclerView.Adapter<ListRecordAdapter.ViewHolder>() {

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
            val itemDuration: Long = record.length
            val minutes: Long = TimeUnit.MILLISECONDS.toMinutes(itemDuration)
            val seconds: Long = (TimeUnit.MILLISECONDS.toSeconds(itemDuration) -
                    TimeUnit.MINUTES.toSeconds(minutes))
            with(holder.binding) {
                fileNameText.text = record.name
                fileLengthText.text = String
                    .format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
            }
        }
    }

    override fun getItemCount(): Int = data.size

    class ViewHolder(
        val binding: ListItemRecordBinding
    ) : RecyclerView.ViewHolder(binding.root)
}
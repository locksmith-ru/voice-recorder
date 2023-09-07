package ru.bedayev.voicerecorder.recordlist

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import ru.bedayev.voicerecorder.R
import ru.bedayev.voicerecorder.database.RecordingItem
import ru.bedayev.voicerecorder.databinding.ListItemRecordBinding
import timber.log.Timber
import java.io.File
import java.util.Locale
import java.util.concurrent.TimeUnit
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
            val itemDuration: Long = record.length
            val minutes: Long = TimeUnit.MILLISECONDS.toMinutes(itemDuration)
            val seconds: Long = (TimeUnit.MILLISECONDS.toSeconds(itemDuration) -
                    TimeUnit.MINUTES.toSeconds(minutes))
            with(holder.binding) {
                fileNameText.text = record.name
                fileLengthText.text = String
                    .format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
                cardView.setOnClickListener {
                    val filePath = record.filePath
                    val file = File(filePath)
                    if (file.exists()) {
                        try {
                            onItemClicked(filePath)
                        } catch (e: Exception) {
                            Timber.e("onItemClick an error has occurred: ${e.message}", e)
                        }
                    } else {
                        Toast.makeText(
                            cardView.context,
                            R.string.file_is_not_exist_text,
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }
                }
                cardView.setOnLongClickListener {
                    onRemoveItem(record.id, record.filePath)
                    false
                }
                removeButton.setOnClickListener{
                    onRemoveItem(record.id, record.filePath)
                }
            }

        }
    }

    override fun getItemCount(): Int = data.size

    class ViewHolder(
        val binding: ListItemRecordBinding
    ) : RecyclerView.ViewHolder(binding.root)
}
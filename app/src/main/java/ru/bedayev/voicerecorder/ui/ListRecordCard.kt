package ru.bedayev.voicerecorder.ui

import android.content.Context
import android.content.res.Configuration
import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.bedayev.voicerecorder.R
import ru.bedayev.voicerecorder.database.RecordingItem
import ru.bedayev.voicerecorder.ui.theme.VoiceRecorderTheme
import timber.log.Timber
import java.io.File
import java.util.Locale
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalFoundationApi::class)
@Composable
inline fun ListRecordCard(
    recordingItem: RecordingItem,
    crossinline onItemClick: (String) -> Unit = {},
    crossinline onRemove: (Long, String?) -> Unit = { _, _ -> }
) {
    val context = LocalContext.current
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentSize()
            .padding(5.dp)
            .combinedClickable(
                onClick = { onClick(record = recordingItem, context = context, onItemClick) },
                onLongClick = { onRemove(recordingItem.id, recordingItem.filePath) }
            ),
        shape = MaterialTheme.shapes.small,
        elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_mic_white_36),
                contentDescription = "record item",
                modifier = Modifier
                    .wrapContentSize()
                    .padding(horizontal = 7.dp),
                colorFilter = ColorFilter.tint(colorResource(id = android.R.color.darker_gray))
            )
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = recordingItem.name,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.SansSerif
                )
                val itemDuration: Long = recordingItem.length
                val minutes: Long = TimeUnit.MILLISECONDS.toMinutes(itemDuration)
                val seconds: Long = (TimeUnit.MILLISECONDS.toSeconds(itemDuration) -
                        TimeUnit.MINUTES.toSeconds(minutes))
                Text(
                    text = String.format(
                        Locale.getDefault(),
                        "%02d:%02d", minutes, seconds
                    ),
                    modifier = Modifier.padding(top = 5.dp),
                    fontFamily = FontFamily.SansSerif,
                    fontSize = 12.sp
                )
            }
            IconButton(onClick = { onRemove(recordingItem.id, recordingItem.filePath) }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_delete_36),
                    contentDescription = "delete button",
                    tint = colorResource(id = android.R.color.darker_gray)
                )
            }
        }
    }
}

inline fun onClick(
    record: RecordingItem,
    context: Context,
    onClicked: (String) -> Unit
) {
    val filePath = record.filePath
    val file = File(filePath)
    if (file.exists()) {
        try {
            onClicked(filePath)
        } catch (e: Exception) {
            Timber.e("onItemClick an error has occurred: ${e.message}", e)
        }
    } else {
        Toast.makeText(
            context,
            R.string.file_is_not_exist_text,
            Toast.LENGTH_SHORT
        )
            .show()
    }
}

@Preview(
    showBackground = true, showSystemUi = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewListItemRecord() {
    val fakeRecordingItem = RecordingItem(
        id = 10, name = "VoiceRecord_2023.09.08",
        filePath = "sdcard/emulated/0/data/VoiceRecorder/VoiceRecord_2023.09.08.mp4",
        length = 197800, time = 204858
    )
    VoiceRecorderTheme {
        Surface {
            ListRecordCard(recordingItem = fakeRecordingItem)
        }
    }
}
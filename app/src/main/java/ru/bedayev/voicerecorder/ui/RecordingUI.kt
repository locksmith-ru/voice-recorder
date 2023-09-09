package ru.bedayev.voicerecorder.ui

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.bedayev.voicerecorder.R
import ru.bedayev.voicerecorder.record.RecordViewModel
import ru.bedayev.voicerecorder.ui.theme.VoiceRecorderTheme

@Composable
fun RecordingUI(viewModel: RecordViewModel) {
    val elapsedTime by viewModel.elapsedTime.collectAsState()
    RecordingUI(elapsedTime = elapsedTime)
}
@Composable
fun RecordingUI(elapsedTime: String) {
    Surface {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                modifier = Modifier.padding(bottom = 48.dp),
                text = elapsedTime,
                style = MaterialTheme.typography.headlineLarge,
                fontSize = 36.sp
            )
            FloatingActionButton(
                onClick = { /*TODO*/ },
                shape = MaterialTheme.shapes.medium,
                elevation = FloatingActionButtonDefaults.elevation(48.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_mic_white_36),
                    contentDescription = "start/stop button",
                    tint = colorResource(id = android.R.color.darker_gray)
                )
            }
        }
    }
}

@Preview(
    name = "Dark theme",
    showSystemUi = true,
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Preview(
    name = "Light theme",
    showSystemUi = true,
    showBackground = true
)
@Composable
fun PreviewRecordingUI() {
    VoiceRecorderTheme {
        Surface {
            RecordingUI(elapsedTime = "00:03:48")
        }
    }
}
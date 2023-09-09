package ru.bedayev.voicerecorder.record

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import ru.bedayev.voicerecorder.MainActivity
import ru.bedayev.voicerecorder.R
import ru.bedayev.voicerecorder.databinding.FragmentRecordBinding
import java.io.File

@AndroidEntryPoint
class RecordFragment : Fragment() {

    private var _binding: FragmentRecordBinding? = null
    private val binding
        get() = _binding!!

    private var mainActivity: MainActivity? = null

    private val viewModel by viewModels<RecordViewModel>()

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { map ->
        // если разрешения присутствуют и все из них выданы пользователем
        if (map.values.isNotEmpty() && map.values.all { it }) {
            // запуск каких-то действий
            onRecord(true)
            viewModel.startTimer()
        } else {
            // в противном случае запуск других действий
            Toast.makeText(requireContext(),
                getString(R.string.toast_recording_permissions),
                Toast.LENGTH_SHORT)
                .show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentRecordBinding.inflate(layoutInflater)
        mainActivity = requireActivity() as MainActivity

        createChannel(
            channelId = getString(R.string.notification_channel_id),
            channelName = getString(R.string.notification_channel_name)
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recordViewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        mainActivity?.let {
            if (!it.isServiceRunning(RecordService::class.java)) {
                viewModel.resetTimer()
            } else {
                binding.playButton.setImageResource(R.drawable.ic_stop_36)
            }
        }

        binding.playButton.setOnClickListener {
            // check permissions
            if (isPermissionsGranted()){
                // если сервис запущен
                if (mainActivity?.isServiceRunning(RecordService::class.java) == true){
                    // останавливаем запись
                    onRecord( start = false)
                    // тогда останавливаем таймер
                    viewModel.stopTimer()
                } else{
                    onRecord(start = true)
                    viewModel.startTimer()
                }
            } else {
                // start launcher
                permissionLauncher.launch(REQUEST_PERMISSIONS)
            }
        }
    }

    private fun onRecord(start: Boolean){
        val intent : Intent = Intent(activity, RecordService::class.java)
        if (start){
            binding.playButton.setImageResource(R.drawable.ic_stop_36)
            Toast.makeText(requireContext(),
                R.string.toast_recording_start,
                Toast.LENGTH_SHORT)
                .show()
            val folder =
                File(activity?.getExternalFilesDir(null)?.absolutePath.toString()
                        + "/VoiceRecorder")
            if (!folder.exists()) folder.mkdir()
            requireActivity().startService(intent)
            // флаг для активити держать экран активным
            requireActivity().window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }else {
            binding.playButton.setImageResource(R.drawable.ic_mic_white_36)
            requireActivity().stopService(intent)
            requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
    }

    private fun createChannel(channelId: String, channelName: String){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val notificationChannel = NotificationChannel(
                channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                setShowBadge(false)
                setSound(null, null)
            }
            val notificationManager =
                requireActivity().getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

    /**
     * Проверка выданы ли разрешения
     * @return true - если все разрешения выданы / false - если хотя бы одно разрешение не выдано
     */
    private fun isPermissionsGranted(): Boolean {
        return REQUEST_PERMISSIONS.all { permission ->
            ContextCompat.checkSelfPermission(requireContext(), permission) ==
                    PackageManager.PERMISSION_GRANTED
        }
    }

    override fun onDestroyView() {
        _binding = null
        mainActivity = null
        super.onDestroyView()
    }

    companion object {
        private val REQUEST_PERMISSIONS: Array<String> = buildList {
            add(Manifest.permission.RECORD_AUDIO)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                add(Manifest.permission.FOREGROUND_SERVICE)
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                add(Manifest.permission.READ_MEDIA_AUDIO)
                add(Manifest.permission.POST_NOTIFICATIONS)
            }
        }.toTypedArray()
    }
}
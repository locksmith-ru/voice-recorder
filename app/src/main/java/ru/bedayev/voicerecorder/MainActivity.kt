package ru.bedayev.voicerecorder

import android.app.ActivityManager
import android.content.Context
import android.content.IntentFilter
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment_container) as NavHostFragment
        val navController = navHostFragment.navController
        navView.setupWithNavController(navController)
    }

    @Suppress("DEPRECATION")
    fun <T> isServiceRunning(serviceClass: Class<T>): Boolean{
        val manager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (service in manager.getRunningServices(Int.MAX_VALUE)){
            Timber.d("Service name = ${service.service.className}")
            if (serviceClass.name.equals(service.service.className))
                return true
        }
        return false
    }
}
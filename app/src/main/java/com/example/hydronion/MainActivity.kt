package com.example.hydronion

import SensorResponse
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.hydronion.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.example.hydronion.MonitoringFragment
import com.example.hydronion.ControlFragment
import com.example.hydronion.LogFragment
class MainActivity : AppCompatActivity() {

    // KOREKSI SINTAKS: Deklarasi yang benar
    private lateinit var binding: ActivityMainBinding
    private val monitoringFragment = MonitoringFragment()
    private val controlFragment = ControlFragment()
    private val logFragment = LogFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (savedInstanceState == null) {
            replaceFragment(monitoringFragment)
        }

        binding.bottomNavigation.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_monitoring -> {
                    replaceFragment(monitoringFragment)
                    true
                }
                R.id.nav_control -> {
                    replaceFragment(controlFragment)
                    true
                }
                R.id.nav_log -> {
                    replaceFragment(logFragment)
                    true
                }
                else -> false
            }
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(binding.navHostFragment.id, fragment)
            .commit()
    }
}
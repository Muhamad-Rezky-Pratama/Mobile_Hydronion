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

    private lateinit var binding: ActivityMainBinding

    // --- ROLE SIMULATION ---
    // Ubah true untuk Admin (2 Menu), false untuk Client (4 Menu)
    private val isAdmin = true

    // Fragment Client
    private val monitoringFragment = MonitoringFragment()
    private val accountFragment = AccountFragment()
    private val controlFragment = ControlFragment()
    private val logFragment = LogFragment()

    // Fragment Admin
    private val adminMonitoringFragment = AdminMonitoringFragment()
    private val adminAccountFragment = AdminAccountFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupNavigationByRole()

        binding.bottomNavigation.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_monitoring -> {
                    replaceFragment(if (isAdmin) adminMonitoringFragment else monitoringFragment)
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
                R.id.nav_account -> {
                    replaceFragment(if (isAdmin) adminAccountFragment else accountFragment)
                    true
                }
                else -> false
            }
        }
    }

    private fun setupNavigationByRole() {
        if (isAdmin) {
            // 1. Tampilkan Dashboard Admin pertama kali
            replaceFragment(adminMonitoringFragment)

            // 2. SEMBUNYIKAN MENU YANG TIDAK ADA DI ADMIN
            binding.bottomNavigation.menu.findItem(R.id.nav_control).isVisible = false
            binding.bottomNavigation.menu.findItem(R.id.nav_log).isVisible = false
        } else {
            // Tampilkan Dashboard Client pertama kali
            replaceFragment(monitoringFragment)

            // Pastikan semua menu terlihat untuk Client
            binding.bottomNavigation.menu.findItem(R.id.nav_control).isVisible = true
            binding.bottomNavigation.menu.findItem(R.id.nav_log).isVisible = true
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(binding.navHostFragment.id, fragment)
            .commit()
    }
}
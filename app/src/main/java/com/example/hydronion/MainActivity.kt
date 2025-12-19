package com.example.hydronion

import SensorResponse
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.hydronion.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.example.hydronion.MonitoringFragment
import com.example.hydronion.ControlFragment
import com.example.hydronion.LogFragment

class MainActivity : AppCompatActivity() {

    // KOREKSI SINTAKS: Deklarasi yang benar
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment

        navController = navHostFragment.navController

        // Awal → login
        navController.setGraph(R.navigation.nav_auth)

    }

    fun switchToUser() {
        navController.setGraph(R.navigation.nav_user)
        setupUserBottomNav()
    }

    fun switchToAdmin() {
        navController.setGraph(R.navigation.nav_admin)
        setupAdminBottomNav()
    }

    private fun setupUserBottomNav() {
        binding.bottomNavigation.visibility = View.VISIBLE
        binding.bottomNavAdmin.visibility = View.GONE

        binding.bottomNavigation.setupWithNavController(navController)
    }

    private fun setupAdminBottomNav() {
        binding.bottomNavAdmin.visibility = View.VISIBLE
        binding.bottomNavigation.visibility = View.GONE

        binding.bottomNavAdmin.setupWithNavController(navController)
    }

    fun logout() {
        binding.bottomNavigation.visibility = View.GONE
        binding.bottomNavAdmin.visibility = View.GONE
        navController.setGraph(R.navigation.nav_auth)
    }

}
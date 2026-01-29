package com.thundercode.learning.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.badge.BadgeDrawable
import com.thundercode.learning.R
import com.thundercode.learning.data.api.RetrofitClient
import com.thundercode.learning.data.local.PrefsManager
import com.thundercode.learning.databinding.ActivityMainBinding
import com.thundercode.learning.ui.auth.LoginActivity
import com.thundercode.learning.ui.videos.MyVideosFragment
import com.thundercode.learning.ui.videos.VideosFragment
import com.thundercode.learning.ui.profile.ProfileFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var prefsManager: PrefsManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        prefsManager = PrefsManager(this)
        val user = prefsManager.getUser()

        if (user == null) {
            navigateToLogin()
            return
        }

        // Set token for API calls
        prefsManager.getToken()?.let {
            RetrofitClient.setToken(it)
        }

        setSupportActionBar(binding.toolbar)
        setupBottomNavigation(user.role)

        // Load default fragment
        if (savedInstanceState == null) {
            loadFragment(VideosFragment())
        }
    }

    private fun setupBottomNavigation(role: String) {
        binding.bottomNavigation.menu.clear()

        // All users see Videos
        binding.bottomNavigation.menu.add(0, R.id.nav_videos, 0, "Videos")
            .setIcon(R.drawable.ic_videos)

        // Content Creators and Admins see My Videos
        if (role == "content_creator" || role == "admin") {
            binding.bottomNavigation.menu.add(0, R.id.nav_my_videos, 1, "My Videos")
                .setIcon(R.drawable.ic_my_videos)
        }

        // Admins see Dashboard
        if (role == "admin") {
            binding.bottomNavigation.menu.add(0, R.id.nav_dashboard, 2, "Dashboard")
                .setIcon(R.drawable.ic_dashboard)
        }

        // All users see Profile
        binding.bottomNavigation.menu.add(0, R.id.nav_profile, 3, "Profile")
            .setIcon(R.drawable.ic_profile)

        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_videos -> {
                    loadFragment(VideosFragment())
                    true
                }
                R.id.nav_my_videos -> {
                    loadFragment(VideosFragment())
                    true
                }
                R.id.nav_dashboard -> {
                    // TODO: Create AdminDashboardFragment
                    Toast.makeText(this, "Dashboard coming soon", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.nav_profile -> {
                    loadFragment(ProfileFragment())
                    true
                }
                else -> false
            }
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_logout -> {
                showLogoutDialog()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showLogoutDialog() {
        AlertDialog.Builder(this)
            .setTitle("Logout")
            .setMessage("Are you sure you want to logout?")
            .setPositiveButton("Logout") { _, _ ->
                logout()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun logout() {
        prefsManager.clear()
        RetrofitClient.setToken(null)
        navigateToLogin()
    }

    private fun navigateToLogin() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }
}

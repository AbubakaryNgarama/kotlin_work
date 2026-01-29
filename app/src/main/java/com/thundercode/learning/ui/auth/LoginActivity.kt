package com.thundercode.learning.ui.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.thundercode.learning.data.api.RetrofitClient
import com.thundercode.learning.data.local.PrefsManager
import com.thundercode.learning.data.repository.AuthRepository
import com.thundercode.learning.databinding.ActivityLoginBinding
import com.thundercode.learning.ui.main.MainActivity

class LoginActivity : AppCompatActivity() {

 private lateinit var binding: ActivityLoginBinding
 private lateinit var viewModel: LoginViewModel
 private lateinit var prefsManager: PrefsManager

 override fun onCreate(savedInstanceState: Bundle?) {
  super.onCreate(savedInstanceState)
  binding = ActivityLoginBinding.inflate(layoutInflater)
  setContentView(binding.root)

  prefsManager = PrefsManager(this)

  // Check if already logged in
  if (prefsManager.isLoggedIn()) {
   navigateToMain()
   return
  }

  val repository = AuthRepository(RetrofitClient.apiService)
  viewModel = LoginViewModel(repository)

  setupObservers()
  setupClickListeners()
 }

 private fun setupObservers() {
  viewModel.loginResult.observe(this) { result ->
   result.onSuccess { response ->
    if (response.success && response.data != null) {
     prefsManager.saveToken(response.data.token)
     prefsManager.saveUser(response.data.user)
     RetrofitClient.setToken(response.data.token)

     Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show()
     navigateToMain()
    } else {
     Toast.makeText(this, response.message ?: "Login failed", Toast.LENGTH_SHORT).show()
    }
   }.onFailure { error ->
    Toast.makeText(this, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
   }
  }

  viewModel.isLoading.observe(this) { isLoading ->
   binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
   binding.btnLogin.isEnabled = !isLoading
  }
 }

 private fun setupClickListeners() {
  binding.btnLogin.setOnClickListener {
   val email = binding.etEmail.text.toString().trim()
   val password = binding.etPassword.text.toString()

   if (validateInput(email, password)) {
    viewModel.login(email, password)
   }
  }

  binding.tvRegister.setOnClickListener {
   startActivity(Intent(this, RegisterActivity::class.java))
  }
 }

 private fun validateInput(email: String, password: String): Boolean {
  if (email.isEmpty()) {
   binding.etEmail.error = "Email is required"
   return false
  }
  if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
   binding.etEmail.error = "Invalid email"
   return false
  }
  if (password.isEmpty()) {
   binding.etPassword.error = "Password is required"
   return false
  }
  return true
 }

 private fun navigateToMain() {
  startActivity(Intent(this, MainActivity::class.java))
  finish()
 }
}
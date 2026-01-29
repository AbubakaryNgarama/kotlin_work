package com.thundercode.learning.ui.auth

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.thundercode.learning.data.api.RetrofitClient
import com.thundercode.learning.data.repository.AuthRepository
import com.thundercode.learning.databinding.ActivityRegisterBinding
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var repository: AuthRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        repository = AuthRepository(RetrofitClient.apiService)

        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.btnRegister.setOnClickListener {
            val name = binding.etName.text.toString().trim()
            val email = binding.etEmail.text.toString().trim()
            val phone = binding.etPhone.text.toString().trim()
            val password = binding.etPassword.text.toString()

            if (validateInput(name, email, password)) {
                register(name, email, password, phone.ifEmpty { null })
            }
        }

        binding.tvLogin.setOnClickListener {
            finish()
        }
    }

    private fun validateInput(name: String, email: String, password: String): Boolean {
        if (name.isEmpty()) {
            binding.etName.error = "Name is required"
            return false
        }
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
        if (password.length < 8) {
            binding.etPassword.error = "Password must be at least 8 characters"
            return false
        }
        return true
    }

    private fun register(name: String, email: String, password: String, phone: String?) {
        binding.progressBar.visibility = View.VISIBLE
        binding.btnRegister.isEnabled = false

        lifecycleScope.launch {
            repository.register(name, email, password, phone)
                .onSuccess { response ->
                    binding.progressBar.visibility = View.GONE
                    binding.btnRegister.isEnabled = true

                    if (response.success) {
                        Toast.makeText(
                            this@RegisterActivity,
                            "Registration successful! Please login.",
                            Toast.LENGTH_SHORT
                        ).show()
                        finish()
                    } else {
                        Toast.makeText(
                            this@RegisterActivity,
                            response.message ?: "Registration failed",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                .onFailure { error ->
                    binding.progressBar.visibility = View.GONE
                    binding.btnRegister.isEnabled = true
                    Toast.makeText(
                        this@RegisterActivity,
                        "Error: ${error.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
        }
    }
}
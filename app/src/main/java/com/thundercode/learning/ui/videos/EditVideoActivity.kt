package com.thundercode.learning.ui.videos

import android.app.Activity
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.thundercode.learning.data.api.RetrofitClient
import com.thundercode.learning.data.models.Video
import com.thundercode.learning.data.repository.VideoRepository
import com.thundercode.learning.databinding.ActivityEditVideoBinding
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

class EditVideoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditVideoBinding
    private lateinit var repository: VideoRepository
    private lateinit var video: Video

    private var thumbnailUri: Uri? = null

    private val thumbnailPickerLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            thumbnailUri = it
            binding.ivThumbnail.setImageURI(it)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditVideoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        repository = VideoRepository(RetrofitClient.apiService)

        video = intent.getParcelableExtra("video") ?: run {
            finish()
            return
        }

        setupToolbar()
        setupSpinner()
        populateData()
        setupClickListeners()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Edit Video"
        binding.toolbar.setNavigationOnClickListener { finish() }
    }

    private fun setupSpinner() {
        val visibilityOptions = arrayOf("Public", "Private", "Unlisted")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, visibilityOptions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerVisibility.adapter = adapter
    }

    private fun populateData() {
        binding.etTitle.setText(video.title)
        binding.etDescription.setText(video.description ?: "")

        when (video.visibility) {
            "public" -> binding.spinnerVisibility.setSelection(0)
            "private" -> binding.spinnerVisibility.setSelection(1)
            "unlisted" -> binding.spinnerVisibility.setSelection(2)
        }
    }

    private fun setupClickListeners() {
        binding.containerThumbnail.setOnClickListener {
            thumbnailPickerLauncher.launch("image/*")
        }

        binding.btnUpload.setOnClickListener {
            updateVideo()
        }
    }

    private fun updateVideo() {
        val title = binding.etTitle.text.toString().trim()
        val description = binding.etDescription.text.toString().trim()
        val visibility = when (binding.spinnerVisibility.selectedItemPosition) {
            0 -> "public"
            1 -> "private"
            2 -> "unlisted"
            else -> "public"
        }

        if (title.isEmpty()) {
            binding.etTitle.error = "Title is required"
            return
        }

        binding.progressBar.visibility = View.VISIBLE
        binding.btnUpload.isEnabled = false

        lifecycleScope.launch {
            try {
                val thumbnailFile = thumbnailUri?.let { uriToFile(it) }

                repository.updateVideo(
                    videoId = video.id,
                    title = title,
                    description = description.ifEmpty { null },
                    thumbnailFile = thumbnailFile,
                    visibility = visibility
                ).onSuccess {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(this@EditVideoActivity, "Video updated successfully!", Toast.LENGTH_SHORT).show()
                    setResult(RESULT_OK)
                    finish()
                }.onFailure { error ->
                    binding.progressBar.visibility = View.GONE
                    binding.btnUpload.isEnabled = true
                    Toast.makeText(this@EditVideoActivity, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                binding.progressBar.visibility = View.GONE
                binding.btnUpload.isEnabled = true
                Toast.makeText(this@EditVideoActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun uriToFile(uri: Uri): File {
        val inputStream = contentResolver.openInputStream(uri)
        val file = File(cacheDir, "thumbnail_${System.currentTimeMillis()}")
        val outputStream = FileOutputStream(file)

        inputStream?.use { input ->
            outputStream.use { output ->
                input.copyTo(output)
            }
        }

        return file
    }
}
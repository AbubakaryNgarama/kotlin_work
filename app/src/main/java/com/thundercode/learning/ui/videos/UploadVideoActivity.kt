package com.thundercode.learning.ui.videos

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.thundercode.learning.R
import com.thundercode.learning.data.api.RetrofitClient
import com.thundercode.learning.data.repository.VideoRepository
import com.thundercode.learning.databinding.ActivityUploadVideoBinding
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

class UploadVideoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUploadVideoBinding
    private lateinit var repository: VideoRepository

    private var videoUri: Uri? = null
    private var thumbnailUri: Uri? = null

    private val videoPickerLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            videoUri = it
            binding.tvVideoSelected.text = "Video selected: ${getFileName(it)}"
            binding.tvVideoSelected.visibility = View.VISIBLE
        }
    }

    private val thumbnailPickerLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            thumbnailUri = it
            binding.ivThumbnail.setImageURI(it)
            binding.tvSelectThumbnail.visibility = View.GONE
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUploadVideoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        repository = VideoRepository(RetrofitClient.apiService)

        setupToolbar()
        setupSpinner()
        setupClickListeners()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Upload Video"
        binding.toolbar.setNavigationOnClickListener { finish() }
    }

    private fun setupSpinner() {
        val visibilityOptions = arrayOf("Public", "Private", "Unlisted")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, visibilityOptions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerVisibility.adapter = adapter
    }

    private fun setupClickListeners() {
        binding.btnSelectVideo.setOnClickListener {
            videoPickerLauncher.launch("video/*")
        }

        binding.containerThumbnail.setOnClickListener {
            thumbnailPickerLauncher.launch("image/*")
        }

        binding.btnUpload.setOnClickListener {
            uploadVideo()
        }
    }

    private fun uploadVideo() {
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

        if (videoUri == null) {
            Toast.makeText(this, "Please select a video", Toast.LENGTH_SHORT).show()
            return
        }

        binding.progressBar.visibility = View.VISIBLE
        binding.btnUpload.isEnabled = false

        lifecycleScope.launch {
            try {
                val videoFile = uriToFile(videoUri!!, "video")
                val thumbnailFile = thumbnailUri?.let { uriToFile(it, "thumbnail") }

                repository.uploadVideo(
                    title = title,
                    description = description.ifEmpty { null },
                    videoFile = videoFile,
                    thumbnailFile = thumbnailFile,
                    visibility = visibility
                ).onSuccess {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(this@UploadVideoActivity, "Video uploaded successfully!", Toast.LENGTH_SHORT).show()
                    setResult(Activity.RESULT_OK)
                    finish()
                }.onFailure { error ->
                    binding.progressBar.visibility = View.GONE
                    binding.btnUpload.isEnabled = true
                    Toast.makeText(this@UploadVideoActivity, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                binding.progressBar.visibility = View.GONE
                binding.btnUpload.isEnabled = true
                Toast.makeText(this@UploadVideoActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun uriToFile(uri: Uri, prefix: String): File {
        val inputStream = contentResolver.openInputStream(uri)
        val file = File(cacheDir, "${prefix}_${System.currentTimeMillis()}")
        val outputStream = FileOutputStream(file)

        inputStream?.use { input ->
            outputStream.use { output ->
                input.copyTo(output)
            }
        }

        return file
    }

    private fun getFileName(uri: Uri): String {
        var result = ""
        val cursor = contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val nameIndex = it.getColumnIndex(MediaStore.Video.Media.DISPLAY_NAME)
                if (nameIndex != -1) {
                    result = it.getString(nameIndex)
                }
            }
        }
        return result.ifEmpty { "video_${System.currentTimeMillis()}" }
    }
}
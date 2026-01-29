package com.thundercode.learning.data.repository

import com.thundercode.learning.data.api.ApiService
import com.thundercode.learning.data.models.Video
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class VideoRepository(private val apiService: ApiService) {

    suspend fun getVideos(search: String? = null): Result<List<Video>> {
        return try {
            val response = apiService.getVideos(search)
            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(response.body()!!.data)
            } else {
                Result.failure(Exception("Failed to load videos"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getMyVideos(search: String? = null): Result<List<Video>> {
        return try {
            val response = apiService.getMyVideos(search)
            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(response.body()!!.data)
            } else {
                Result.failure(Exception("Failed to load your videos"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun uploadVideo(
        title: String,
        description: String?,
        videoFile: File,
        thumbnailFile: File?,
        visibility: String
    ): Result<Video> {
        return try {
            val titleBody = title.toRequestBody("text/plain".toMediaTypeOrNull())
            val descBody = description?.toRequestBody("text/plain".toMediaTypeOrNull())
            val visibilityBody = visibility.toRequestBody("text/plain".toMediaTypeOrNull())

            val videoBody = videoFile.asRequestBody("video/*".toMediaTypeOrNull())
            val videoPart = MultipartBody.Part.createFormData("video", videoFile.name, videoBody)

            val thumbnailPart = thumbnailFile?.let {
                val thumbBody = it.asRequestBody("image/*".toMediaTypeOrNull())
                MultipartBody.Part.createFormData("thumbnail", it.name, thumbBody)
            }

            val response = apiService.uploadVideo(
                titleBody, descBody, visibilityBody, videoPart, thumbnailPart
            )

            if (response.isSuccessful && response.body()?.data != null) {
                Result.success(response.body()!!.data!!)
            } else {
                Result.failure(Exception("Upload failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateVideo(
        videoId: Int,
        title: String,
        description: String?,
        thumbnailFile: File?,
        visibility: String?
    ): Result<Video> {
        return try {
            val methodBody = "PUT".toRequestBody("text/plain".toMediaTypeOrNull())
            val titleBody = title.toRequestBody("text/plain".toMediaTypeOrNull())
            val descBody = description?.toRequestBody("text/plain".toMediaTypeOrNull())
            val visibilityBody = visibility?.toRequestBody("text/plain".toMediaTypeOrNull())

            val thumbnailPart = thumbnailFile?.let {
                val thumbBody = it.asRequestBody("image/*".toMediaTypeOrNull())
                MultipartBody.Part.createFormData("thumbnail", it.name, thumbBody)
            }

            val response = apiService.updateVideo(
                videoId, methodBody, titleBody, descBody, visibilityBody, thumbnailPart
            )

            if (response.isSuccessful && response.body()?.data != null) {
                Result.success(response.body()!!.data!!)
            } else {
                Result.failure(Exception("Update failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteVideo(videoId: Int): Result<Unit> {
        return try {
            val response = apiService.deleteVideo(videoId)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Delete failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
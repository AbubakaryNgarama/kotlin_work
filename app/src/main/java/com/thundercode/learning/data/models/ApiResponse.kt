package com.thundercode.learning.data.models

data class AuthResponse(
    val success: Boolean,
    val message: String?,
    val data: AuthData?
)

data class AuthData(
    val user: User,
    val token: String,
    val token_type: String
)

data class VideosResponse(
    val success: Boolean,
    val data: List<Video>
)

data class VideoResponse(
    val success: Boolean,
    val message: String?,
    val data: Video?
)

data class ApiError(
    val success: Boolean,
    val message: String
)
package com.thundercode.learning.data.api


import com.thundercode.learning.data.models.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    @POST("v1/register")
    suspend fun register(
        @Body request: RegisterRequest
    ): Response<AuthResponse>

    @POST("v1/login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<AuthResponse>

    @POST("v1/logout")
    suspend fun logout(): Response<ApiError>

    @GET("v1/profile")
    suspend fun getProfile(): Response<UserResponse>

    @GET("v1/videos")
    suspend fun getVideos(
        @Query("search") search: String? = null,
        @Query("page") page: Int = 1
    ): Response<VideosResponse>

    @GET("v1/creator/videos")
    suspend fun getMyVideos(
        @Query("search") search: String? = null
    ): Response<VideosResponse>

    @Multipart
    @POST("v1/creator/videos")
    suspend fun uploadVideo(
        @Part("title") title: RequestBody,
        @Part("description") description: RequestBody?,
        @Part("visibility") visibility: RequestBody,
        @Part video: MultipartBody.Part,
        @Part thumbnail: MultipartBody.Part?
    ): Response<VideoResponse>

    @Multipart
    @POST("v1/creator/videos/{id}")
    suspend fun updateVideo(
        @Path("id") id: Int,
        @Part("_method") method: RequestBody,
        @Part("title") title: RequestBody,
        @Part("description") description: RequestBody?,
        @Part("visibility") visibility: RequestBody?,
        @Part thumbnail: MultipartBody.Part?
    ): Response<VideoResponse>

    @DELETE("v1/creator/videos/{id}")
    suspend fun deleteVideo(
        @Path("id") id: Int
    ): Response<ApiError>
}

data class LoginRequest(val email: String, val password: String)
data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String,
    val password_confirmation: String,
    val phone: String?
)
data class UserResponse(val success: Boolean, val data: User)
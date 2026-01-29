package com.thundercode.learning.data.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
    val id: Int,
    val name: String,
    val email: String,
    val role: String,
    val phone: String?,
    val avatar: String?,
    val bio: String?
) : Parcelable {
    val isAdmin: Boolean get() = role == "admin"
    val isContentCreator: Boolean get() = role == "content_creator"
    val isViewer: Boolean get() = role == "viewer"

    val roleDisplayName: String get() = when(role) {
        "admin" -> "Administrator"
        "content_creator" -> "Content Creator"
        "viewer" -> "Viewer"
        else -> "User"
    }
}

@Parcelize
data class Video(
    val id: Int,
    val title: String,
    val description: String?,
    val slug: String,
    val video_path: String,
    val thumbnail_path: String?,
    val uploaded_by: Int,
    val uploader_name: String?,
    val views_count: Int,
    val visibility: String,
    val status: String,
    val created_at: String
) : Parcelable

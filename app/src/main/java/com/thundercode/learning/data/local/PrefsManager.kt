package com.thundercode.learning.data.local

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.thundercode.learning.data.models.User

class PrefsManager(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences(
        "streaming_prefs",
        Context.MODE_PRIVATE
    )

    private val gson = Gson()

    fun saveToken(token: String) {
        prefs.edit().putString(KEY_TOKEN, token).apply()
    }

    fun getToken(): String? = prefs.getString(KEY_TOKEN, null)

    fun saveUser(user: User) {
        val userJson = gson.toJson(user)
        prefs.edit().putString(KEY_USER, userJson).apply()
    }

    fun getUser(): User? {
        val userJson = prefs.getString(KEY_USER, null)
        return userJson?.let { gson.fromJson(it, User::class.java) }
    }

    fun isLoggedIn(): Boolean = getToken() != null

    fun clear() {
        prefs.edit().clear().apply()
    }

    companion object {
        private const val KEY_TOKEN = "auth_token"
        private const val KEY_USER = "user_data"
    }
}
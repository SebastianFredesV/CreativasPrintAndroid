package com.example.creativasprint.auth

import android.content.Context
import android.content.SharedPreferences
import com.example.creativasprint.model.User
import com.google.gson.Gson

class SessionManager(private val context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("CreativasPrintPrefs", Context.MODE_PRIVATE)
    private val gson = Gson()

    companion object {
        const val KEY_USER = "user_data"
        const val KEY_IS_LOGGED_IN = "is_logged_in"
        const val KEY_AUTH_TOKEN = "auth_token"
        const val ROLE_ADMIN = "admin"
        const val ROLE_CLIENT = "client"
    }

    fun saveUserSession(user: User, token: String? = null) {
        with(sharedPreferences.edit()) {
            putString(KEY_USER, gson.toJson(user))
            putBoolean(KEY_IS_LOGGED_IN, true)
            token?.let { putString(KEY_AUTH_TOKEN, it) }
            apply()
        }
    }

    fun getCurrentUser(): User? {
        val userJson = sharedPreferences.getString(KEY_USER, null)
        return if (userJson != null) {
            gson.fromJson(userJson, User::class.java)
        } else {
            null
        }
    }

    fun getAuthToken(): String? {
        return sharedPreferences.getString(KEY_AUTH_TOKEN, null)
    }

    fun isLoggedIn(): Boolean = sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false)

    fun isAdmin(): Boolean {
        val user = getCurrentUser()
        return user?.role == ROLE_ADMIN
    }

    fun logout() = sharedPreferences.edit().clear().apply()
}
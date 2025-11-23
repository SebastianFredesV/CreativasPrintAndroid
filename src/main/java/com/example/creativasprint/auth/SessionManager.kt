package com.example.creativasprint.auth

import android.content.Context
import android.content.SharedPreferences
import com.example.creativasprint.model.User

class SessionManager(private val context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("CreativasPrintPrefs", Context.MODE_PRIVATE)

    companion object {
        const val KEY_USER_ID = "user_id"
        const val KEY_EMAIL = "email"
        const val KEY_NAME = "name"
        const val KEY_ROLE = "role"
        const val KEY_IS_LOGGED_IN = "is_logged_in"
        const val ROLE_ADMIN = "admin"
        const val ROLE_CLIENT = "client"
    }

    fun saveUserSession(user: User) {
        with(sharedPreferences.edit()) {
            putString(KEY_USER_ID, user.id)
            putString(KEY_EMAIL, user.email)
            putString(KEY_NAME, user.name)
            putString(KEY_ROLE, user.role)
            putBoolean(KEY_IS_LOGGED_IN, true)
            apply()
        }
    }

    fun getCurrentUser(): User? {
        return if (isLoggedIn()) {
            User(
                id = sharedPreferences.getString(KEY_USER_ID, "") ?: "",
                email = sharedPreferences.getString(KEY_EMAIL, "") ?: "",
                name = sharedPreferences.getString(KEY_NAME, "") ?: "",
                role = sharedPreferences.getString(KEY_ROLE, "") ?: ROLE_CLIENT
            )
        } else {
            null
        }
    }

    fun isLoggedIn(): Boolean = sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false)
    fun isAdmin(): Boolean = sharedPreferences.getString(KEY_ROLE, "") == ROLE_ADMIN
    fun logout() = sharedPreferences.edit().clear().apply()
}
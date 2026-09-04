package com.example.medication_demo.storage

import android.content.Context

object CurrentUserStorage {

    private const val PREFS_NAME =
        "current_user_preferences"

    private const val KEY_USER_ID =
        "current_user_id"

    fun saveUserId(
        context: Context,
        userId: String
    ) {
        context
            .getSharedPreferences(
                PREFS_NAME,
                Context.MODE_PRIVATE
            )
            .edit()
            .putString(
                KEY_USER_ID,
                userId
            )
            .apply()
    }

    fun getUserId(
        context: Context
    ): String? {
        return context
            .getSharedPreferences(
                PREFS_NAME,
                Context.MODE_PRIVATE
            )
            .getString(
                KEY_USER_ID,
                null
            )
    }

    fun clearUserId(
        context: Context
    ) {
        context
            .getSharedPreferences(
                PREFS_NAME,
                Context.MODE_PRIVATE
            )
            .edit()
            .remove(
                KEY_USER_ID
            )
            .apply()
    }
}
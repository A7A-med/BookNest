package com.example.booknest.ui.splash

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore

val Context.dataStore by preferencesDataStore(name = "user_prefs")

object UserPreferences {
    val IS_FIRST_TIME = booleanPreferencesKey("is_first_time")
}
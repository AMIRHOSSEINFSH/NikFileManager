package com.android.filemanager.core

import android.content.Context
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import okio.IOException
import javax.inject.Inject

private val Context.dataStore by preferencesDataStore("settings")

class DataStoreManager @Inject constructor(@ApplicationContext appContext: Context) {

    private val settingsDataStore = appContext.dataStore

    suspend fun setThemeMode(mode: DataStorePreferences.THEME): Int {
        settingsDataStore.edit { settings ->
            settings[intPreferencesKey(DataStorePreferences.THEME::class.simpleName ?: "THEME")] =
                mode.value
        }
        return mode.value
    }

    suspend fun doPermissionGranted(isGranted: Boolean) {
        settingsDataStore.edit {settings->
            settings[booleanPreferencesKey(PERMISSION_GRANTED)] = isGranted
        }
    }

    fun getPermissionGranted(): Flow<Boolean>  = settingsDataStore.data.catch { exception ->
        if (exception is IOException) {
            Log.e("TAG", "Error reading preferences: ", exception)
            emit(emptyPreferences())
        } else {
            throw exception
        }
    }.map { pref ->
            pref[booleanPreferencesKey(PERMISSION_GRANTED)] ?: false
        }


}
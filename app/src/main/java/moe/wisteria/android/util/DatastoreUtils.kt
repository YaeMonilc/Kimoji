package moe.wisteria.android.util

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore

private object PreferenceNames {
    const val APP = "KIMOJI_APP"
    const val USER = "KIMOJI_USER"
}

object PreferenceKeys {
    object APP {
        val SELECTED_CHANNEL = stringPreferencesKey("SELECTED_CHANNEL")
    }
    object USER {
        val EMAIL = stringPreferencesKey("SELECTED_CHANNEL")
        val PASSWORD = stringPreferencesKey("SELECTED_CHANNEL")
        val TOKEN = stringPreferencesKey("SELECTED_CHANNEL")
    }
}

val Context.appDatastore: DataStore<Preferences> by preferencesDataStore(
    name = PreferenceNames.APP
)

val Context.userDatastore: DataStore<Preferences> by preferencesDataStore(
    name = PreferenceNames.USER
)
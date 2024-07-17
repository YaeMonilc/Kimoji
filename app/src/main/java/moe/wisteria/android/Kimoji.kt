package moe.wisteria.android

import android.app.Application
import androidx.datastore.preferences.core.edit
import com.google.android.material.color.DynamicColors
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import moe.wisteria.android.network.setDefaultOkhttpClientDns
import moe.wisteria.android.util.IO
import moe.wisteria.android.util.PreferenceKeys
import moe.wisteria.android.util.appDatastore

class Kimoji : Application() {
    override fun onCreate() {
        super.onCreate()

        DynamicColors.applyToActivitiesIfAvailable(this)
        initDefaultOkhttpClient()
    }

    private fun initDefaultOkhttpClient() {
        CoroutineScope(IO).launch {
            appDatastore.edit {
                setDefaultOkhttpClientDns(it[PreferenceKeys.APP.SELECTED_CHANNEL])
            }
        }
    }
}
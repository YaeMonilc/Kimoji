package moe.wisteria.android.kimoji

import android.app.Application
import androidx.datastore.preferences.core.edit
import com.google.android.material.color.DynamicColors
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import moe.wisteria.android.kimoji.network.setDefaultOkhttpClientDns
import moe.wisteria.android.kimoji.util.IO
import moe.wisteria.android.kimoji.util.PreferenceKeys
import moe.wisteria.android.kimoji.util.appDatastore
import moe.wisteria.android.kimoji.util.launchIO

class Kimoji : Application() {
    override fun onCreate() {
        super.onCreate()

        DynamicColors.applyToActivitiesIfAvailable(this)
        initDefaultOkhttpClient()
    }

    private fun initDefaultOkhttpClient() {
        launchIO {
            appDatastore.edit {
                setDefaultOkhttpClientDns(it[PreferenceKeys.APP.SELECTED_CHANNEL])
            }
        }
    }
}
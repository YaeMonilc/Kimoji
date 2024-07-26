package moe.wisteria.android.kimoji

import android.app.Application
import android.content.Intent
import androidx.datastore.preferences.core.edit
import com.google.android.material.color.DynamicColors
import moe.wisteria.android.kimoji.network.setDefaultOkhttpClientDns
import moe.wisteria.android.kimoji.ui.activity.fatalExceptionCatch.FatalExceptionCatchActivity
import moe.wisteria.android.kimoji.util.PreferenceKeys
import moe.wisteria.android.kimoji.util.appDatastore
import moe.wisteria.android.kimoji.util.launchIO
import kotlin.system.exitProcess

class Kimoji : Application() {
    companion object {
        object Dir {
            const val COIL_CACHE = "coil_cache"
        }
    }

    override fun onCreate() {
        super.onCreate()

        DynamicColors.applyToActivitiesIfAvailable(this)

        initUncaughtExceptionHandle()
        initDefaultOkhttpClient()
    }

    private fun initUncaughtExceptionHandle() {
        Thread.setDefaultUncaughtExceptionHandler { _, throwable ->
            startActivity(Intent(this, FatalExceptionCatchActivity::class.java).apply {
                putExtra(FatalExceptionCatchActivity.EXTRA_EXCEPTION, throwable.stackTraceToString())
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            })
            throwable.printStackTrace()
            exitProcess(0)
        }
    }

    private fun initDefaultOkhttpClient() {
        launchIO {
            appDatastore.edit {
                setDefaultOkhttpClientDns(it[PreferenceKeys.APP.SELECTED_CHANNEL])
            }
        }
    }
}
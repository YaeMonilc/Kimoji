package moe.wisteria.android.kimoji.ui.activity.fatalExceptionCatch

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import moe.wisteria.android.kimoji.R
import moe.wisteria.android.kimoji.databinding.ActivityFatalExceptionCatchBinding
import moe.wisteria.android.kimoji.ui.activity.main.MainActivity
import kotlin.system.exitProcess

class FatalExceptionCatchActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFatalExceptionCatchBinding

    companion object {
        const val EXTRA_EXCEPTION = "EXTRA_EXCEPTION"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityFatalExceptionCatchBinding.inflate(layoutInflater).also {
            setContentView(it.root)
        }

        intent.extras?.getString(EXTRA_EXCEPTION)?.let {
            binding.activityFatalExceptionCatchMessage.text = it
        }

        initEvent()
    }

    private fun initEvent() {
        binding.activityFatalExceptionCatchCopy.setOnClickListener {
            (getSystemService(CLIPBOARD_SERVICE) as ClipboardManager).setPrimaryClip(
                ClipData.newPlainText(null, binding.activityFatalExceptionCatchMessage.text)
            )

            Snackbar.make(binding.root, R.string.activity_fatal_exception_catch_copy_complete, Snackbar.LENGTH_SHORT).show()
        }

        binding.activityFatalExceptionCatchRestart.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            })
            exitProcess(0)
        }
    }
}
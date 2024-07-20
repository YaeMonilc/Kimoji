package moe.wisteria.android.kimoji.ui.activity.main

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import moe.wisteria.android.kimoji.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater).also {
            setContentView(it.root)
        }

        initToolBar()
    }

    private fun initToolBar() {
        setSupportActionBar(binding.activityMainToolbar)
    }
}
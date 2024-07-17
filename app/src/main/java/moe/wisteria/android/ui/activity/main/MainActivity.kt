package moe.wisteria.android.ui.activity.main

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import moe.wisteria.android.R
import moe.wisteria.android.databinding.ActivityMainBinding

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
package moe.wisteria.android.kimoji.util

import android.app.Application
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import moe.wisteria.android.kimoji.ui.view.BaseFragment

val MAIN = Dispatchers.Main
val IO = Dispatchers.IO

fun Application.launchUI(block: suspend CoroutineScope.() -> Unit) = CoroutineScope(MAIN).launch {
    block()
}

fun Application.launchIO(block: suspend CoroutineScope.() -> Unit) = CoroutineScope(IO).launch {
    block()
}

fun AppCompatActivity.launchUI(block: suspend CoroutineScope.() -> Unit) = lifecycleScope.launch(
    MAIN
) {
    block()
}

fun AppCompatActivity.launchIO(block: suspend CoroutineScope.() -> Unit) = lifecycleScope.launch(IO) {
    block()
}

fun BaseFragment.launchUI(block: suspend CoroutineScope.() -> Unit) = lifecycleScope.launch(MAIN) {
    block()
}

fun BaseFragment.launchIO(block: suspend CoroutineScope.() -> Unit) = lifecycleScope.launch(IO) {
    block()
}

fun ViewModel.launchUI(block: suspend CoroutineScope.() -> Unit) = viewModelScope.launch(MAIN) {
    block()
}

fun ViewModel.launchIO(block: suspend CoroutineScope.() -> Unit) = viewModelScope.launch(IO) {
    block()
}
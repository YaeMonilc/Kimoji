package moe.wisteria.android.util

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import moe.wisteria.android.ui.view.BaseFragment

val MAIN = Dispatchers.Main
val IO = Dispatchers.IO

fun AppCompatActivity.launchUI(block: suspend CoroutineScope.() -> Unit) = lifecycleScope.launch(MAIN) {
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
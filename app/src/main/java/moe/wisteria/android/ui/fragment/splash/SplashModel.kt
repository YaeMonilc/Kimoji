package moe.wisteria.android.ui.fragment.splash

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SplashModel : ViewModel() {
    enum class NavigatePosition {
        CHANNEL_SELECTOR,
        SIGN_IN,
        INDEX
    }

    private val _navigatePosition: MutableLiveData<NavigatePosition> = MutableLiveData()
    val navigatePosition: LiveData<NavigatePosition> = _navigatePosition
}
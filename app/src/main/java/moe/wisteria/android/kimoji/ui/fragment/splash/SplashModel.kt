package moe.wisteria.android.kimoji.ui.fragment.splash

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.NavDirections
import kotlinx.coroutines.flow.catch
import moe.wisteria.android.kimoji.network.PICACOMIC_SERVER_URL
import moe.wisteria.android.kimoji.repository.network.PicaRepository
import moe.wisteria.android.kimoji.util.NetworkUtils
import moe.wisteria.android.kimoji.util.launchIO

class SplashModel : ViewModel() {
    private val _navigatePosition: MutableLiveData<NavDirections> = MutableLiveData()
    val navigatePosition: LiveData<NavDirections>
        get() = _navigatePosition

    private val _token: MutableLiveData<String> = MutableLiveData()
    val token: LiveData<String>
        get() = _token

    fun init(
        channel: String?,
        email: String?,
        password: String?
    ) {
        launchIO {
            if (channel == null) {
                navigateToChannelSelector()
                return@launchIO
            }

            try {
                NetworkUtils.tryConnect(PICACOMIC_SERVER_URL)
            } catch (e: Exception) {
                navigateToChannelSelector()
                return@launchIO
            }

            if (email == null || password == null) {
                navigateToSignIn()
                return@launchIO
            }

            PicaRepository.Auth.signIn(
                email = email,
                password = password
            ).catch {
                navigateToSignIn()
            }.collect {
                _token.postValue(it)
                navigateToIndex()
            }
        }
    }

    private fun navigateToChannelSelector() {
        _navigatePosition.postValue(SplashFragmentDirections.actionSplashFragmentToChannelSelectorFragment())
    }

    private fun navigateToSignIn() {
        _navigatePosition.postValue(SplashFragmentDirections.actionSplashFragmentToSignInFragment())
    }

    private fun navigateToIndex() {
        _navigatePosition.postValue(SplashFragmentDirections.actionSplashFragmentToIndexFragment())
    }
}
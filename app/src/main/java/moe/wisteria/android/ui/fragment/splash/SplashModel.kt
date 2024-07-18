package moe.wisteria.android.ui.fragment.splash

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.async
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import moe.wisteria.android.R
import moe.wisteria.android.entity.NetworkState
import moe.wisteria.android.network.PICACOMIC_SERVER_URL
import moe.wisteria.android.util.IO
import moe.wisteria.android.util.MAIN
import moe.wisteria.android.util.NetworkUtils

class SplashModel : ViewModel() {
    enum class NavigatePosition {
        CHANNEL_SELECTOR,
        SIGN_IN,
        HOME
    }

    private val _tryConnectServerState: MutableLiveData<NetworkState<Int>> = MutableLiveData(NetworkState())
    val tryConnectServerState: LiveData<NetworkState<Int>> = _tryConnectServerState

    private val _tryLoginState: MutableLiveData<NetworkState<Int>> = MutableLiveData(NetworkState())
    val tryLoginState: LiveData<NetworkState<Int>> = _tryLoginState

    private val _navigatePosition: MutableLiveData<NavigatePosition> = MutableLiveData(NavigatePosition.HOME)
    val navigatePosition: LiveData<NavigatePosition> = _navigatePosition

    fun init(
        channel: String?,
        email: String?,
        password: String?
    ) {
        viewModelScope.launch(IO) {
            if (channel == null) {
                _navigatePosition.postValue(NavigatePosition.CHANNEL_SELECTOR)
                return@launch
            }

            if (email == null || password == null) {
                _navigatePosition.postValue(NavigatePosition.SIGN_IN)
                return@launch
            }

            async {
                val result = NetworkUtils.tryConnect(
                    url = PICACOMIC_SERVER_URL
                )

                _tryConnectServerState.postValue(_tryConnectServerState.value!!.copy(
                    state = if (result) NetworkState.State.SUCCESS else NetworkState.State.FAILED,
                    data = if (result) null else R.string.network_server_connect_failed
                ))
            }.await()

            if (_tryConnectServerState.value!!.state == NetworkState.State.FAILED) {
                _navigatePosition.postValue(NavigatePosition.CHANNEL_SELECTOR)
                return@launch
            }

            async {
                delay(1000)
                _tryLoginState.postValue(_tryLoginState.value!!.copy(
                    state = NetworkState.State.SUCCESS
                ))
            }.await()
        }
    }
}
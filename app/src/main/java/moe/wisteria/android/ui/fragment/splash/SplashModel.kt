package moe.wisteria.android.ui.fragment.splash

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import moe.wisteria.android.R
import moe.wisteria.android.entity.NetworkState
import moe.wisteria.android.network.PICACOMIC_SERVER_URL
import moe.wisteria.android.network.entity.body.SignInBody
import moe.wisteria.android.network.entity.response.PicacomicErrorResponse
import moe.wisteria.android.network.entity.response.PicacomicSignInResponse
import moe.wisteria.android.network.picacomicApi
import moe.wisteria.android.util.IO
import moe.wisteria.android.util.NetworkUtils
import moe.wisteria.android.util.NetworkUtils.responseAnalysis
import moe.wisteria.android.util.localizationMapping

class SplashModel : ViewModel() {
    enum class NavigatePosition {
        CHANNEL_SELECTOR,
        SIGN_IN,
        INDEX
    }

    private val _tryConnectServerState: MutableLiveData<NetworkState<Int>> = MutableLiveData(NetworkState())
    val tryConnectServerState: LiveData<NetworkState<Int>> = _tryConnectServerState

    private val _tryLoginState: MutableLiveData<NetworkState<Int>> = MutableLiveData(NetworkState())
    val tryLoginState: LiveData<NetworkState<Int>> = _tryLoginState

    private val _navigatePosition: MutableLiveData<NavigatePosition> = MutableLiveData()
    val navigatePosition: LiveData<NavigatePosition> = _navigatePosition

    private val _token: MutableLiveData<String> = MutableLiveData()
    val token: LiveData<String> = _token

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

            if (!initChannel(channel)) {
                _navigatePosition.postValue(NavigatePosition.CHANNEL_SELECTOR)
                return@launch
            }

            if (tryLogin(email, password)) {
                _navigatePosition.postValue(NavigatePosition.SIGN_IN)
                return@launch
            }

            _navigatePosition.postValue(NavigatePosition.INDEX)
        }
    }

    private suspend fun initChannel(
        channel: String
    ): Boolean = withContext(IO) {
        _tryConnectServerState.postValue(_tryConnectServerState.value!!.copy(
            state = NetworkState.State.LOADING
        ))

        val result = NetworkUtils.tryConnect(
            url = PICACOMIC_SERVER_URL
        )
        _tryConnectServerState.postValue(_tryConnectServerState.value!!.copy(
            state = if (result) NetworkState.State.SUCCESS else NetworkState.State.FAILED,
            data = if (result) null else R.string.network_server_connect_failed
        ))

        _tryConnectServerState.value!!.state == NetworkState.State.SUCCESS
    }

    private suspend fun tryLogin(
        email: String,
        password: String
    ): Boolean = withContext(IO) {
        _tryLoginState.postValue(_tryLoginState.value!!.copy(
            state = NetworkState.State.LOADING
        ))
        picacomicApi.signIn(
            signInBody = SignInBody(
                email = email,
                password = password
            )
        ).responseAnalysis<PicacomicSignInResponse, PicacomicErrorResponse>(
            success = {
                _token.postValue(it.data?.token)

                _tryLoginState.postValue(_tryLoginState.value!!.copy(
                    state = NetworkState.State.SUCCESS
                ))
            },
            error = {
                _tryLoginState.postValue(_tryLoginState.value!!.copy(
                    state = NetworkState.State.FAILED,
                    data = localizationMapping[it.message]
                ))
            },
            failure = {
                _tryLoginState.postValue(_tryLoginState.value!!.copy(
                    state = NetworkState.State.EXCEPTION,
                    exception = it
                ))
            }
        )

        _tryLoginState.value!!.state == NetworkState.State.SUCCESS
    }
}
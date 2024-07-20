package moe.wisteria.android.ui.fragment.splash

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.withContext
import moe.wisteria.android.network.PICACOMIC_SERVER_URL
import moe.wisteria.android.network.entity.body.SignInBody
import moe.wisteria.android.network.entity.response.PicaResponse
import moe.wisteria.android.network.picaApi
import moe.wisteria.android.util.IO
import moe.wisteria.android.util.NetworkUtils
import moe.wisteria.android.util.executeForPica
import moe.wisteria.android.util.launchIO

class SplashModel : ViewModel() {
    enum class NavigatePosition {
        CHANNEL_SELECTOR,
        SIGN_IN,
        INDEX
    }

    private val _navigatePosition: MutableLiveData<NavigatePosition> = MutableLiveData()
    val navigatePosition: LiveData<NavigatePosition> = _navigatePosition

    private val _signInResponse: MutableLiveData<PicaResponse<PicaResponse.SignInResponse>> = MutableLiveData()
    val signInResponse: LiveData<PicaResponse<PicaResponse.SignInResponse>>
        get() = _signInResponse

    fun init(
        channel: String?,
        email: String?,
        password: String?
    ) {
        launchIO {
            if (channel == null) {
                _navigatePosition.postValue(NavigatePosition.CHANNEL_SELECTOR)
                return@launchIO
            }

            if (email == null || password == null) {
                _navigatePosition.postValue(NavigatePosition.SIGN_IN)
                return@launchIO
            }

            if (!tryConnectServer()) {
                _navigatePosition.postValue(NavigatePosition.CHANNEL_SELECTOR)
                return@launchIO
            }

            if (trySignIn(
                    email = email,
                    password = password
                ).status != PicaResponse.Status.SUCCESS
            ) {
                _navigatePosition.postValue(NavigatePosition.SIGN_IN)
                return@launchIO
            }

            _navigatePosition.postValue(NavigatePosition.INDEX)
        }
    }

    private suspend fun tryConnectServer(): Boolean = withContext(IO) {
        return@withContext NetworkUtils.tryConnect(PICACOMIC_SERVER_URL)
    }

    private suspend fun trySignIn(
        email: String,
        password: String
    ): PicaResponse<PicaResponse.SignInResponse> = withContext(IO) {
        return@withContext picaApi.signIn(
            body = SignInBody(
                email = email,
                password = password
            )
        ).executeForPica<PicaResponse.SignInResponse>().also {
            _signInResponse.postValue(it)
        }
    }
}
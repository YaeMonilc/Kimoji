package moe.wisteria.android.kimoji.ui.fragment.splash

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.NavDirections
import kotlinx.coroutines.withContext
import moe.wisteria.android.kimoji.network.PICACOMIC_SERVER_URL
import moe.wisteria.android.kimoji.network.entity.body.SignInBody
import moe.wisteria.android.kimoji.network.entity.response.PicaResponse
import moe.wisteria.android.kimoji.network.picaApi
import moe.wisteria.android.kimoji.util.IO
import moe.wisteria.android.kimoji.util.NetworkUtils
import moe.wisteria.android.kimoji.util.executeForPica
import moe.wisteria.android.kimoji.util.launchIO

class SplashModel : ViewModel() {
    private val _navigatePosition: MutableLiveData<NavDirections> = MutableLiveData()
    val navigatePosition: LiveData<NavDirections> = _navigatePosition

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
                _navigatePosition.postValue(SplashFragmentDirections.actionSplashFragmentToChannelSelectorFragment())
                return@launchIO
            }

            if (!tryConnectServer()) {
                _navigatePosition.postValue(SplashFragmentDirections.actionSplashFragmentToChannelSelectorFragment())
                return@launchIO
            }

            if (email == null || password == null) {
                _navigatePosition.postValue(SplashFragmentDirections.actionSplashFragmentToSignInFragment())
                return@launchIO
            }

            if (trySignIn(
                    email = email,
                    password = password
                ).status != PicaResponse.Status.SUCCESS
            ) {
                _navigatePosition.postValue(SplashFragmentDirections.actionSplashFragmentToSignInFragment())
                return@launchIO
            }

            _navigatePosition.postValue(SplashFragmentDirections.actionSplashFragmentToIndexFragment())
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
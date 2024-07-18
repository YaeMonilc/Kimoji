package moe.wisteria.android.ui.fragment.signIn

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import moe.wisteria.android.entity.IndicatorState
import moe.wisteria.android.entity.NetworkState
import moe.wisteria.android.network.entity.body.SignInBody
import moe.wisteria.android.network.entity.response.PicacomicErrorResponse
import moe.wisteria.android.network.entity.response.PicacomicSignInResponse
import moe.wisteria.android.network.picacomicApi
import moe.wisteria.android.util.IO
import moe.wisteria.android.util.NetworkUtils.responseAnalysis
import moe.wisteria.android.util.localizationMapping

class SignInModel : ViewModel() {

    private val _indicatorState: MutableLiveData<IndicatorState> = MutableLiveData(IndicatorState.NORMAL)
    val indicatorState: LiveData<IndicatorState> = _indicatorState

    private val _networkState: MutableLiveData<NetworkState<Int>> = MutableLiveData(NetworkState())
    val networkState: LiveData<NetworkState<Int>> = _networkState

    private val _token: MutableLiveData<String> = MutableLiveData()
    val token: LiveData<String> = _token

    val email: MutableLiveData<String> = MutableLiveData("")
    val password: MutableLiveData<String> = MutableLiveData("")

    fun setEmail(
        email: String?
    ) {
        this.email.postValue(email)
    }

    fun setPassword(
        password: String?
    ) {
        this.password.postValue(password)
    }

    fun signIn() {
        _indicatorState.postValue(IndicatorState.LOADING)
        _networkState.postValue(_networkState.value!!.copy(
            state = NetworkState.State.LOADING
        ))

        viewModelScope.launch(IO) {
            picacomicApi.signIn(
                signInBody = SignInBody(
                    email = email.value!!,
                    password = password.value!!
                )
            ).responseAnalysis<PicacomicSignInResponse, PicacomicErrorResponse>(
                success = {
                    _token.postValue(it.data?.token)

                    _networkState.postValue(_networkState.value!!.copy(
                        state = NetworkState.State.SUCCESS
                    ))
                },
                error = {
                    _networkState.postValue(_networkState.value!!.copy(
                        state = NetworkState.State.FAILED,
                        data = localizationMapping[it.message]
                    ))
                },
                failure = {
                    _networkState.postValue(_networkState.value!!.copy(
                        state = NetworkState.State.EXCEPTION,
                        exception = it
                    ))
                    it.printStackTrace()
                },
                finally = {
                    _indicatorState.postValue(IndicatorState.NORMAL)
                }
            )
        }
    }
}
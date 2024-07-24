package moe.wisteria.android.kimoji.ui.fragment.signIn

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import moe.wisteria.android.kimoji.entity.IndicatorState
import moe.wisteria.android.kimoji.network.entity.body.SignInBody
import moe.wisteria.android.kimoji.network.entity.response.PicaResponse
import moe.wisteria.android.kimoji.network.picaApi
import moe.wisteria.android.kimoji.util.executeForPica
import moe.wisteria.android.kimoji.util.launchIO

class SignInModel : ViewModel() {

    private val _indicatorState: MutableLiveData<IndicatorState> = MutableLiveData(IndicatorState.NORMAL)
    val indicatorState: LiveData<IndicatorState>
        get() = _indicatorState

    private val _signInResponse: MutableLiveData<PicaResponse<PicaResponse.SignInResponse>> = MutableLiveData()
    val signInResponse: LiveData<PicaResponse<PicaResponse.SignInResponse>>
        get() = _signInResponse

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

        launchIO {
            picaApi.signIn(
                body = SignInBody(
                    email = email.value!!,
                    password = password.value!!
                )
            ).executeForPica<PicaResponse.SignInResponse>().also {
                _signInResponse.postValue(it)
                _indicatorState.postValue(IndicatorState.NORMAL)
            }
        }
    }
}
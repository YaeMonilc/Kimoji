package moe.wisteria.android.kimoji.ui.fragment.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import moe.wisteria.android.kimoji.entity.IndicatorState
import moe.wisteria.android.kimoji.network.entity.body.RegisterBody
import moe.wisteria.android.kimoji.network.entity.response.PicaResponse
import moe.wisteria.android.kimoji.network.picaApi
import moe.wisteria.android.kimoji.util.executeForPica
import moe.wisteria.android.kimoji.util.launchIO

class RegisterModel : ViewModel() {
    enum class Gender(
        val realValue: String
    ) {
        MAN("m"),
        WOMAN("f"),
        OTHER("bot")
    }

    private val _indicatorState: MutableLiveData<IndicatorState> = MutableLiveData(IndicatorState.NORMAL)
    val indicatorState: LiveData<IndicatorState>
        get() = _indicatorState

    private val _registerResponse: MutableLiveData<PicaResponse<PicaResponse.BaseResponse>> = MutableLiveData()
    val registerResponse: LiveData<PicaResponse<PicaResponse.BaseResponse>>
        get() = _registerResponse

    val email: MutableLiveData<String> = MutableLiveData("")
    val name: MutableLiveData<String> = MutableLiveData("")
    val password: MutableLiveData<String> = MutableLiveData("")
    val gender: MutableLiveData<String> = MutableLiveData(Gender.MAN.realValue)
    val birthday: MutableLiveData<String> = MutableLiveData("")
    val question: MutableLiveData<String> = MutableLiveData("")
    val answer: MutableLiveData<String> = MutableLiveData("")

    fun register() {
        _indicatorState.postValue(IndicatorState.LOADING)

        launchIO {
            picaApi.register(
                body = RegisterBody(
                    email = email.value!!,
                    name = name.value!!,
                    password = password.value!!,
                    gender = gender.value!!,
                    birthday = birthday.value!!,
                    question1 = question.value!!,
                    answer1 = answer.value!!,
                )
            ).executeForPica<PicaResponse.BaseResponse>().also {
                _registerResponse.postValue(it)
                _indicatorState.postValue(IndicatorState.NORMAL)
            }
        }
    }
}
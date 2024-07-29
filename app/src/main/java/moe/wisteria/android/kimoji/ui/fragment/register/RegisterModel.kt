package moe.wisteria.android.kimoji.ui.fragment.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.catch
import moe.wisteria.android.kimoji.entity.IndicatorState
import moe.wisteria.android.kimoji.repository.network.PicaRepository
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

    val email: MutableLiveData<String> = MutableLiveData("")
    val name: MutableLiveData<String> = MutableLiveData("")
    val password: MutableLiveData<String> = MutableLiveData("")
    val gender: MutableLiveData<String> = MutableLiveData(Gender.MAN.realValue)
    val birthday: MutableLiveData<String> = MutableLiveData("")
    val question: MutableLiveData<String> = MutableLiveData("")
    val answer: MutableLiveData<String> = MutableLiveData("")

    fun register(
        success: () -> Unit,
        exceptionHandler: (Exception) -> Unit
    ) {
        _indicatorState.postValue(IndicatorState.LOADING)

        launchIO {
            PicaRepository.Auth.register(
                email = email.value!!,
                name = name.value!!,
                password = password.value!!,
                gender = gender.value!!,
                birthday = birthday.value!!,
                question1 = question.value!!,
                answer1 = answer.value!!,
            ).catch {
                exceptionHandler(it as Exception)
            }.collect {
                success()
            }
        }
    }
}
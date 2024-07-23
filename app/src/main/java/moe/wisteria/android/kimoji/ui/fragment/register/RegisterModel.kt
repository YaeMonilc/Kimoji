package moe.wisteria.android.kimoji.ui.fragment.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import moe.wisteria.android.kimoji.entity.IndicatorState

class RegisterModel : ViewModel() {
    enum class Gender(
        val realValue: String
    ) {
        MAN("m"),
        WOMAN("f"),
        OTHER("bot")
    }

    private val _indicatorState: MutableLiveData<IndicatorState> = MutableLiveData(IndicatorState.NORMAL)
    val indicatorState: LiveData<IndicatorState> = _indicatorState

    val email: MutableLiveData<String> = MutableLiveData("")
    val name: MutableLiveData<String> = MutableLiveData("")
    val password: MutableLiveData<String> = MutableLiveData("")
    val gender: MutableLiveData<String> = MutableLiveData(Gender.MAN.realValue)
    val birthday: MutableLiveData<String> = MutableLiveData("")
    val question: MutableLiveData<String> = MutableLiveData("")
    val answer: MutableLiveData<String> = MutableLiveData("")
}
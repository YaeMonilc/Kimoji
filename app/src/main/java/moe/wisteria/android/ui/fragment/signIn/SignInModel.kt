package moe.wisteria.android.ui.fragment.signIn

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import moe.wisteria.android.entity.IndicatorState

class SignInModel : ViewModel() {

    private val _indicatorState: MutableLiveData<IndicatorState> = MutableLiveData(IndicatorState.NORMAL)
    val indicatorState: LiveData<IndicatorState> = _indicatorState

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

}
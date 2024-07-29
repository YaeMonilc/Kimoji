package moe.wisteria.android.kimoji.ui.fragment.signIn

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.firstOrNull
import moe.wisteria.android.kimoji.entity.IndicatorState
import moe.wisteria.android.kimoji.repository.network.PicaRepository

class SignInModel : ViewModel() {

    private val _indicatorState: MutableLiveData<IndicatorState> = MutableLiveData(IndicatorState.NORMAL)
    val indicatorState: LiveData<IndicatorState>
        get() = _indicatorState

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

    suspend fun signIn(
        exceptionHandler: (Exception) -> Unit
    ): String? {
        _indicatorState.postValue(IndicatorState.LOADING)

        return PicaRepository.Auth.signIn(
            email = email.value!!,
            password = password.value!!
        ).catch {
            exceptionHandler(it as Exception)
        }.firstOrNull().also {
            _indicatorState.postValue(IndicatorState.NORMAL)
        }
    }
}
package moe.wisteria.android.kimoji.ui.fragment.index.page.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.catch
import moe.wisteria.android.kimoji.entity.IndicatorState
import moe.wisteria.android.kimoji.entity.Profile
import moe.wisteria.android.kimoji.repository.network.PicaRepository
import moe.wisteria.android.kimoji.util.launchIO

class ProfileModel : ViewModel() {

    private val _indicatorState: MutableLiveData<IndicatorState> =
        MutableLiveData(IndicatorState.NORMAL)
    val indicatorState: LiveData<IndicatorState>
        get() = _indicatorState

    private val _profile: MutableLiveData<Profile> = MutableLiveData()
    val profile: LiveData<Profile>
        get() = _profile


    fun loadProfile(
        token: String,
        exceptionHandler: (Exception) -> Unit
    ) {
        launchIO {
            PicaRepository.User.profile(
                token = token
            ).catch {
                exceptionHandler(it as Exception)
            }.collect {
                _profile.postValue(it)
            }
        }
    }

    fun punchIn(
        token: String,
        exceptionHandler: (Exception) -> Unit
    ) = PicaRepository.User.punchIn(
        token = token
    ).catch {
        exceptionHandler(it as Exception)
    }
}


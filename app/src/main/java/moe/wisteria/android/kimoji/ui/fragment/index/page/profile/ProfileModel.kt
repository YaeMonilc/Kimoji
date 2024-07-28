package moe.wisteria.android.kimoji.ui.fragment.index.page.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import moe.wisteria.android.kimoji.entity.IndicatorState
import moe.wisteria.android.kimoji.entity.Profile
import moe.wisteria.android.kimoji.network.entity.response.PicaResponse
import moe.wisteria.android.kimoji.network.entity.response.PicaResponse.Companion.onSuccess
import moe.wisteria.android.kimoji.network.picaApi
import moe.wisteria.android.kimoji.util.executeForPica
import moe.wisteria.android.kimoji.util.launchIO

class ProfileModel : ViewModel() {

    private val _indicatorState: MutableLiveData<IndicatorState> = MutableLiveData(IndicatorState.NORMAL)
    val indicatorState: LiveData<IndicatorState>
        get() = _indicatorState

    private val _usersProfileResponse: MutableLiveData<PicaResponse<PicaResponse.UsersProfile>> = MutableLiveData()
    val usersProfileResponse: LiveData<PicaResponse<PicaResponse.UsersProfile>>
        get() = _usersProfileResponse

    private val _usersPunchInResponse: MutableLiveData<PicaResponse<PicaResponse.UsersPunchIn>> = MutableLiveData()
    val usersPunchInResponse: LiveData<PicaResponse<PicaResponse.UsersPunchIn>>
        get() = _usersPunchInResponse

    private val _profile: MutableLiveData<Profile> = MutableLiveData()
    val profile: LiveData<Profile>
        get() = _profile


    fun getProfile(
        token: String
    ) {
        launchIO {
            picaApi.usersProfile(
                token = token
            ).executeForPica<PicaResponse.UsersProfile>().let {
                it.onSuccess { usersProfile ->
                    _profile.postValue(usersProfile.data.user)
                }
            }.also {
                _usersProfileResponse.postValue(it)
            }
        }
    }

    fun punchIn(
        token: String
    ) {
        launchIO {
            picaApi.usersPunchIn(
                token = token
            ).executeForPica<PicaResponse.UsersPunchIn>().also {
                _usersPunchInResponse.postValue(it)
            }
        }
    }
}
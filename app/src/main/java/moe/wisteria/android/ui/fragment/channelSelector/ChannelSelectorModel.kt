package moe.wisteria.android.ui.fragment.channelSelector

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import moe.wisteria.android.network.channelApi
import moe.wisteria.android.network.entity.response.ChannelInitResponse
import moe.wisteria.android.util.IO
import moe.wisteria.android.util.NetworkUtils.responseAnalysis

class ChannelSelectorModel : ViewModel() {
    enum class State {
        NORMAL,
        LOADING,
        FAILED
    }

    private val _state: MutableLiveData<State> = MutableLiveData(State.NORMAL)
    val state: LiveData<State> = _state

    private val _channelList: MutableLiveData<List<String>> = MutableLiveData(listOf())
    val channelList: LiveData<List<String>> = _channelList

    fun loadChannelList() {
        _state.postValue(State.LOADING)

        viewModelScope.launch(IO) {
            channelApi.init().responseAnalysis<ChannelInitResponse, Any>(
                success = {
                    _channelList.postValue(it.addresses)
                },
                finally = {
                    _state.postValue(State.NORMAL)
                }
            )
        }
    }
}
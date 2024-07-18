package moe.wisteria.android.ui.fragment.channelSelector

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import moe.wisteria.android.R
import moe.wisteria.android.entity.IndicatorState
import moe.wisteria.android.entity.NetworkState
import moe.wisteria.android.network.channelApi
import moe.wisteria.android.network.entity.response.ChannelInitResponse
import moe.wisteria.android.util.IO
import moe.wisteria.android.util.NetworkUtils.responseAnalysis

class ChannelSelectorModel : ViewModel() {
    enum class NavigatePosition {
        SIGN_IN,
        BACK;

        companion object {
            fun getByValue(
                value: Int
            ): NavigatePosition? {
                return entries.firstOrNull() { it.ordinal == value }
            }
        }
    }

    private val _indicatorState: MutableLiveData<IndicatorState> = MutableLiveData(IndicatorState.NORMAL)
    val indicatorState: LiveData<IndicatorState> = _indicatorState

    private val _networkState: MutableLiveData<NetworkState<Int>> = MutableLiveData(NetworkState())
    val networkState: LiveData<NetworkState<Int>> = _networkState

    private val _channelList: MutableLiveData<List<String>> = MutableLiveData(listOf())
    val channelList: LiveData<List<String>> = _channelList

    private val _navigatePosition: MutableLiveData<NavigatePosition> = MutableLiveData(NavigatePosition.SIGN_IN)
    val navigatePosition: LiveData<NavigatePosition> = _navigatePosition

    fun loadChannelList() {
        _indicatorState.postValue(IndicatorState.LOADING)

        viewModelScope.launch(IO) {
            channelApi.init().responseAnalysis<ChannelInitResponse, String>(
                success = {
                    _channelList.postValue(it.addresses)
                },
                error = {
                    _networkState.postValue(_networkState.value!!.copy(
                        state = NetworkState.State.FAILED,
                        data = R.string.network_unknown_failed
                    ))
                },
                failure = {
                    _networkState.postValue(_networkState.value!!.copy(
                        state = NetworkState.State.FAILED,
                        data = R.string.network_server_connect_failed
                    ))
                },
                finally = {
                    _indicatorState.postValue(IndicatorState.NORMAL)
                }
            )
        }
    }

    fun setNavigatePosition(
        navigatePosition: NavigatePosition?
    ) {
        navigatePosition?.let {
            _navigatePosition.postValue(it)
        }
    }
}
package moe.wisteria.android.kimoji.ui.fragment.channelSelector

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import moe.wisteria.android.kimoji.entity.IndicatorState
import moe.wisteria.android.kimoji.network.channelApi
import moe.wisteria.android.kimoji.util.launchIO

class ChannelSelectorModel : ViewModel() {
    private val _indicatorState: MutableLiveData<IndicatorState> = MutableLiveData(IndicatorState.NORMAL)
    val indicatorState: LiveData<IndicatorState>
        get() = _indicatorState

    private val _navigatePosition: MutableLiveData<ChannelSelectorFragment.NavigatePosition> = MutableLiveData(
        ChannelSelectorFragment.NavigatePosition.SIGN_IN
    )
    val navigatePosition: LiveData<ChannelSelectorFragment.NavigatePosition>
        get() = _navigatePosition

    private val _channelList: MutableLiveData<List<String>> = MutableLiveData(listOf())
    val channelList: LiveData<List<String>>
        get() = _channelList

    init {
        _indicatorState.postValue(IndicatorState.LOADING)

        launchIO {
            try {
                channelApi.init().addresses.let {
                    _channelList.postValue(it)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _indicatorState.postValue(IndicatorState.NORMAL)
            }
        }
    }

    fun setNavigatePosition(
        navigatePosition: ChannelSelectorFragment.NavigatePosition?
    ) {
        navigatePosition?.let {
            _navigatePosition.postValue(it)
        }
    }
}
package moe.wisteria.android.kimoji.ui.fragment.channelSelector

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import moe.wisteria.android.kimoji.entity.IndicatorState
import moe.wisteria.android.kimoji.network.channelApi
import moe.wisteria.android.kimoji.util.launchIO

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

    private val _navigatePosition: MutableLiveData<NavigatePosition> = MutableLiveData(
        NavigatePosition.SIGN_IN
    )
    val navigatePosition: LiveData<NavigatePosition> = _navigatePosition

    private val _channelList: MutableLiveData<List<String>> = MutableLiveData(listOf())
    val channelList: LiveData<List<String>> = _channelList

    init {
        _indicatorState.postValue(IndicatorState.LOADING)

        launchIO {
            channelApi.init().addresses.let {
                try {
                    _channelList.postValue(it)
                } catch (e: Exception) {
                    e.printStackTrace()
                } finally {
                    _indicatorState.postValue(IndicatorState.NORMAL)
                }
            }
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
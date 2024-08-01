package moe.wisteria.android.kimoji.ui.fragment.comicViewer

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.catch
import moe.wisteria.android.kimoji.entity.IndicatorState
import moe.wisteria.android.kimoji.entity.Order
import moe.wisteria.android.kimoji.repository.network.PicaRepository
import moe.wisteria.android.kimoji.util.launchIO

class ComicViewerModel : ViewModel() {

    private val _indicatorState: MutableLiveData<IndicatorState> = MutableLiveData(IndicatorState.NORMAL)
    val indicatorState: LiveData<IndicatorState>
        get() = _indicatorState

    private val _orderList: MutableLiveData<List<Order>> = MutableLiveData(listOf())
    val orderList: LiveData<List<Order>>
        get() = _orderList

    private val _currentVisiblePosition: MutableLiveData<Int> = MutableLiveData(0)
    val currentVisiblePosition: LiveData<Int>
        get() = _currentVisiblePosition

    private val _controlPanelVisible: MutableLiveData<Boolean> = MutableLiveData(false)
    val controlPanelVisible: LiveData<Boolean>
        get() = _controlPanelVisible

    object PageController {
        var currentPage: Int = 0
        var totalPage: Int = 999

        fun nextPage() = ++currentPage

        fun reset() {
            currentPage = 0
            totalPage = 999
        }

        fun set(
            currentPage: Int,
            totalPage: Int
        ) {
            this.currentPage = currentPage
            this.totalPage = totalPage
        }
    }

    fun loadOrder(
        token: String,
        comicId: String,
        order: String = "1",
        exceptionHandler: (Exception) -> Unit
    ): Boolean {
        if (PageController.currentPage >= PageController.totalPage) {
            return false
        }

        _indicatorState.postValue(IndicatorState.LOADING)

        launchIO {
            PicaRepository.Comics.order(
                token = token,
                comicId = comicId,
                order = order,
                page = PageController.nextPage()
            ).catch {
                _indicatorState.postValue(IndicatorState.NORMAL)

                exceptionHandler(it as Exception)
            }.collect {
                _indicatorState.postValue(IndicatorState.NORMAL)

                PageController.set(
                    currentPage = it.page,
                    totalPage = it.pages
                )

                _orderList.postValue(it.docs)
            }
        }

        return true
    }

    fun resetPageController() {
        PageController.reset()
    }

    fun setCurrentVisiblePosition(
        position: Int
    ) {
        _currentVisiblePosition.postValue(position)
    }

    fun setControlPanelVisible(
        visible: Boolean
    ) {
        _controlPanelVisible.postValue(visible)
    }
}
package moe.wisteria.android.kimoji.ui.fragment.index.page.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.catch
import moe.wisteria.android.kimoji.entity.BaseComic
import moe.wisteria.android.kimoji.entity.IndicatorState
import moe.wisteria.android.kimoji.repository.network.PicaRepository
import moe.wisteria.android.kimoji.util.launchIO
import kotlin.Exception

class HomeModel : ViewModel() {
    private val _indicatorState: MutableLiveData<IndicatorState> = MutableLiveData(IndicatorState.NORMAL)
    val indicatorState: LiveData<IndicatorState>
        get() = _indicatorState

    private val _comicList: MutableLiveData<List<BaseComic>> = MutableLiveData(listOf())
    val comicList: LiveData<List<BaseComic>>
        get() = _comicList

    fun loadComics(
        token: String,
        exceptionHandler: (Exception) -> Unit
    ) {
        _indicatorState.postValue(IndicatorState.LOADING)

        launchIO {
            PicaRepository.Comics.random(
                token = token
            ).catch {
                exceptionHandler(it as Exception)
            }.collect {
                _comicList.postValue(
                    _comicList.value!!.plus(it)
                )
            }

            _indicatorState.postValue(IndicatorState.NORMAL)
        }
    }
}
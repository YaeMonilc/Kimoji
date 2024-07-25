package moe.wisteria.android.kimoji.ui.fragment.index.page.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import moe.wisteria.android.kimoji.entity.BaseComic
import moe.wisteria.android.kimoji.entity.IndicatorState
import moe.wisteria.android.kimoji.network.entity.response.PicaResponse
import moe.wisteria.android.kimoji.network.entity.response.PicaResponse.Companion.onSuccess
import moe.wisteria.android.kimoji.network.picaApi
import moe.wisteria.android.kimoji.util.executeForPica
import moe.wisteria.android.kimoji.util.launchIO

class HomeModel : ViewModel() {
    private val _indicatorState: MutableLiveData<IndicatorState> = MutableLiveData(IndicatorState.NORMAL)
    val indicatorState: LiveData<IndicatorState>
        get() = _indicatorState

    private val _randomComicsResponse: MutableLiveData<PicaResponse<PicaResponse.RandomComics>> = MutableLiveData()
    val randomComicsResponse: LiveData<PicaResponse<PicaResponse.RandomComics>>
        get() = _randomComicsResponse

    private val _comicList: MutableLiveData<List<BaseComic>> = MutableLiveData(listOf())
    val comicList: LiveData<List<BaseComic>>
        get() = _comicList

    fun loadRandomComics(
        token: String
    ) {
        _indicatorState.postValue(IndicatorState.LOADING)

        launchIO {
            picaApi.randomComic(
                token = token
            ).executeForPica<PicaResponse.RandomComics>().also {
                _indicatorState.postValue(IndicatorState.NORMAL)
                _randomComicsResponse.postValue(it)

                it.onSuccess { randomComicsResponse ->
                    randomComicsResponse.data.comics.let { comics ->
                        _comicList.postValue(comicList.value!!.plus(comics))
                    }
                }
            }
        }
    }
}
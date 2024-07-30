package moe.wisteria.android.kimoji.ui.fragment.comicDetail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.catch
import moe.wisteria.android.kimoji.entity.Comic
import moe.wisteria.android.kimoji.entity.Episode
import moe.wisteria.android.kimoji.entity.IndicatorState
import moe.wisteria.android.kimoji.repository.network.PicaRepository
import moe.wisteria.android.kimoji.util.launchIO

class ComicDetailModel : ViewModel() {

    private val _indicatorState: MutableLiveData<IndicatorState> = MutableLiveData(IndicatorState.NORMAL)
    val indicatorState: LiveData<IndicatorState>
        get() = _indicatorState

    private val _comic: MutableLiveData<Comic> = MutableLiveData()
    val comic: LiveData<Comic>
        get() = _comic

    private val _episode: MutableLiveData<List<Episode>> = MutableLiveData(listOf())
    val episode: LiveData<List<Episode>>
        get() = _episode

    object PageController {
        var currentPage: Int = 0
        var totalPage: Int = -1

        fun nextPage() = ++currentPage

        fun reset() {
            currentPage = 0
            totalPage = -1
        }

        fun set(
            currentPage: Int,
            totalPage: Int
        ) {
            this.currentPage = currentPage
            this.totalPage = totalPage
        }
    }

    fun loadComicDetail(
        token: String,
        comicId: String,
        exceptionHandler: (Exception) -> Unit
    ) {
        _indicatorState.postValue(IndicatorState.LOADING)

        PageController.reset()

        launchIO {
            PicaRepository.Comics.detail(
                token = token,
                comicId = comicId
            ).catch {
                exceptionHandler(it as Exception)
            }.collect {
                _indicatorState.postValue(IndicatorState.NORMAL)

                _comic.postValue(it)

                loadEpisode(
                    token = token,
                    comicId = comicId,
                    exceptionHandler = exceptionHandler
                )
            }
        }
    }

    fun loadEpisode(
        token: String,
        comicId: String,
        exceptionHandler: (Exception) -> Unit
    ) {
        _indicatorState.postValue(IndicatorState.LOADING)

        launchIO {
            PicaRepository.Comics.episode(
                token = token,
                comicId = comicId,
                page = PageController.nextPage()
            ).catch {
                exceptionHandler(it as Exception)
            }.collect {
                _indicatorState.postValue(IndicatorState.NORMAL)

                PageController.set(
                    currentPage = it.page,
                    totalPage = it.pages
                )

                _episode.postValue(it.docs)
            }
        }
    }

    fun like(
        token: String,
        comicId: String,
        exceptionHandler: (Exception) -> Unit
    ) {
        _indicatorState.postValue(IndicatorState.LOADING)

        launchIO {
            PicaRepository.Comics.like(
                token = token,
                comicId = comicId
            ).catch {
                exceptionHandler(it as Exception)
            }.collect {
                _indicatorState.postValue(IndicatorState.NORMAL)
            }
        }
    }

    fun favourite(
        token: String,
        comicId: String,
        exceptionHandler: (Exception) -> Unit
    ) {
        _indicatorState.postValue(IndicatorState.LOADING)

        launchIO {
            PicaRepository.Comics.favourite(
                token = token,
                comicId = comicId
            ).catch {
                exceptionHandler(it as Exception)
            }.collect {
                _indicatorState.postValue(IndicatorState.NORMAL)
            }
        }
    }

}
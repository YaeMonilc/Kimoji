package moe.wisteria.android.kimoji.ui.fragment.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import moe.wisteria.android.kimoji.entity.SearchState
import moe.wisteria.android.kimoji.network.entity.body.SearchBody
import moe.wisteria.android.kimoji.network.entity.response.PicaResponse
import moe.wisteria.android.kimoji.network.entity.response.PicaResponse.Companion.onSuccess
import moe.wisteria.android.kimoji.network.picaApi
import moe.wisteria.android.kimoji.util.executeForPica
import moe.wisteria.android.kimoji.util.launchIO

class SearchModel : ViewModel() {
    private val _searchState: MutableLiveData<SearchState> = MutableLiveData(SearchState())
    val searchState: LiveData<SearchState>
        get() = _searchState

    private val _searchResponse: MutableLiveData<PicaResponse<PicaResponse.ComicList>> = MutableLiveData()
    val searchResponse: LiveData<PicaResponse<PicaResponse.ComicList>>
        get() = _searchResponse

    private val _searchContent: MutableLiveData<String> = MutableLiveData("")

    private object PageController {
        var currentPage: Int = 0
        var totalPage: Int = 0

        fun reset() {
            currentPage = 0
            totalPage = 0
        }

        fun nextPage(): Int = ++currentPage

        fun set(
            currentPage: Int,
            totalPage: Int
        ) {
            this.currentPage = currentPage
            this.totalPage = totalPage
        }
    }

    fun search(
        token: String
    ) {
        _searchState.postValue(
            _searchState.value!!.copy(
                state = SearchState.State.LOADING
            )
        )

        launchIO {
            picaApi.searchComic(
                token = token,
                body = SearchBody(
                    keyword = _searchContent.value!!
                ),
                page = PageController.nextPage()
            ).executeForPica<PicaResponse.ComicList>().also {
                _searchResponse.postValue(it)
            }.onSuccess {
                it.data.comics.let { comics ->
                    _searchState.value!!.comics.let { originList ->
                        _searchState.postValue(
                            SearchState(
                                state = if (originList.isEmpty() && comics.docs.isEmpty())
                                    SearchState.State.EMPTY
                                else
                                    SearchState.State.SUCCESS,
                                comics = originList.plus(comics.docs)
                            )
                        )
                    }

                    PageController.set(
                        currentPage = comics.page,
                        totalPage = comics.pages
                    )
                }
            }
        }
    }

    fun setSearchContent(
        string: String
    ) {
        _searchContent.postValue(string)
    }

    fun prepareReSearch() {
        PageController.reset()

        _searchState.postValue(
            SearchState(
                state = SearchState.State.WAIT,
                comics = listOf()
            )
        )
    }
}
package moe.wisteria.android.kimoji.ui.fragment.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.catch
import moe.wisteria.android.kimoji.entity.SearchState
import moe.wisteria.android.kimoji.entity.Sort
import moe.wisteria.android.kimoji.repository.network.PicaRepository
import moe.wisteria.android.kimoji.util.launchIO

class SearchModel : ViewModel() {
    private val _searchState: MutableLiveData<SearchState> = MutableLiveData(SearchState())
    val searchState: LiveData<SearchState>
        get() = _searchState

    private val _sort: MutableLiveData<Sort> = MutableLiveData(Sort.FAVORITE)
    val sort: LiveData<Sort>
        get() = _sort

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
        token: String,
        exceptionHandler: (Exception) -> Unit
    ) {
        _searchState.postValue(
            _searchState.value!!.copy(
                state = SearchState.State.LOADING
            )
        )

        launchIO {
            PicaRepository.Comics.search(
                token = token,
                keyword = _searchContent.value!!,
                page = PageController.nextPage(),
                sort = sort.value!!.string
            ).catch {
                _searchState.postValue(
                    _searchState.value!!.copy(
                        state = SearchState.State.WAIT,
                    )
                )
                exceptionHandler(it as Exception)
            }.collect {
                _searchState.value!!.comics.let { originComics ->
                    _searchState.postValue(
                        _searchState.value!!.copy(
                            state = if (it.docs.isEmpty())
                                SearchState.State.EMPTY
                            else
                                SearchState.State.SUCCESS,
                            comics = originComics.plus(it.docs)
                        )
                    )
                }
            }
        }
    }

    fun setSort(
        sort: Sort
    ) {
        _sort.postValue(sort)
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
package moe.wisteria.android.kimoji.ui.fragment.search

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.datastore.preferences.core.edit
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.OnScrollListener
import moe.wisteria.android.kimoji.databinding.FragmentSearchBinding
import moe.wisteria.android.kimoji.entity.SearchState
import moe.wisteria.android.kimoji.network.entity.response.PicaResponse.Companion.onError
import moe.wisteria.android.kimoji.network.entity.response.PicaResponse.Companion.onException
import moe.wisteria.android.kimoji.ui.adapter.ColumnComicAdapter
import moe.wisteria.android.kimoji.ui.view.BaseFragment
import moe.wisteria.android.kimoji.util.PreferenceKeys
import moe.wisteria.android.kimoji.util.getLocalization
import moe.wisteria.android.kimoji.util.launchIO
import moe.wisteria.android.kimoji.util.userDatastore

class SearchFragment : BaseFragment(
    toolBarOption = ToolBarOption(
        display = false
    )
) {
    private lateinit var binding: FragmentSearchBinding
    private val viewModel: SearchModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return FragmentSearchBinding.inflate(inflater, container, false).apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = this@SearchFragment.viewModel
        }.also {
            binding = it
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        viewModel.let { viewModel ->
            binding.fragmentSearchSearchView.apply {
                toolbar.setNavigationOnClickListener {
                    findNavController().popBackStack()
                }
                show()

                editText.apply {
                    setOnEditorActionListener { _, i, _ ->
                        if (i == EditorInfo.IME_ACTION_SEARCH) {
                            viewModel.setSearchContent(text.toString())
                            viewModel.prepareReSearch()
                            search()

                            (requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
                                .hideSoftInputFromWindow(windowToken, InputMethodManager.HIDE_NOT_ALWAYS)

                            return@setOnEditorActionListener true
                        }
                        false
                    }
                }
            }

            binding.fragmentSearchComicList.apply {
                adapter = ColumnComicAdapter(
                    context = requireContext(),
                    itemOnClickListener = {

                    },
                    comicList = viewModel.searchState.value?.comics ?: listOf()
                ).apply {
                    stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
                }
                layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

                addOnScrollListener(object : OnScrollListener() {
                    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                        super.onScrolled(recyclerView, dx, dy)

                        if (recyclerView.computeVerticalScrollOffset() > 0 &&!recyclerView.canScrollVertically(1)
                            && viewModel.searchState.value?.state != SearchState.State.LOADING) {
                            search()
                        }
                    }
                })

                setItemViewCacheSize(50)
            }

            viewModel.searchResponse.observe(viewLifecycleOwner) {
                it.onError { errorResponse ->
                    showSnackBar(getLocalization(errorResponse.error))
                }.onException { exception ->
                    showSnackBar(exception.stackTraceToString())
                }
            }

            viewModel.searchState.observe(viewLifecycleOwner) {
                (binding.fragmentSearchComicList.adapter as ColumnComicAdapter).let { columnComicAdapter ->
                    when (it.state) {
                        SearchState.State.WAIT -> columnComicAdapter.removeAll()
                        SearchState.State.SUCCESS -> columnComicAdapter.insertComics(*it.comics.takeLast(20).toTypedArray())
                        else -> {}
                    }
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
    }

    private fun search() {
        launchIO {
            requireContext().userDatastore.edit {
                it[PreferenceKeys.USER.TOKEN]?.let { token ->
                    viewModel.search(
                        token = token
                    )
                }
            }
        }
    }
}
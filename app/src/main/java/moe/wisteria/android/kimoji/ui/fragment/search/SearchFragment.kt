package moe.wisteria.android.kimoji.ui.fragment.search

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import moe.wisteria.android.kimoji.R
import moe.wisteria.android.kimoji.databinding.FragmentSearchBinding
import moe.wisteria.android.kimoji.entity.SearchState
import moe.wisteria.android.kimoji.entity.Sort
import moe.wisteria.android.kimoji.ui.adapter.ColumnComicAdapter
import moe.wisteria.android.kimoji.ui.view.BaseFragment
import moe.wisteria.android.kimoji.util.PreferenceKeys
import moe.wisteria.android.kimoji.util.launchIO
import moe.wisteria.android.kimoji.util.picaExceptionHandler
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
                    addTextChangedListener(object : TextWatcher {
                        override fun beforeTextChanged(
                            s: CharSequence?,
                            start: Int,
                            count: Int,
                            after: Int
                        ) {

                        }

                        override fun onTextChanged(
                            s: CharSequence?,
                            start: Int,
                            before: Int,
                            count: Int
                        ) {
                            viewModel.setSearchContent(s.toString())
                        }

                        override fun afterTextChanged(s: Editable?) {
                        }
                    })

                    setOnEditorActionListener { _, i, _ ->
                        if (i == EditorInfo.IME_ACTION_SEARCH) {
                            prepareReSearch()
                            search()

                            (requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
                                .hideSoftInputFromWindow(windowToken, InputMethodManager.HIDE_NOT_ALWAYS)

                            return@setOnEditorActionListener true
                        }
                        false
                    }
                }
            }

            binding.fragmentSearchFilter.setOnClickListener {
                showFilterDialog()
            }

            binding.fragmentSearchComicList.apply {
                adapter = ColumnComicAdapter(
                    context = requireContext(),
                    itemOnClickListener = {
                        findNavController().navigate(
                            SearchFragmentDirections.actionToComicDetailFragment(
                                it.id
                            )
                        )
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

            viewModel.searchState.observe(viewLifecycleOwner) {
                (binding.fragmentSearchComicList.adapter as ColumnComicAdapter).let { columnComicAdapter ->
                    when (it.state) {
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
                    ) { exception ->
                        picaExceptionHandler(exception)
                    }
                }
            }
        }
    }

    private fun prepareReSearch() {
        viewModel.prepareReSearch()
        (binding.fragmentSearchComicList.adapter as ColumnComicAdapter).removeAll()
    }

    private fun showFilterDialog() {
        requireContext().let { context ->
            MaterialAlertDialogBuilder(context).apply {
                val item = listOf(
                    getString(R.string.dialog_sort_selector_radio_new),
                    getString(R.string.dialog_sort_selector_radio_old),
                    getString(R.string.dialog_sort_selector_radio_liked),
                    getString(R.string.dialog_sort_selector_radio_favorite)
                )

                var select: Int = viewModel.sort.value!!.ordinal

                setTitle(R.string.dialog_sort_selector_title)

                setSingleChoiceItems(item.toTypedArray(), select
                ) { _, which ->
                    select = which
                }

                setNegativeButton(R.string.dialog_sort_selector_radio_cancel
                ) { _, _ -> }

                setPositiveButton(R.string.dialog_sort_selector_radio_ok
                ) { _, _ ->
                    viewModel.setSort(Sort.getByOrdinal(select))
                    prepareReSearch()
                    search()
                }
            }.show()
        }
    }
}
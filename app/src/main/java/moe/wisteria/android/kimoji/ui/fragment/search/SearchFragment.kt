package moe.wisteria.android.kimoji.ui.fragment.search

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.datastore.preferences.core.edit
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import moe.wisteria.android.kimoji.databinding.FragmentSearchBinding
import moe.wisteria.android.kimoji.entity.SearchState
import moe.wisteria.android.kimoji.ui.adapter.ColumnComicAdapter
import moe.wisteria.android.kimoji.ui.view.BaseFragment
import moe.wisteria.android.kimoji.util.PreferenceKeys
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
            }

            binding.fragmentSearchSearchView.editText.addTextChangedListener(object : TextWatcher {
                private var time: Long = System.currentTimeMillis()
                private var job: Job? = null

                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    if (p0?.isNotBlank() == true) {
                        if (job != null && time + 10000 >= System.currentTimeMillis()) {
                            job?.cancel()
                        }

                        job = launchIO {
                            time = System.currentTimeMillis()
                            viewModel.setSearchContent(p0.toString())

                            delay(300)
                            viewModel.prepareReSearch()
                            search()
                        }
                    } else {
                        viewModel.prepareReSearch()
                    }
                }

                override fun afterTextChanged(p0: Editable?) {}
            })
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

            setItemViewCacheSize(50)
        }

        viewModel.searchState.observe(viewLifecycleOwner) {
            (binding.fragmentSearchComicList.adapter as ColumnComicAdapter).let { columnComicAdapter ->
                when (it.state) {
                    SearchState.State.WAIT -> columnComicAdapter.removeAll()
                    SearchState.State.SUCCESS -> columnComicAdapter.insertComics(*it.comics.toTypedArray())
                    else -> {}
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
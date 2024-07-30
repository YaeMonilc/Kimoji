package moe.wisteria.android.kimoji.ui.fragment.index.page.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.datastore.preferences.core.edit
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import moe.wisteria.android.kimoji.R
import moe.wisteria.android.kimoji.databinding.FragmentHomeBinding
import moe.wisteria.android.kimoji.entity.IndicatorState
import moe.wisteria.android.kimoji.ui.adapter.ColumnComicAdapter
import moe.wisteria.android.kimoji.ui.fragment.index.IndexFragmentDirections
import moe.wisteria.android.kimoji.ui.view.BaseFragment
import moe.wisteria.android.kimoji.util.PreferenceKeys
import moe.wisteria.android.kimoji.util.launchIO
import moe.wisteria.android.kimoji.util.picaExceptionHandler
import moe.wisteria.android.kimoji.util.userDatastore

class HomeFragment : BaseFragment(
    toolBarOption = ToolBarOption(
        display = false
    )
) {
    private var view: View? = null
    private lateinit var binding: FragmentHomeBinding
    private val viewModel: HomeModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        loadComics()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return (view ?: FragmentHomeBinding.inflate(inflater, container, false).also {
            binding = it
            view = binding.root
        }.root).also {
            binding.lifecycleOwner = viewLifecycleOwner
            binding.viewModel = viewModel
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.let { viewModel ->
            binding.fragmentHomeSearchBar.setOnClickListener {
                Navigation.findNavController(requireActivity(), R.id.activity_main_fragment_container).navigate(
                    IndexFragmentDirections.actionToSearchFragment()
                )
            }

            binding.fragmentHomeComicList.apply {
                adapter = ColumnComicAdapter(
                    context = context,
                    itemOnClickListener = {
                        Navigation.findNavController(requireActivity(), R.id.activity_main_fragment_container).navigate(
                            IndexFragmentDirections.actionToComicDetailFragment(
                                it.id
                            )
                        )
                    },
                    comicList = viewModel.comicList.value ?: listOf()
                ).apply {
                    stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
                }
                layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

                setItemViewCacheSize(50)

                addOnScrollListener(object : RecyclerView.OnScrollListener() {
                    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                        super.onScrolled(recyclerView, dx, dy)

                        if (recyclerView.computeVerticalScrollOffset() > 0 &&!recyclerView.canScrollVertically(1)
                            && viewModel.indicatorState.value != IndicatorState.LOADING) {
                            loadComics()
                        }
                    }
                })
            }

            viewModel.comicList.observe(viewLifecycleOwner) {
                (binding.fragmentHomeComicList.adapter as ColumnComicAdapter).insertComics(
                    *it.takeLast(20).toTypedArray()
                )
            }
        }
    }

    override fun onStart() {
        super.onStart()
    }

    private fun loadComics() {
        launchIO {
            requireContext().userDatastore.edit {
                it[PreferenceKeys.USER.TOKEN]?.let { token ->
                    viewModel.loadComics(
                        token = token
                    ) { exception ->
                        picaExceptionHandler(
                            exception = exception,
                            view = binding.fragmentHomeComicList
                        )
                    }
                }
            }
        }
    }
}
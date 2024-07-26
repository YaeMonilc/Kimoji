package moe.wisteria.android.kimoji.ui.fragment.index.page.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.datastore.preferences.core.edit
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import moe.wisteria.android.kimoji.R
import moe.wisteria.android.kimoji.databinding.FragmentHomeBinding
import moe.wisteria.android.kimoji.network.entity.response.PicaResponse.Companion.onError
import moe.wisteria.android.kimoji.network.entity.response.PicaResponse.Companion.onException
import moe.wisteria.android.kimoji.ui.adapter.ColumnComicAdapter
import moe.wisteria.android.kimoji.ui.view.BaseFragment
import moe.wisteria.android.kimoji.util.PreferenceKeys
import moe.wisteria.android.kimoji.util.getLocalization
import moe.wisteria.android.kimoji.util.launchIO
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

        loadRandomComics()
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
                    R.id.action_indexFragment_to_searchFragment
                )
            }

            binding.fragmentHomeComicList.apply {
                adapter = ColumnComicAdapter(
                    context = context,
                    itemOnClickListener = {
                        showSnackBar(it.author, binding.fragmentHomeComicList)
                    },
                    comicList = viewModel.comicList.value ?: listOf()
                ).apply {
                    stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
                }
                layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false).apply {
                    setItemViewCacheSize(50)
                }
            }

            viewModel.randomComicsResponse.observe(viewLifecycleOwner) {
                it.onError { errorResponse ->
                    showSnackBar(getLocalization(errorResponse.error))
                }.onException { exception ->
                    showSnackBar(exception.stackTraceToString())
                }
            }

            viewModel.comicList.observe(viewLifecycleOwner) {
                (binding.fragmentHomeComicList.adapter as ColumnComicAdapter).insertComics(*it.toTypedArray())
            }
        }
    }

    override fun onStart() {
        super.onStart()
    }

    private fun loadRandomComics() {
        launchIO {
            requireContext().userDatastore.edit {
                it[PreferenceKeys.USER.TOKEN]?.let { token ->
                    viewModel.loadRandomComics(
                        token = token
                    )
                }
            }
        }
    }
}
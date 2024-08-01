package moe.wisteria.android.kimoji.ui.fragment.comicViewer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.datastore.preferences.core.edit
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.OnScrollListener
import moe.wisteria.android.kimoji.databinding.FragmentComicViewerBinding
import moe.wisteria.android.kimoji.entity.IndicatorState
import moe.wisteria.android.kimoji.ui.adapter.ComicImageAdapter
import moe.wisteria.android.kimoji.ui.view.BaseFragment
import moe.wisteria.android.kimoji.util.PreferenceKeys
import moe.wisteria.android.kimoji.util.launchIO
import moe.wisteria.android.kimoji.util.picaExceptionHandler
import moe.wisteria.android.kimoji.util.userDatastore

class ComicViewerFragment : BaseFragment(
    toolBarOption = ToolBarOption(
        title = null,
        display = false
    )
) {
    private lateinit var binding: FragmentComicViewerBinding
    private val viewModel: ComicViewerModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.resetPageController()

        loadOrders()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return FragmentComicViewerBinding.inflate(inflater, container, false).apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = this@ComicViewerFragment.viewModel
        }.also {
            binding = it
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.let { viewModel ->
            viewModel.orderList.observe(viewLifecycleOwner) {
                (binding.fragmentComicViewerImageList.adapter as ComicImageAdapter).let { adapter ->
                    adapter.insertOrders(*it.toTypedArray())

                    binding.fragmentComicViewerImagePageSlider.valueTo = (if (adapter.itemCount == 0) 1 else adapter.itemCount - 1).toFloat()
                }
            }

            viewModel.currentVisiblePosition.observe(viewLifecycleOwner) {
                binding.fragmentComicViewerImagePageSlider.value = it.toFloat()
            }

            binding.fragmentComicViewerImagePageSlider.addOnChangeListener { _, value, fromUser ->
                if (fromUser) {
                    binding.fragmentComicViewerImageList.scrollToPosition(value.toInt())
                }
            }

            binding.fragmentComicViewerImageList.apply {
                adapter = ComicImageAdapter(
                    context = requireContext(),
                    onClickListener = {
                        viewModel.setControlPanelVisible(false)
                    },
                    onLongClickListener = {
                        viewModel.setControlPanelVisible(true)
                    },
                    orderList = viewModel.orderList.value ?: listOf()
                )
                layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

                addOnScrollListener(object : OnScrollListener() {
                    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                        super.onScrolled(recyclerView, dx, dy)

                        (recyclerView.layoutManager as LinearLayoutManager).let { layoutManager ->
                            val currentVisiblePosition = layoutManager.findFirstCompletelyVisibleItemPosition()

                            if (currentVisiblePosition != -1 && currentVisiblePosition != viewModel.currentVisiblePosition.value) {
                                viewModel.setCurrentVisiblePosition(currentVisiblePosition)
                            }
                        }

                        if (recyclerView.computeVerticalScrollOffset() > 0 && !recyclerView.canScrollVertically(1)
                            && viewModel.indicatorState.value!! != IndicatorState.LOADING) {
                            loadOrders()
                        }
                    }
                })

                setItemViewCacheSize(50)
            }
        }
    }

    override fun onStart() {
        super.onStart()
    }

    private fun loadOrders(
        order: String? = null
    ) {
        launchIO {
            arguments?.let { bundle ->
                requireContext().userDatastore.edit {
                    it[PreferenceKeys.USER.TOKEN]?.let { token ->
                        ComicViewerFragmentArgs.fromBundle(bundle).let { arguments ->
                            viewModel.loadOrder(
                                token = token,
                                comicId = arguments.comicId,
                                order = order ?: arguments.order
                            ) { exception ->
                                picaExceptionHandler(
                                    exception = exception
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
package moe.wisteria.android.kimoji.ui.fragment.comicDetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.MenuProvider
import androidx.datastore.preferences.core.edit
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.AlignItems
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import moe.wisteria.android.kimoji.R
import moe.wisteria.android.kimoji.databinding.FragmentComicDetailBinding
import moe.wisteria.android.kimoji.ui.adapter.EpisodeAdapter
import moe.wisteria.android.kimoji.ui.adapter.LabelAdapter
import moe.wisteria.android.kimoji.ui.view.BaseFragment
import moe.wisteria.android.kimoji.util.PreferenceKeys
import moe.wisteria.android.kimoji.util.launchIO
import moe.wisteria.android.kimoji.util.loadImage
import moe.wisteria.android.kimoji.util.picaExceptionHandler
import moe.wisteria.android.kimoji.util.userDatastore

class ComicDetailFragment : BaseFragment(
    toolBarOption = ToolBarOption(
        title = R.string.fragment_comic_detail_title,
        displayHomeAsUpEnabled = true
    )
) {
    private var view: View? = null
    private lateinit var binding: FragmentComicDetailBinding
    private val viewModel: ComicDetailModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (viewModel.comic.value == null)
            loadComicDetail()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return  (view ?: FragmentComicDetailBinding.inflate(inflater, container, false).also {
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
            viewModel.comic.observe(viewLifecycleOwner) {
                it.thumb.let { media ->
                    binding.fragmentComicDetailThumb.loadImage(
                        data = "${ media.fileServer }/static/${ media.path }"
                    )
                }

                it.creator.avatar?.let { media ->
                    binding.fragmentComicDetailCreatorProfileImage.loadImage(
                        data = "${ media.fileServer }/static/${ media.path }"
                    )
                }

                (binding.fragmentComicDetailCategory.adapter as LabelAdapter).replaceLabels(*it.categories.toTypedArray())
                (binding.fragmentComicTag.adapter as LabelAdapter).replaceLabels(*it.tags.toTypedArray())
            }

            viewModel.episode.observe(viewLifecycleOwner) {
                ComicDetailModel.PageController.let { pageController ->
                    binding.fragmentComicDetailEpisodeLoadMore.visibility =
                        if (pageController.currentPage >= pageController.totalPage)
                            View.GONE
                        else
                            View.VISIBLE
                }

                (binding.fragmentComicDetailEpisodeList.adapter as EpisodeAdapter).insertEpisodes(*it.toTypedArray())
            }

            binding.fragmentComicDetailLiked.setOnCheckedChangeListener { _, _ ->
                like()
            }

            binding.fragmentComicDetailFavorite.setOnCheckedChangeListener { _, _ ->
                favourite()
            }

            binding.fragmentComicDetailRead.setOnClickListener {
                (binding.fragmentComicDetailEpisodeList.adapter as EpisodeAdapter).getFirstEpisode()?.let { episode ->
                    read(episode.order)
                }
            }

            binding.fragmentComicDetailCategory.apply {
                adapter = LabelAdapter(
                    context = requireContext(),
                    labelList = viewModel.comic.value?.categories ?: listOf()
                )
                layoutManager = object : FlexboxLayoutManager(requireContext()) {
                    override fun canScrollVertically(): Boolean {
                        return false
                    }
                }
            }

            binding.fragmentComicTag.apply {
                adapter = LabelAdapter(
                    context = requireContext(),
                    labelList = viewModel.comic.value?.tags ?: listOf()
                )
                layoutManager = object : FlexboxLayoutManager(requireContext()) {
                    override fun canScrollVertically(): Boolean {
                        return false
                    }
                }
            }

            binding.fragmentComicDetailEpisodeList.apply {
                adapter = EpisodeAdapter(
                    context = requireContext(),
                    onClickListener = {
                        read(it.order)
                    },
                    episodeList = viewModel.episode.value ?: listOf()
                ).apply {
                    stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
                }
                layoutManager = object : FlexboxLayoutManager(requireContext()) {
                    override fun canScrollVertically(): Boolean {
                        return false
                    }
                }.apply {
                    alignItems = AlignItems.FLEX_START
                    justifyContent = JustifyContent.SPACE_BETWEEN
                }


            }

            binding.fragmentComicDetailEpisodeLoadMore.setOnClickListener {
                loadEpisode()
            }
        }
    }

    override fun onStart() {
        super.onStart()
    }

    override fun getMenuProvider(): MenuProvider {
        return object : DefaultMenuProvider() {
            override fun onHomeAsUpClick(): Boolean {
                findNavController().popBackStack()
                return true
            }
        }
    }

    private fun loadComicDetail() {
        launchIO {
            arguments?.let { arguments ->
                requireContext().userDatastore.edit {
                    it[PreferenceKeys.USER.TOKEN]?.let { token ->
                        viewModel.loadComicDetail(
                            token = token,
                            comicId = ComicDetailFragmentArgs.fromBundle(arguments).comicId
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

    private fun loadEpisode() {
        launchIO {
            arguments?.let { arguments ->
                requireContext().userDatastore.edit {
                    it[PreferenceKeys.USER.TOKEN]?.let { token ->
                        viewModel.loadEpisode(
                            token = token,
                            comicId = ComicDetailFragmentArgs.fromBundle(arguments).comicId
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

    private fun like() {
        launchIO {
            arguments?.let { arguments ->
                requireContext().userDatastore.edit {
                    it[PreferenceKeys.USER.TOKEN]?.let { token ->
                        viewModel.like(
                            token = token,
                            comicId = ComicDetailFragmentArgs.fromBundle(arguments).comicId
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

    private fun favourite() {
        launchIO {
            arguments?.let { arguments ->
                requireContext().userDatastore.edit {
                    it[PreferenceKeys.USER.TOKEN]?.let { token ->
                        viewModel.favourite(
                            token = token,
                            comicId = ComicDetailFragmentArgs.fromBundle(arguments).comicId
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

    private fun read(
        order: String
    ) {
        arguments?.let { arguments ->
            findNavController().navigate(
                ComicDetailFragmentDirections.actionToComicViewerFragment(
                    ComicDetailFragmentArgs.fromBundle(arguments).comicId,
                    order
                )
            )
        }
    }
}
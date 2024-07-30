package moe.wisteria.android.kimoji.ui.fragment.comicDetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.view.MenuProvider
import androidx.datastore.preferences.core.edit
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.flexbox.AlignItems
import com.google.android.flexbox.FlexboxItemDecoration
import com.google.android.flexbox.FlexboxLayout
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import moe.wisteria.android.kimoji.R
import moe.wisteria.android.kimoji.databinding.FragmentComicDetailBinding
import moe.wisteria.android.kimoji.entity.Episode
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
    private lateinit var binding: FragmentComicDetailBinding
    private val viewModel: ComicDetailModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        loadComicDetail()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return FragmentComicDetailBinding.inflate(inflater, container, false).apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = this@ComicDetailFragment.viewModel
        }.also {
            binding = it
        }.root
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

                (binding.fragmentComicDetailEpisodeList.adapter as EpisodeAdapter).let { adapter ->
                    (binding.fragmentComicDetailEpisodeList.layoutManager as FlexboxLayoutManager).let { layoutManger ->
                        if (adapter.itemCount == 2) {
                            layoutManger.alignItems = AlignItems.FLEX_START
                        } else {
                            layoutManger.alignItems = AlignItems.STRETCH
                        }
                    }

                    adapter.insertEpisodes(*it.toTypedArray())
                }
            }

            binding.fragmentComicDetailLiked.setOnCheckedChangeListener { _, _ ->
                like()
            }

            binding.fragmentComicDetailFavorite.setOnCheckedChangeListener { _, _ ->
                favourite()
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

                    },
                    episodeList = viewModel.episode.value ?: listOf()
                )
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

    }
}
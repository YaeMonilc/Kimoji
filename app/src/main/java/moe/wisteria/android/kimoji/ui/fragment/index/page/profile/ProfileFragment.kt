package moe.wisteria.android.kimoji.ui.fragment.index.page.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.view.MenuProvider
import androidx.datastore.preferences.core.edit
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import moe.wisteria.android.kimoji.R
import moe.wisteria.android.kimoji.databinding.FragmentProfileBinding
import moe.wisteria.android.kimoji.ui.adapter.LabelAdapter
import moe.wisteria.android.kimoji.ui.view.BaseFragment
import moe.wisteria.android.kimoji.util.PreferenceKeys
import moe.wisteria.android.kimoji.util.launchIO
import moe.wisteria.android.kimoji.util.loadImage
import moe.wisteria.android.kimoji.util.picaExceptionHandler
import moe.wisteria.android.kimoji.util.userDatastore

class ProfileFragment : BaseFragment(
    toolBarOption = ToolBarOption(
        title = R.string.fragment_profile_title
    )
) {
    private var view: View? = null
    private lateinit var binding: FragmentProfileBinding
    private val viewModel: ProfileModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        loadProfile()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return (view ?: FragmentProfileBinding.inflate(inflater, container, false).also {
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
            viewModel.profile.observe(viewLifecycleOwner) {
                it.avatar?.let { avatar ->
                    binding.fragmentProfileAvatar.loadImage(
                        data = "${ avatar.fileServer }/static/${ avatar.path }"
                    )
                }

                binding.fragmentProfileCharacters.apply {
                    adapter = LabelAdapter(
                        context = requireContext(),
                        labelList = it.characters
                    )
                    layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
    }

    override fun getMenuProvider(): MenuProvider {
        return object : DefaultMenuProvider(
            menuRes = R.menu.menu_fragment_profile_toolbar
        ) {
            override fun onMenuItemClick(menuItem: MenuItem): Boolean {
                when (menuItem.itemId) {
                    R.id.menu_action_punch_in -> punchIn()
                    else -> {}
                }

                return true
            }
        }
    }

    private fun loadProfile() {
        launchIO {
            requireContext().userDatastore.edit {
                it[PreferenceKeys.USER.TOKEN]?.let { token ->
                    viewModel.loadProfile(
                        token = token,
                        exceptionHandler = { exception ->
                            picaExceptionHandler(exception)
                        }
                    )
                }
            }
        }
    }

    private fun punchIn() {
        launchIO {
            requireContext().userDatastore.edit {
                it[PreferenceKeys.USER.TOKEN]?.let { token ->
                    viewModel.punchIn(
                        token = token,
                        exceptionHandler = { exception ->
                            picaExceptionHandler(exception)
                        }
                    ).collect { result ->
                        if (result.status == "ok")
                            showSnackBar(getString(R.string.network_punch_in_success, result.punchInLastDay))
                        else
                            showSnackBar(R.string.network_punch_in_failed)
                    }
                }
            }
        }
    }
}
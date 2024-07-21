package moe.wisteria.android.kimoji.ui.fragment.channelSelector

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.datastore.preferences.core.edit
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.launch
import moe.wisteria.android.kimoji.R
import moe.wisteria.android.kimoji.databinding.FragmentChannelSelectorBinding
import moe.wisteria.android.kimoji.network.setDefaultOkhttpClientDns
import moe.wisteria.android.kimoji.ui.adapter.ChannelListAdapter
import moe.wisteria.android.kimoji.ui.view.BaseFragment
import moe.wisteria.android.kimoji.util.IO
import moe.wisteria.android.kimoji.util.PreferenceKeys
import moe.wisteria.android.kimoji.util.appDatastore

class ChannelSelectorFragment : BaseFragment(
    toolBarOption = ToolBarOption(
        title = R.string.fragment_channel_selector_title
    )
) {
    private lateinit var binding: FragmentChannelSelectorBinding
    private val viewModel: ChannelSelectorModel by viewModels()

    companion object {
        const val NAVIGATION_PARAM_NAVIGATE_POSITION = "NAVIGATION_PARAM_NAVIGATE_POSITION"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.let { model ->

            arguments?.getInt(NAVIGATION_PARAM_NAVIGATE_POSITION)?.let {
                model.setNavigatePosition(
                    navigatePosition = ChannelSelectorModel.NavigatePosition.getByValue(it)
                )
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)

        return FragmentChannelSelectorBinding.inflate(inflater, container, false).apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = this@ChannelSelectorFragment.viewModel
        }.also {
            binding = it
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.let { viewModel ->
            binding.fragmentChannelSelectorList.apply {
                adapter = ChannelListAdapter(
                    context = requireContext(),
                    channelList = viewModel.channelList.value!!,
                    itemOnClickListener = {
                        onChannelSelected(it)
                    }
                )
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            }

            binding.fragmentChannelSelectorDefault.setOnClickListener {
                onChannelSelected(null)
            }

            viewModel.channelList.observe(viewLifecycleOwner) {
                binding.fragmentChannelSelectorList.apply {
                    adapter.let { adapter ->
                        (adapter as ChannelListAdapter).noticeChange(it)
                    }
                }
            }

            viewModel.navigatePosition.observe(viewLifecycleOwner) {
                setDisplayHomeAsUp(it == ChannelSelectorModel.NavigatePosition.BACK)
            }
        }
    }

    override fun onStart() {
        super.onStart()
    }

    private fun onChannelSelected(
        channel: String?
    ) {
        setDefaultOkhttpClientDns(channel)

        lifecycleScope.launch(IO) {
            requireContext().appDatastore.edit {
                it[PreferenceKeys.APP.SELECTED_CHANNEL] = channel ?: ""
            }
        }

        findNavController().let { navController ->
            when (viewModel.navigatePosition.value!!) {
                ChannelSelectorModel.NavigatePosition.SIGN_IN -> navController.navigate(R.id.action_channelSelectorFragment_to_signInFragment)
                ChannelSelectorModel.NavigatePosition.BACK -> navController.popBackStack()
            }
        }
    }
}
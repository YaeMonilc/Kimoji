package moe.wisteria.android.ui.fragment.channelSelector

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import moe.wisteria.android.R
import moe.wisteria.android.databinding.FragmentChannelSelectorBinding
import moe.wisteria.android.ui.adapter.ChannelListAdapter
import moe.wisteria.android.ui.view.BaseFragment

class ChannelSelectorFragment : BaseFragment(
    toolBarOption = ToolBarOption(
        title = R.string.fragment_channel_selector_title
    )
) {
    private lateinit var binding: FragmentChannelSelectorBinding
    private val viewModel: ChannelSelectorModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.loadChannelList()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return FragmentChannelSelectorBinding.inflate(inflater, container, false).also {
            binding = it
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.let { model ->
            binding.fragmentChannelSelectorList.apply {
                adapter = ChannelListAdapter(
                    context = requireContext(),
                    channelList = model.channelList.value!!,
                    itemOnClickListener = {
                        Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                    }
                )
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            }

            model.state.observe(viewLifecycleOwner) {
                binding.fragmentChannelSelectorIndicator.visibility = when (it) {
                    ChannelSelectorModel.State.LOADING -> View.VISIBLE
                    else -> View.GONE
                }
            }

            model.channelList.observe(viewLifecycleOwner) {
                binding.fragmentChannelSelectorList.apply {
                    (adapter as ChannelListAdapter).noticeChange(it)
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
    }
}
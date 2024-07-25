package moe.wisteria.android.kimoji.ui.fragment.index.page.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import moe.wisteria.android.kimoji.databinding.FragmentHomeBinding
import moe.wisteria.android.kimoji.ui.view.BaseFragment

class HomeFragment : BaseFragment(
    toolBarOption = ToolBarOption(
        title = null
    )
) {
    private lateinit var binding: FragmentHomeBinding
    private val viewModel: HomeModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return FragmentHomeBinding.inflate(inflater, container, false).apply {
            lifecycleOwner = viewLifecycleOwner
        }.also {
            binding = it
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onStart() {
        super.onStart()
    }
}
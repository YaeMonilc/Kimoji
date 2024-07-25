package moe.wisteria.android.kimoji.ui.fragment.index

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import moe.wisteria.android.kimoji.databinding.FragmentIndexBinding
import moe.wisteria.android.kimoji.ui.view.BaseFragment

class IndexFragment : BaseFragment(
    toolBarOption = ToolBarOption(
        title = null,
        display = false
    )
) {
    private lateinit var binding: FragmentIndexBinding
    private val viewModel: IndexModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return FragmentIndexBinding.inflate(inflater, container, false).apply {
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

        NavigationUI.setupWithNavController(
            navigationBarView = binding.fragmentIndexBottomNavigation,
            navController = Navigation.findNavController(binding.fragmentIndexFragmentContainer)
        )
    }
}
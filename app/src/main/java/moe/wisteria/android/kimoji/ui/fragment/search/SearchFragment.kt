package moe.wisteria.android.kimoji.ui.fragment.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import moe.wisteria.android.kimoji.databinding.FragmentSearchBinding
import moe.wisteria.android.kimoji.ui.view.BaseFragment

class SearchFragment : BaseFragment(
    toolBarOption = ToolBarOption(
        display = false
    )
) {
    private lateinit var binding: FragmentSearchBinding
    private val viewModel: SearchModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return FragmentSearchBinding.inflate(inflater, container, false).apply {
            lifecycleOwner = viewLifecycleOwner
        }.also {
            binding = it
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        binding.fragmentSearchSearchView.apply {
            toolbar.setNavigationOnClickListener {
                findNavController().popBackStack()
            }
            show()
        }
    }

    override fun onStart() {
        super.onStart()
    }
}
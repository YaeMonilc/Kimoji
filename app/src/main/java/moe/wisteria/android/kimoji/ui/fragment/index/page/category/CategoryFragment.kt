package moe.wisteria.android.kimoji.ui.fragment.index.page.category

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import moe.wisteria.android.kimoji.R
import moe.wisteria.android.kimoji.databinding.FragmentCategoryBinding
import moe.wisteria.android.kimoji.ui.view.BaseFragment

class CategoryFragment : BaseFragment(
    toolBarOption = ToolBarOption(
        title = R.string.fragment_category_title
    )
) {
    private lateinit var binding: FragmentCategoryBinding
    private val viewModel: CategoryModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return FragmentCategoryBinding.inflate(inflater, container, false).apply {
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
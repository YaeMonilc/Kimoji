package moe.wisteria.android.kimoji.ui.fragment.index.page.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import moe.wisteria.android.kimoji.R
import moe.wisteria.android.kimoji.databinding.FragmentProfileBinding
import moe.wisteria.android.kimoji.ui.view.BaseFragment

class ProfileFragment : BaseFragment(
    toolBarOption = ToolBarOption(
        title = R.string.fragment_profile_title
    )
) {
    private lateinit var binding: FragmentProfileBinding
    private val viewModel: ProfileModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return FragmentProfileBinding.inflate(inflater, container, false).apply {
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
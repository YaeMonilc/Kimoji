package moe.wisteria.android.kimoji.ui.fragment.register

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.MenuProvider
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import moe.wisteria.android.kimoji.R
import moe.wisteria.android.kimoji.databinding.FragmentRegisterBinding
import moe.wisteria.android.kimoji.ui.view.BaseFragment
import moe.wisteria.android.kimoji.util.TextInputLayoutControllerList

class RegisterFragment : BaseFragment(
    toolBarOption = ToolBarOption(
        title = R.string.fragment_register_title,
        displayHomeAsUpEnabled = true
    )
) {
    private lateinit var binding: FragmentRegisterBinding
    private val viewModel: RegisterModel by viewModels()

    private lateinit var textInputLayoutControllerList: TextInputLayoutControllerList

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)

        return FragmentRegisterBinding.inflate(layoutInflater, container, false).apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = this@RegisterFragment.viewModel
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

    override fun getMenuProvider(): MenuProvider {
        return object : DefaultMenuProvider() {
            override fun onHomeAsUpClick(): Boolean {
                findNavController().popBackStack()
                return true
            }
        }
    }
}
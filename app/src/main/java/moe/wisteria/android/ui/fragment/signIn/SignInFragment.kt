package moe.wisteria.android.ui.fragment.signIn

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import moe.wisteria.android.R
import moe.wisteria.android.databinding.FragmentSignInBinding
import moe.wisteria.android.ui.view.BaseFragment
import moe.wisteria.android.util.TextInputLayoutControllerList

class SignInFragment : BaseFragment(
    toolBarOption = ToolBarOption(
        title = R.string.fragment_sign_in_title
    )
) {
    private lateinit var binding: FragmentSignInBinding
    private val viewModel: SignInModel by viewModels()

    private lateinit var textInputLayoutControllerList: TextInputLayoutControllerList

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return FragmentSignInBinding.inflate(inflater, container, false).apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = this@SignInFragment.viewModel
        }.also {
            binding = it
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        textInputLayoutControllerList = TextInputLayoutControllerList(
            context = requireContext()
        ).apply {
            addTextInputLayout(
                textInputLayout = binding.fragmentSignInEmail
            )
            addTextInputLayout(
                textInputLayout = binding.fragmentSignInPassword,
                conditionMap = mapOf(
                    R.string.fragment_sign_in_password_tip to {
                        (it?.length ?: 0) < 8
                    }
                )
            )
        }

        viewModel.let { model ->
            binding.fragmentSignInOk.setOnClickListener {
                if (textInputLayoutControllerList.checkAll()) {
                    Toast.makeText(requireContext(), "不合格", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
    }
}
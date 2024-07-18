package moe.wisteria.android.ui.fragment.signIn

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import moe.wisteria.android.R
import moe.wisteria.android.databinding.FragmentSignInBinding
import moe.wisteria.android.ui.view.BaseFragment

class SignInFragment : BaseFragment(
    toolBarOption = ToolBarOption(
        title = R.string.fragment_sign_in_title
    )
) {
    private lateinit var binding: FragmentSignInBinding
    private val viewModel: SignInModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return FragmentSignInBinding.inflate(inflater, container, false).also {
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
package moe.wisteria.android.ui.fragment.signIn

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.datastore.preferences.core.edit
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import moe.wisteria.android.R
import moe.wisteria.android.databinding.FragmentSignInBinding
import moe.wisteria.android.entity.NetworkState
import moe.wisteria.android.ui.view.BaseFragment
import moe.wisteria.android.util.IO
import moe.wisteria.android.util.PreferenceKeys
import moe.wisteria.android.util.TextInputLayoutControllerList
import moe.wisteria.android.util.userDatastore

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

        viewModel.let { model ->
            lifecycleScope.launch(IO) {
                requireContext().userDatastore.edit {
                    model.setEmail(it[PreferenceKeys.USER.EMAIL])
                    model.setPassword(it[PreferenceKeys.USER.PASSWORD])
                }
            }
        }
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
                    return@setOnClickListener
                }

                model.signIn()
            }

            model.networkState.observe(viewLifecycleOwner) {
                when (it.state) {
                    NetworkState.State.FAILED -> {
                        it.data?.let { stringRes ->
                            Snackbar.make(binding.root, stringRes, Snackbar.LENGTH_SHORT).show()
                        }
                    }
                    NetworkState.State.EXCEPTION -> {
                        it.exception?.message?.let { message ->
                            Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
                        }
                    }
                    else -> {}
                }
            }

            model.token.observe(viewLifecycleOwner) { token ->
                lifecycleScope.launch(IO) {
                    requireContext().let { context ->
                        context.userDatastore.edit {
                            it[PreferenceKeys.USER.EMAIL] = viewModel.email.value!!
                            it[PreferenceKeys.USER.PASSWORD] = viewModel.password.value!!
                            it[PreferenceKeys.USER.TOKEN] = token
                        }
                    }
                }

                findNavController().navigate(R.id.action_signInFragment_to_indexFragment)
            }
        }
    }

    override fun onStart() {
        super.onStart()
    }
}
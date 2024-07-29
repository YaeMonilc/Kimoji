package moe.wisteria.android.kimoji.ui.fragment.signIn

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.view.MenuProvider
import androidx.datastore.preferences.core.edit
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import moe.wisteria.android.kimoji.R
import moe.wisteria.android.kimoji.databinding.FragmentSignInBinding
import moe.wisteria.android.kimoji.ui.fragment.register.RegisterFragment
import moe.wisteria.android.kimoji.ui.view.BaseFragment
import moe.wisteria.android.kimoji.util.PreferenceKeys
import moe.wisteria.android.kimoji.util.TextInputLayoutControllerList
import moe.wisteria.android.kimoji.util.launchIO
import moe.wisteria.android.kimoji.util.launchUI
import moe.wisteria.android.kimoji.util.picaExceptionHandler
import moe.wisteria.android.kimoji.util.userDatastore

class SignInFragment : BaseFragment(
    toolBarOption = ToolBarOption(
        title = null
    )
) {
    private lateinit var binding: FragmentSignInBinding
    private val viewModel: SignInModel by viewModels()

    private lateinit var textInputLayoutControllerList: TextInputLayoutControllerList

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.let { model ->
            launchIO {
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
        super.onCreateView(inflater, container, savedInstanceState)

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

        viewModel.let { viewModel ->
            setFragmentResultListener(RegisterFragment.GET_REGISTER_RESULT) { _, bundle ->
                viewModel.setEmail(bundle.getString(RegisterFragment.GET_REGISTER_RESULT_EMAIL))
                viewModel.setPassword(bundle.getString(RegisterFragment.GET_REGISTER_RESULT_PASSWORD))
            }

            binding.fragmentSignInOk.setOnClickListener {
                if (!textInputLayoutControllerList.checkAll()) {
                    return@setOnClickListener
                }

                signIn()
            }
        }
    }

    override fun onStart() {
        super.onStart()
    }

    override fun getMenuProvider(): MenuProvider {
        return object : DefaultMenuProvider(
            menuRes = R.menu.menu_frgament_sign_in_toolbar
        ) {
            override fun onMenuItemClick(menuItem: MenuItem): Boolean {
                when (menuItem.itemId) {
                    R.id.menu_action_navigate_register -> {
                        findNavController().navigate(SignInFragmentDirections.actionSignInFragmentToRegisterFragment())
                    }
                    else -> {}
                }

                return true
            }
        }
    }

    private fun signIn() {
        launchIO {
            viewModel.signIn {
                picaExceptionHandler(it)
            }?.let { token ->
                requireContext().userDatastore.edit {
                    it[PreferenceKeys.USER.EMAIL] = viewModel.email.value!!
                    it[PreferenceKeys.USER.PASSWORD] = viewModel.password.value!!
                    it[PreferenceKeys.USER.TOKEN] = token
                }

                launchUI {
                    findNavController().navigate(SignInFragmentDirections.actionSignInFragmentToIndexFragment())
                }
            }
        }
    }
}
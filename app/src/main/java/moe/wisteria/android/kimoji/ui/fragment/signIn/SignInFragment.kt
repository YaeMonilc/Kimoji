package moe.wisteria.android.kimoji.ui.fragment.signIn

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.view.MenuProvider
import androidx.datastore.preferences.core.edit
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import moe.wisteria.android.kimoji.R
import moe.wisteria.android.kimoji.databinding.FragmentSignInBinding
import moe.wisteria.android.kimoji.network.entity.response.PicaResponse.Companion.onError
import moe.wisteria.android.kimoji.network.entity.response.PicaResponse.Companion.onException
import moe.wisteria.android.kimoji.network.entity.response.PicaResponse.Companion.onSuccess
import moe.wisteria.android.kimoji.ui.view.BaseFragment
import moe.wisteria.android.kimoji.util.IO
import moe.wisteria.android.kimoji.util.PreferenceKeys
import moe.wisteria.android.kimoji.util.TextInputLayoutControllerList
import moe.wisteria.android.kimoji.util.getLocalization
import moe.wisteria.android.kimoji.util.launchIO
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
            viewModel.signInResponse.observe(viewLifecycleOwner) {
                it.onSuccess { data ->
                    lifecycleScope.launch(IO) {
                        requireContext().userDatastore.edit { userDatastore ->
                            userDatastore[PreferenceKeys.USER.EMAIL] = viewModel.email.value!!
                            userDatastore[PreferenceKeys.USER.PASSWORD] = viewModel.password.value!!
                            userDatastore[PreferenceKeys.USER.TOKEN] = data.data.token
                        }
                    }

                    findNavController().navigate(R.id.action_signInFragment_to_indexFragment)
                }.onError { errorResponse ->
                    Snackbar.make(binding.root, getLocalization(errorResponse.message), Snackbar.LENGTH_LONG).show()
                }.onException { exception ->
                    exception.message?.let { message ->
                        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
                    }
                }
            }

            binding.fragmentSignInOk.setOnClickListener {
                if (!textInputLayoutControllerList.checkAll()) {
                    return@setOnClickListener
                }

                viewModel.signIn()
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
                        findNavController().navigate(R.id.action_signInFragment_to_registerFragment)
                    }
                    else -> {}
                }

                return true
            }
        }
    }
}
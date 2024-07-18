package moe.wisteria.android.ui.fragment.splash

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.datastore.preferences.core.edit
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import moe.wisteria.android.R
import moe.wisteria.android.databinding.FragmentSplashBinding
import moe.wisteria.android.entity.NetworkState
import moe.wisteria.android.ui.view.BaseFragment
import moe.wisteria.android.util.IO
import moe.wisteria.android.util.MAIN
import moe.wisteria.android.util.PreferenceKeys
import moe.wisteria.android.util.appDatastore
import moe.wisteria.android.util.userDatastore

class SplashFragment : BaseFragment(
    toolBarOption = ToolBarOption(
        display = false
    )
) {
    private lateinit var binding: FragmentSplashBinding
    private val viewModel: SplashModel by viewModels()

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {
        super.onCreate(savedInstanceState)

        viewModel.let { model ->
            requireContext().let { context ->
                lifecycleScope.launch(IO) {
                    context.appDatastore.edit { appDatastore ->
                        context.userDatastore.edit { userDatastore ->
                            model.init(
                                channel = appDatastore[PreferenceKeys.APP.SELECTED_CHANNEL],
                                email = userDatastore[PreferenceKeys.USER.EMAIL],
                                password = userDatastore[PreferenceKeys.USER.PASSWORD]
                            )
                        }
                    }
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return FragmentSplashBinding.inflate(inflater, container, false).also {
            binding = it
        }.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.let { model ->
            model.tryConnectServerState.observe(viewLifecycleOwner) {
                when (it.state) {
                    NetworkState.State.FAILED -> {
                        it.data?.let { data ->
                            Snackbar.make(binding.root, data, Snackbar.LENGTH_SHORT).show()
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

            model.tryLoginState.observe(viewLifecycleOwner) {
                when (it.state) {
                    NetworkState.State.FAILED -> {
                        it.data?.let { data ->
                            Snackbar.make(binding.root, data, Snackbar.LENGTH_SHORT).show()
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
                    requireContext().userDatastore.edit {
                        it[PreferenceKeys.USER.TOKEN] = token
                    }
                }
            }

            model.navigatePosition.observe(viewLifecycleOwner) {
                lifecycleScope.launch(IO) {
                    delay(800)
                    launch(MAIN) {
                        findNavController().navigate(
                            when (it!!) {
                                SplashModel.NavigatePosition.CHANNEL_SELECTOR -> R.id.action_splashFragment_to_channelSelectorFragment
                                SplashModel.NavigatePosition.SIGN_IN -> R.id.action_splashFragment_to_signInFragment
                                SplashModel.NavigatePosition.INDEX -> R.id.action_splashFragment_to_indexFragment
                            }
                        )
                    }
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
    }
}
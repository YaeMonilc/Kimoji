package moe.wisteria.android.ui.fragment.splash

import android.os.Bundle
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
import moe.wisteria.android.network.entity.response.PicaResponse.Companion.onException
import moe.wisteria.android.network.entity.response.PicaResponse.Companion.onSuccess
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

        viewModel.let { viewModel ->
            requireContext().let { context ->
                lifecycleScope.launch(IO) {
                    context.appDatastore.edit { appDatastore ->
                        context.userDatastore.edit { userDatastore ->
                            viewModel.init(
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

        viewModel.let { viewModel ->

            viewModel.signInResponse.observe(viewLifecycleOwner) {
                it.onSuccess {
                    lifecycleScope.launch(IO) {
                        requireContext().userDatastore.edit { userDatastore ->
                            userDatastore[PreferenceKeys.USER.TOKEN] = it.data.token
                        }
                    }
                }.onException { exception ->
                    exception.message?.let { message ->
                        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
                    }
                }
            }

            viewModel.navigatePosition.observe(viewLifecycleOwner) {
                lifecycleScope.launch(IO) {
                    delay(1800)
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
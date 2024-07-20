package moe.wisteria.android.kimoji.ui.fragment.splash

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import androidx.datastore.preferences.core.edit
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import moe.wisteria.android.kimoji.R
import moe.wisteria.android.kimoji.databinding.FragmentSplashBinding
import moe.wisteria.android.kimoji.network.entity.response.PicaResponse.Companion.onException
import moe.wisteria.android.kimoji.network.entity.response.PicaResponse.Companion.onSuccess
import moe.wisteria.android.kimoji.ui.view.BaseFragment
import moe.wisteria.android.kimoji.util.MAIN
import moe.wisteria.android.kimoji.util.PreferenceKeys
import moe.wisteria.android.kimoji.util.appDatastore
import moe.wisteria.android.kimoji.util.launchIO
import moe.wisteria.android.kimoji.util.launchUI
import moe.wisteria.android.kimoji.util.userDatastore
import kotlin.random.Random

class SplashFragment : BaseFragment(
    toolBarOption = ToolBarOption(
        display = false
    )
) {
    private lateinit var binding: FragmentSplashBinding
    private val viewModel: SplashModel by viewModels()

    private val animationList: MutableList<Animation> = mutableListOf()

    companion object {
        object AnimationOption  {
            const val TRANSLATE_IMAGE_DURATION = 1800L
        }
    }

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {
        super.onCreate(savedInstanceState)
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
            launchUI {
                requireContext().let { context ->
                    if (Random.nextBoolean()) {
                        animationList.add(
                            TranslateAnimation(
                                Animation.RELATIVE_TO_SELF,
                                0f,
                                Animation.RELATIVE_TO_SELF,
                                0f,
                                Animation.RELATIVE_TO_SELF,
                                1f,
                                Animation.RELATIVE_TO_SELF,
                                0f
                            ).apply {
                                duration = AnimationOption.TRANSLATE_IMAGE_DURATION
                                interpolator = AccelerateDecelerateInterpolator()
                            }.also {
                                binding.fragmentSplashImage.startAnimation(it)
                            }
                        )

                        delay(AnimationOption.TRANSLATE_IMAGE_DURATION)
                    } else {
                        binding.fragmentSplashImage.visibility = View.GONE
                    }

                    launchIO {
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

            viewModel.signInResponse.observe(viewLifecycleOwner) {
                it.onSuccess {
                    launchIO {
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
                launchIO {
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

    override fun onPause() {
        super.onPause()

        animationList.forEach { it.cancel() }
    }
}
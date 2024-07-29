package moe.wisteria.android.kimoji.ui.fragment.register

import android.os.Bundle
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.MenuProvider
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.datepicker.MaterialDatePicker
import moe.wisteria.android.kimoji.R
import moe.wisteria.android.kimoji.databinding.FragmentRegisterBinding
import moe.wisteria.android.kimoji.ui.view.BaseFragment
import moe.wisteria.android.kimoji.util.TextInputLayoutControllerList
import moe.wisteria.android.kimoji.util.picaExceptionHandler
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class RegisterFragment : BaseFragment(
    toolBarOption = ToolBarOption(
        title = R.string.fragment_register_title,
        displayHomeAsUpEnabled = true
    )
) {
    private lateinit var binding: FragmentRegisterBinding
    private val viewModel: RegisterModel by viewModels()

    private lateinit var textInputLayoutControllerList: TextInputLayoutControllerList

    companion object {
        const val DATE_FORMAT = "yyyy-MM-dd"
        const val GET_REGISTER_RESULT = "GET_REGISTER_RESULT"
        const val GET_REGISTER_RESULT_EMAIL = "GET_REGISTER_RESULT_EMAIL"
        const val GET_REGISTER_RESULT_PASSWORD = "GET_REGISTER_RESULT_PASSWORD"
    }

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

        textInputLayoutControllerList = TextInputLayoutControllerList(
            context = requireContext()
        ).apply {
            addTextInputLayout(binding.fragmentRegisterEmail)
            addTextInputLayout(binding.fragmentRegisterName)
            addTextInputLayout(
                textInputLayout = binding.fragmentRegisterPassword,
                conditionMap = mapOf(
                    R.string.fragment_sign_in_password_tip to {
                        (it?.length ?: 0) < 8
                    }
                )
            )
            addTextInputLayout(
                textInputLayout = binding.fragmentRegisterBirthday,
                conditionMap = mapOf(
                    R.string.view_textInput_layout_error_require_adult to {
                        it?.let { dateStr ->
                            SimpleDateFormat(DATE_FORMAT, Locale.getDefault())
                                .parse(dateStr)?.time?.let { selectTime ->
                                    Calendar.getInstance(Locale.getDefault()).apply {
                                        time = Date(selectTime)
                                    }.time > Calendar.getInstance(Locale.getDefault()).apply {
                                        add(Calendar.YEAR, -18)
                                    }.time
                                }
                        } == true
                    }
                )
            )
            addTextInputLayout(binding.fragmentRegisterQuestion)
            addTextInputLayout(binding.fragmentRegisterAnswer)
        }

        viewModel.let { viewModel ->
            binding.fragmentRegisterBirthday.setEndIconOnClickListener {
                MaterialDatePicker.Builder.datePicker().build().apply {
                    addOnPositiveButtonClickListener {
                        viewModel.birthday.value = DateFormat.format(DATE_FORMAT, Date(it)).toString()
                    }
                }.show(childFragmentManager, null)
            }

            binding.fragmentRegisterGender.addOnButtonCheckedListener { _, checkedId, _ ->
                viewModel.gender.postValue(
                    when (checkedId) {
                        R.id.fragment_register_gender_man -> RegisterModel.Gender.MAN
                        R.id.fragment_register_gender_woman -> RegisterModel.Gender.WOMAN
                        R.id.fragment_register_gender_other -> RegisterModel.Gender.OTHER
                        else -> RegisterModel.Gender.MAN
                    }.realValue
                )
            }

            binding.fragmentRegisterOk.setOnClickListener {
                if (!textInputLayoutControllerList.checkAll()) {
                    return@setOnClickListener
                }

                register()
            }
        }
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

    private fun register() {
        viewModel.register(
            success = {
                setFragmentResult(GET_REGISTER_RESULT, Bundle().apply {
                    putString(GET_REGISTER_RESULT_EMAIL, viewModel.email.value)
                    putString(GET_REGISTER_RESULT_PASSWORD, viewModel.password.value)
                })

                findNavController().popBackStack()
            }
        ) {
            picaExceptionHandler(it)
        }
    }
}
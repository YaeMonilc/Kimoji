package moe.wisteria.android.kimoji.ui.fragment.register

import android.os.Bundle
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.MenuProvider
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.datepicker.MaterialDatePicker
import moe.wisteria.android.kimoji.R
import moe.wisteria.android.kimoji.databinding.FragmentRegisterBinding
import moe.wisteria.android.kimoji.ui.view.BaseFragment
import moe.wisteria.android.kimoji.util.TextInputLayoutControllerList
import java.util.Date

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
            addTextInputLayout(binding.fragmentRegisterPassword)
            addTextInputLayout(binding.fragmentRegisterBirthday)
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
}
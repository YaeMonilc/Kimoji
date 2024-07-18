package moe.wisteria.android.util

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import com.google.android.material.textfield.TextInputLayout
import moe.wisteria.android.R

class TextInputLayoutControllerList(
    private val context: Context,
    private val conditionMap: Map<Int, (String?) -> Boolean> = mapOf(
        R.string.view_textInput_layout_error_not_be_empty to {
            it.isNullOrBlank()
        }
    )
) {
    private val textInputLayoutList: MutableList<TextInputLayoutController> = mutableListOf()

    fun addTextInputLayout(
        textInputLayout: TextInputLayout,
        conditionMap: Map<Int, (String?) -> Boolean> = mapOf()
    ) {
        textInputLayoutList.add(
            TextInputLayoutController(
                context = context,
                textInputLayout = textInputLayout,
                conditionMap = this.conditionMap.plus(conditionMap)
            )
        )
    }

    fun addConditionTo(
        textInputLayout: TextInputLayout,
        stringResId: Int,
        condition: (String?) -> Boolean
    ) {
        textInputLayoutList.firstOrNull {
            it.textInputLayout == textInputLayout
        }?.addCondition(
            stringResId = stringResId,
            condition = condition
        )
    }

    fun checkAll(): Boolean {
        textInputLayoutList.forEach {
            if (!it.check())
                return false
        }
        return true
    }
}

class TextInputLayoutController(
    private val context: Context,
    val textInputLayout: TextInputLayout,
    private val conditionMap: Map<Int, (String?) -> Boolean>
) {
    private val _conditionMap: MutableMap<Int, (String?) -> Boolean> = conditionMap.toMutableMap()

    init {
        textInputLayout.editText?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(p0: Editable?) {
                check()
            }
        })
    }

    fun check(): Boolean {
        _conditionMap.forEach { (i, function) ->
            function(textInputLayout.editText?.text.toString()).let {
                if (it) {
                    textInputLayout.error = context.getString(i)
                    return true
                }
            }
        }

        textInputLayout.error = null
        return false
    }

    fun addCondition(
        stringResId: Int,
        condition: (String?) -> Boolean
    ) {
        _conditionMap[stringResId] = condition
    }
}
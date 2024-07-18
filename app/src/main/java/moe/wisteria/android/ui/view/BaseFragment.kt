package moe.wisteria.android.ui.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.annotation.MenuRes
import androidx.annotation.StringRes
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment

abstract class BaseFragment(
    private val toolBarOption: ToolBarOption = ToolBarOption()
) : Fragment() {
    companion object {
        open class DefaultMenuProvider(
            @MenuRes
            private val menuRes: Int? = null,
            private val homeAsUpOnClick: () -> Boolean = { true },
            private val onMenuItemClick: (MenuItem) -> Boolean = { false }
        ) : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menu.clear()

                menuRes?.let {
                    menuInflater.inflate(it, menu)
                }
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    android.R.id.home -> homeAsUpOnClick()
                    else -> onMenuItemClick(menuItem)
                }
            }

        }
    }

    data class ToolBarOption(
        @StringRes
        val title: Int? = null,
        val displayHomeAsUpEnabled: Boolean = false,
        val display: Boolean = true
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initToolBar()
    }

    private fun getSupportActionBar(): ActionBar? {
        return (requireActivity() as AppCompatActivity).supportActionBar
    }

    private fun initToolBar() {
        getSupportActionBar()?.let { actionBar ->
            toolBarOption.let {
                if (it.display)
                    actionBar.show()
                else
                    actionBar.hide()

                actionBar.setDisplayHomeAsUpEnabled(it.displayHomeAsUpEnabled)

                actionBar.title = if (it.title == null) null else getString(it.title)

                requireActivity().addMenuProvider(getMenuProvider())
            }
        }
    }

    open fun getMenuProvider(): MenuProvider = DefaultMenuProvider()

    fun setDisplayHomeAsUp(
        enabled: Boolean
    ) {
        getSupportActionBar()?.setDisplayHomeAsUpEnabled(enabled)
    }
}
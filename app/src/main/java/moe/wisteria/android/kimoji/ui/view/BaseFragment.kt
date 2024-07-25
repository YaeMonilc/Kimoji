package moe.wisteria.android.kimoji.ui.view

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
import com.google.android.material.snackbar.Snackbar

abstract class BaseFragment(
    private val toolBarOption: ToolBarOption = ToolBarOption()
) : Fragment() {
    open class DefaultMenuProvider(
        @MenuRes
        private val menuRes: Int? = null
    ) : MenuProvider {
        override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
            menu.clear()

            menuRes?.let {
                menuInflater.inflate(it, menu)
            }
        }

        override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
            return when (menuItem.itemId) {
                android.R.id.home -> onHomeAsUpClick()
                else -> onMenuItemClick(menuItem)
            }
        }

        open fun onHomeAsUpClick() = false
        open fun onMenuItemClick(menuItem: MenuItem) = false
    }

    data class ToolBarOption(
        @StringRes
        val title: Int? = null,
        val displayHomeAsUpEnabled: Boolean = false,
        val display: Boolean = true
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onStart() {
        super.onStart()

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

                setMenuProvider(getMenuProvider())
            }
        }
    }

    private fun setMenuProvider(
        menuProvider: MenuProvider
    ) {
        requireActivity().addMenuProvider(menuProvider, viewLifecycleOwner)
    }

    open fun getMenuProvider(): MenuProvider = DefaultMenuProvider()

    internal fun setDisplayHomeAsUp(
        enabled: Boolean
    ) {
        getSupportActionBar()?.setDisplayHomeAsUpEnabled(enabled)
    }

    internal fun showSnackBar(
        @StringRes
        message: Int,
        view: View? = null,
        length: Int = Snackbar.LENGTH_SHORT
    ) {
        Snackbar.make(view ?: requireActivity().findViewById(android.R.id.content), message, length).show()
    }

    internal fun showSnackBar(
        message: String,
        view: View? = null,
        length: Int = Snackbar.LENGTH_SHORT
    ) {
        Snackbar.make(view ?: requireActivity().findViewById(android.R.id.content), message, length).show()
    }
}
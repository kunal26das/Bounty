package kudos26.bounty.core

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.annotation.CallSuper
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat
import androidx.fragment.app.Fragment
import com.microsoft.officeuifabric.util.ThemeUtil
import kudos26.bounty.R
import org.koin.android.ext.android.inject


/**
 * Created by kunal on 06-02-2020.
 */

abstract class Fragment : Fragment() {

    protected abstract val viewModel: ViewModel
    private val inputMethodManager: InputMethodManager by inject()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initObservers()
    }

    @CallSuper
    protected open fun initObservers() {
    }

    @CallSuper
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        for (index in 0 until menu.size()) {
            val drawable = menu.getItem(index).icon
            drawable?.colorFilter = BlendModeColorFilterCompat.createBlendModeColorFilterCompat(
                    ThemeUtil.getThemeAttrColor(requireContext(), R.attr.uifabricToolbarIconColor),
                    BlendModeCompat.SRC_IN
            )
        }
    }

    protected fun hideKeyboard(view: View) {
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }
}
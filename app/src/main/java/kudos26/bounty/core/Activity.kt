package kudos26.bounty.core

import android.os.Bundle
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.annotation.CallSuper
import androidx.appcompat.app.AppCompatActivity
import org.koin.android.ext.android.inject

abstract class Activity : AppCompatActivity() {

    abstract val viewModel: ViewModel
    private val inputMethodManager: InputMethodManager by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initObservers()
    }

    @CallSuper
    protected open fun initObservers() {
    }

    @CallSuper
    override fun onSupportNavigateUp(): Boolean {
        hideKeyboard()
        return super.onSupportNavigateUp()
    }

    @CallSuper
    override fun onBackPressed() {
        hideKeyboard()
        super.onBackPressed()
    }

    protected fun hideKeyboard() {
        inputMethodManager.hideSoftInputFromWindow(findViewById<ViewGroup>(android.R.id.content).windowToken, 0)
    }

    // Switch Google Account Method

}


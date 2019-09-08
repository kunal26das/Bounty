package kudos26.bounty.core

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SwitchCompat
import com.google.android.material.snackbar.Snackbar
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

abstract class Activity : AppCompatActivity() {

    // Activity Life Cycle
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )
    }

    override fun onDestroy() {
        CompositeDisposable().clear()
        super.onDestroy()
    }

    // Disposables
    fun dispose(disposable: Disposable) {
        CompositeDisposable().add(disposable)
    }

    // Theme
    fun switchTheme(flag: Boolean) {
        flag.apply {
            when (this) {
                true -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                false -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            }
        }
    }

    fun isDarkTheme(): Boolean {
        return when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_NO -> true
            Configuration.UI_MODE_NIGHT_YES -> false
            else -> false
        }
    }

    // Status Bar
    fun getStatusBarHeight(): Int {
        resources.getIdentifier("status_bar_height", "dimen", "android").apply {
            return resources.getDimensionPixelSize(this)
        }
    }

    // Toasts
    fun String.toastForShortDuration(context: Context = this@Activity, duration: Int = Toast.LENGTH_SHORT): Toast {
        return Toast.makeText(context, this, duration).apply { show() }
    }

    fun String.toastForLongDuration(context: Context = this@Activity, duration: Int = Toast.LENGTH_LONG): Toast {
        return Toast.makeText(context, this, duration).apply { show() }
    }

    // SnackBars
    fun String.snackBarForShortDuration(view: View = findViewById(android.R.id.content), duration: Int = Snackbar.LENGTH_SHORT): Snackbar {
        return Snackbar.make(view, this, duration).apply { show() }
    }

    fun String.snackBarForLongDuration(view: View = findViewById(android.R.id.content), duration: Int = Snackbar.LENGTH_LONG): Snackbar {
        return Snackbar.make(view, this, duration).apply { show() }
    }

    fun String.snackBarForIndefiniteDuration(view: View = findViewById(android.R.id.content), duration: Int = Snackbar.LENGTH_INDEFINITE): Snackbar {
        return Snackbar.make(view, this, duration).apply { show() }
    }

    // Switch
    fun SwitchCompat.onCheckedChangeListener(boolean: (Boolean) -> Unit) {
        this.setOnCheckedChangeListener { _, isChecked ->
            boolean(isChecked)
        }
    }

}


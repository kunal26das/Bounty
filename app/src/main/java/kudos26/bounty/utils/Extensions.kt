package kudos26.bounty.utils

import android.view.View
import android.view.ViewTreeObserver
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.microsoft.officeuifabric.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.*

object Extensions {

    private val LOCALE_INDIA = Locale("en", "in")

    val Long.amount: String get() = NumberFormat.getInstance(LOCALE_INDIA).format(this)

    inline fun <T> T.Try(block: T.() -> Unit) = try {
        block()
    } catch (e: Exception) {
    }

    inline fun <T> T.default(crossinline block: T.() -> Unit) = CoroutineScope(Dispatchers.Default).launch {
        Try { block() }
    }

    inline fun <T> T.main(crossinline block: T.() -> Unit) = CoroutineScope(Dispatchers.Main).launch {
        Try { block() }
    }

    fun View.onGlobalLayoutListener(action: () -> Unit) {
        viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                viewTreeObserver.removeOnGlobalLayoutListener(this)
                action.invoke()
            }
        })
    }

    fun Snackbar.setOnDismissListener(action: (transientBottomBar: Snackbar?, event: Int) -> Unit) {
        addCallback(object : BaseTransientBottomBar.BaseCallback<Snackbar>() {
            override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                super.onDismissed(transientBottomBar, event)
                action.invoke(transientBottomBar, event)
            }
        })
    }
}
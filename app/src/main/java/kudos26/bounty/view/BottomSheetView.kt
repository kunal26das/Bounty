package kudos26.bounty.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.core.view.updateMargins
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.*
import com.google.android.material.card.MaterialCardView
import kudos26.bounty.R

class BottomSheetView @JvmOverloads constructor(
        context: Context,
        attributes: AttributeSet? = null
) : MaterialCardView(context, attributes) {

    var peekHeight: Int
        get() = bottomSheetBehaviour.peekHeight
        set(value) {
            bottomSheetBehaviour.peekHeight = value
        }
    val isExpanded get() = bottomSheetBehaviour.state == STATE_EXPANDED
    val isCollapsed get() = bottomSheetBehaviour.state == STATE_COLLAPSED
    private lateinit var bottomSheetBehaviour: BottomSheetBehavior<View>

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        bottomSheetBehaviour = from(this)
        bottomSheetBehaviour.isDraggable = false
        bottomSheetBehaviour.state = STATE_EXPANDED

        radius = context.resources.getDimension(R.dimen.margin_global)
        (layoutParams as MarginLayoutParams).updateMargins(bottom = context.resources.getDimension(R.dimen.margin_global_negative).toInt())

    }

    fun collapse() = try {
        bottomSheetBehaviour.state = STATE_COLLAPSED
    } catch (e: RuntimeException) {
        onAttachedToWindow()
    }

    fun expand() = try {
        bottomSheetBehaviour.state = STATE_EXPANDED
    } catch (e: RuntimeException) {
        onAttachedToWindow()
    }

    fun switch() {
        when (bottomSheetBehaviour.state) {
            STATE_COLLAPSED -> expand()
            STATE_EXPANDED -> collapse()
        }
    }
}
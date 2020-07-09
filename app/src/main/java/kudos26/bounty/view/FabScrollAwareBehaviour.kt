/*
 * Copyright (c) 2020.
 */

package kudos26.bounty.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.floatingactionbutton.FloatingActionButton.OnVisibilityChangedListener


class FabScrollAwareBehaviour(
        context: Context?,
        attrs: AttributeSet?
) :
        FloatingActionButton.Behavior() {
    override fun onStartNestedScroll(
            parent: CoordinatorLayout,
            fab: FloatingActionButton,
            directTargetfab: View,
            target: View,
            nestedScrollAxes: Int
    ): Boolean {
        return true
    }

    override fun layoutDependsOn(
            parent: CoordinatorLayout,
            fab: FloatingActionButton,
            dependency: View
    ): Boolean {
        return dependency is RecyclerView
    }

    override fun onNestedScroll(
            coordinatorLayout: CoordinatorLayout,
            fab: FloatingActionButton,
            target: View,
            dxConsumed: Int,
            dyConsumed: Int,
            dxUnconsumed: Int,
            dyUnconsumed: Int,
            type: Int,
            consumed: IntArray
    ) {
        super.onNestedScroll(
                coordinatorLayout,
                fab,
                target,
                dxConsumed,
                dyConsumed,
                dxUnconsumed,
                dyUnconsumed,
                type,
                consumed
        )
        if (dyConsumed > 0 && fab.visibility == View.VISIBLE) {
            fab.hide(object : OnVisibilityChangedListener() {
                override fun onHidden(fab: FloatingActionButton) {
                    super.onHidden(fab)
                    fab.visibility = View.INVISIBLE
                }
            })
        } else if (dyConsumed < 0 && fab.visibility != View.VISIBLE) {
            fab.show()
        }
    }

}
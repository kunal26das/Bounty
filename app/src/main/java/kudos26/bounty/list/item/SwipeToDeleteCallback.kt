package kudos26.bounty.list.item

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import androidx.core.graphics.drawable.toBitmap
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import kudos26.bounty.R


class SwipeToDeleteCallback(
        context: Context,
        action: (position: Int) -> Unit
) : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {

    private var onSwipeListener: OnSwipeListener? = null
    private var icon = context.getDrawable(R.drawable.ic_delete_outline_24dp)!!
    private val paint = Paint().apply { color = context.getColor(R.color.colorNegative) }

    init {
        onSwipeListener = object : OnSwipeListener {
            override fun onSwipe(position: Int) {
                action.invoke(position)
            }
        }
    }

    override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder) = false

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        onSwipeListener?.onSwipe(viewHolder.adapterPosition)
    }

    override fun onChildDraw(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            val itemView = viewHolder.itemView
            val width = (itemView.bottom - itemView.top) / 3f
            if (dX > 0) {
                c.drawRect(RectF(
                        itemView.left.toFloat(),
                        itemView.top.toFloat(),
                        dX,
                        itemView.bottom.toFloat()
                ), paint)
                c.drawBitmap(icon.toBitmap(), null, RectF(
                        itemView.left + width,
                        itemView.top + width,
                        itemView.left + 2 * width,
                        itemView.bottom - width
                ), paint)
            } else {
                c.drawRect(RectF(itemView.right + dX, itemView.top.toFloat(), itemView.right.toFloat(), itemView.bottom.toFloat()), paint)
                c.drawBitmap(icon.toBitmap(), null, RectF(
                        itemView.right - 2 * width,
                        itemView.top + width,
                        itemView.right - width,
                        itemView.bottom - width
                ), paint)
            }
        }

        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }

    interface OnSwipeListener {
        fun onSwipe(position: Int)
    }

}
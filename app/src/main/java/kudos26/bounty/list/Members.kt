package kudos26.bounty.list

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DatabaseReference
import com.microsoft.officeuifabric.listitem.ListItemDivider
import com.microsoft.officeuifabric.listitem.ListItemView
import com.microsoft.officeuifabric.popupmenu.PopupMenu
import com.microsoft.officeuifabric.popupmenu.PopupMenuItem
import com.microsoft.officeuifabric.util.ThemeUtil
import kudos26.bounty.R
import kudos26.bounty.adapter.ListItemViewHolder
import kudos26.bounty.adapter.LoadingAdapter
import kudos26.bounty.firebase.Extensions.name
import kudos26.bounty.firebase.Extensions.observeValue
import kudos26.bounty.firebase.Extensions.user
import kudos26.bounty.source.model.Member
import kudos26.bounty.source.model.Member.Companion.memberDiffCallback
import kudos26.bounty.utils.CalendarUtils.date
import kudos26.bounty.utils.Extensions.Try
import org.koin.core.KoinComponent
import org.koin.core.inject


class Members(
        context: Context,
        attributes: AttributeSet? = null
) : RecyclerView(context, attributes), KoinComponent {

    private val membersAdapter = MembersAdapter()
    private val loadingAdapter = LoadingAdapter()
    private val database: DatabaseReference by inject()
    private val linearLayoutManager = LinearLayoutManager(context)

    var adminAccess = false
        set(value) {
            field = value
            membersAdapter.adminAccess = value
        }

    init {
        adapter = loadingAdapter
        layoutManager = linearLayoutManager
        addItemDecoration(ListItemDivider(context, DividerItemDecoration.VERTICAL))
    }

    fun submitList(list: List<Member>?) {
        adapter = when (list) {
            null -> loadingAdapter
            else -> {
                membersAdapter.submitList(list)
                membersAdapter
            }
        }
    }

    fun setOnMemberClickListener(action: ((popupMenuItem: PopupMenuItem, member: Member) -> Unit)) {
        membersAdapter.onMemberClickListener = object : OnMemberClickListener {
            override fun onClick(popupMenuItem: PopupMenuItem, member: Member) {
                action(popupMenuItem, member)
            }
        }
    }

    inner class MembersAdapter : ListAdapter<Member, ListItemViewHolder>(memberDiffCallback) {

        var adminAccess = false
            set(value) {
                field = value
                notifyDataSetChanged()
            }

        private val popupMenuItems = arrayListOf(
                PopupMenuItem(R.id.remove, context.getString(R.string.remove)),
                PopupMenuItem(R.id.admin, context.getString(R.string.make_admin))
        )

        var onMemberClickListener: OnMemberClickListener? = null

        private fun popupMenu(anchorView: View) = PopupMenu(
                context, anchorView, popupMenuItems,
                PopupMenu.ItemCheckableBehavior.NONE
        )

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListItemViewHolder {
            ListItemView(context).apply {
                layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                )
                return ListItemViewHolder(this)
            }
        }

        override fun onBindViewHolder(holder: ListItemViewHolder, position: Int) {
            getItem(position).apply {
                holder.listItemView.subtitle = date.date
                holder.listItemView.footer = when (isAdmin) {
                    true -> "Admin"
                    else -> {
                        if (adminAccess) {
                            AppCompatImageView(context).let {
                                it.setImageDrawable(context.getDrawable(R.drawable.ic_more_vert_24dp))
                                it.drawable?.colorFilter = BlendModeColorFilterCompat.createBlendModeColorFilterCompat(
                                        ThemeUtil.getThemeAttrColor(context, R.attr.uifabricToolbarBackgroundColor),
                                        BlendModeCompat.SRC_IN
                                )
                                holder.listItemView.customAccessoryView = it
                                TypedValue().apply {
                                    context.theme.resolveAttribute(
                                            android.R.attr.selectableItemBackgroundBorderless,
                                            this, true
                                    )
                                    it.setBackgroundResource(resourceId)
                                }
                                holder.listItemView.customAccessoryView?.setOnClickListener {
                                    popupMenu(it).apply {
                                        onItemClickListener = object : PopupMenuItem.OnClickListener {
                                            override fun onPopupMenuItemClicked(popupMenuItem: PopupMenuItem) {
                                                onMemberClickListener?.onClick(popupMenuItem, getItem(position))
                                            }
                                        }
                                    }.show()
                                }
                            }
                        }
                        ""
                    }
                }
                database.user(uid).name.observeValue({
                    Try {
                        name = it.value as String
                        holder.listItemView.title = name
                    }
                })
            }
        }
    }

    interface OnMemberClickListener {
        fun onClick(popupMenuItem: PopupMenuItem, member: Member)
    }
}
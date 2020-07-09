package kudos26.bounty.list

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.*
import com.google.firebase.database.DatabaseReference
import com.microsoft.officeuifabric.listitem.ListItemDivider
import com.microsoft.officeuifabric.listitem.ListItemView
import kudos26.bounty.adapter.EmptyAdapter
import kudos26.bounty.adapter.ListItemViewHolder
import kudos26.bounty.adapter.LoadingAdapter
import kudos26.bounty.firebase.Extensions.group
import kudos26.bounty.firebase.Extensions.name
import kudos26.bounty.firebase.Extensions.observeValue
import kudos26.bounty.firebase.Extensions.user
import kudos26.bounty.list.item.SwipeToAcceptCallback
import kudos26.bounty.list.item.SwipeToDeleteCallback
import kudos26.bounty.source.model.Invitation
import kudos26.bounty.source.model.Invitation.Companion.invitationDiffCallback
import kudos26.bounty.utils.CalendarUtils.date
import kudos26.bounty.utils.Extensions.main
import org.koin.core.KoinComponent
import org.koin.core.inject

class Invitations(
        context: Context,
        attributes: AttributeSet? = null
) : RecyclerView(context, attributes), KoinComponent {

    private val loadingAdapter = LoadingAdapter()
    private val database: DatabaseReference by inject()
    private val invitationsAdapter = InvitationsAdapter()
    private var swipeToAcceptHelper: ItemTouchHelper? = null
    private var swipeToDeleteHelper: ItemTouchHelper? = null
    private val linearLayoutManager = LinearLayoutManager(context)
    private val emptyAdapter = EmptyAdapter("You have no pending invitations")

    init {
        adapter = loadingAdapter
        layoutManager = linearLayoutManager
        addItemDecoration(ListItemDivider(context, DividerItemDecoration.VERTICAL))
    }

    fun submitList(list: List<Invitation>?) {
        swipeToAcceptHelper?.attachToRecyclerView(null)
        swipeToDeleteHelper?.attachToRecyclerView(null)
        adapter = when {
            list == null -> loadingAdapter
            list.isEmpty() -> emptyAdapter
            else -> {
                swipeToAcceptHelper?.attachToRecyclerView(this)
                swipeToDeleteHelper?.attachToRecyclerView(this)
                invitationsAdapter.submitList(list)
                invitationsAdapter
            }
        }
    }

    fun setOnSwipeToAcceptListener(action: ((invitation: Invitation) -> Unit)) {
        swipeToAcceptHelper = ItemTouchHelper(SwipeToAcceptCallback(context) {
            invitationsAdapter.currentList.toMutableList().apply {
                action(removeAt(it))
                submitList(this)
            }
        })
    }

    fun setOnSwipeToDeleteListener(action: ((invitation: Invitation) -> Unit)) {
        swipeToDeleteHelper = ItemTouchHelper(SwipeToDeleteCallback(context) {
            invitationsAdapter.currentList.toMutableList().apply {
                action(removeAt(it))
                submitList(this)
            }
        })
    }

    inner class InvitationsAdapter : ListAdapter<Invitation, ListItemViewHolder>(invitationDiffCallback) {

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
                holder.listItemView.footer = date.date
                database.group(id).name.observeValue({
                    main { holder.listItemView.title = it.value as String }
                })
                database.user(uid).name.observeValue({
                    main { holder.listItemView.subtitle = "From: ${it.value as String}" }
                })
            }
        }
    }

}
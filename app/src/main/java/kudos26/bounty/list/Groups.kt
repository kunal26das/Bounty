package kudos26.bounty.list

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textview.MaterialTextView
import com.google.firebase.database.DatabaseReference
import com.microsoft.officeuifabric.listitem.ListItemDivider
import com.microsoft.officeuifabric.listitem.ListItemView
import kudos26.bounty.R
import kudos26.bounty.adapter.EmptyAdapter
import kudos26.bounty.adapter.ListItemViewHolder
import kudos26.bounty.adapter.LoadingAdapter
import kudos26.bounty.firebase.Extensions.amount
import kudos26.bounty.firebase.Extensions.group
import kudos26.bounty.firebase.Extensions.name
import kudos26.bounty.firebase.Extensions.observeValue
import kudos26.bounty.firebase.Extensions.transactions
import kudos26.bounty.source.model.Group
import kudos26.bounty.source.model.Group.Companion.groupDiffCallback
import kudos26.bounty.utils.Extensions.amount
import kudos26.bounty.utils.Extensions.default
import kudos26.bounty.utils.Extensions.main
import org.koin.core.KoinComponent
import org.koin.core.inject

class Groups(
        context: Context,
        attributes: AttributeSet? = null
) : RecyclerView(context, attributes), KoinComponent {

    private val groupsAdapter = GroupsAdapter()
    private val loadingAdapter = LoadingAdapter()
    private val database: DatabaseReference by inject()
    private val emptyAdapter = EmptyAdapter("Create a Group")
    private val linearLayoutManager = LinearLayoutManager(context)

    init {
        adapter = loadingAdapter
        layoutManager = linearLayoutManager
        addItemDecoration(ListItemDivider(context, DividerItemDecoration.VERTICAL))
    }

    fun submitList(list: List<Group>?) {
        adapter = when {
            list == null -> loadingAdapter
            list.isEmpty() -> emptyAdapter
            else -> {
                groupsAdapter.submitList(list)
                groupsAdapter
            }
        }
    }

    fun setOnGroupClickListener(action: ((group: Group) -> Unit)) {
        groupsAdapter.onGroupClickListener = object : OnGroupClickListener {
            override fun onClick(group: Group) {
                action(group)
            }
        }
    }

    inner class GroupsAdapter : ListAdapter<Group, ListItemViewHolder>(groupDiffCallback) {

        var onGroupClickListener: OnGroupClickListener? = null

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
                holder.listItemView.setOnClickListener {
                    onGroupClickListener?.onClick(this)
                }
                database.group(id).observeValue({
                    if (it.exists()) default {
                        name = it.name
                        total = it.transactions.fold(0L) { sum, transaction ->
                            sum + transaction.amount
                        }
                        main {
                            holder.listItemView.title = it.name
                            MaterialTextView(context).let {
                                it.textSize = 20f
                                it.text = "₹${total.amount}"
                                holder.listItemView.customAccessoryView = it
                                it.setTextColor(context.getColor(R.color.colorAccent))
                            }
                        }
                    }
                })
            }
        }
    }

    interface OnGroupClickListener {
        fun onClick(group: Group)
    }

}
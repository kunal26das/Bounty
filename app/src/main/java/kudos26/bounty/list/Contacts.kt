package kudos26.bounty.list

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.*
import com.microsoft.officeuifabric.listitem.ListItemDivider
import com.microsoft.officeuifabric.listitem.ListItemView
import com.microsoft.officeuifabric.persona.IPersona
import kudos26.bounty.adapter.EmptyAdapter
import kudos26.bounty.adapter.ListItemViewHolder
import kudos26.bounty.adapter.LoadingAdapter

class Contacts(
        context: Context,
        attributes: AttributeSet? = null
) : RecyclerView(context, attributes) {

    private val loadingAdapter = LoadingAdapter()
    private val contactsAdapter = ContactsAdapter()
    private val emptyAdapter = EmptyAdapter("No Contacts Found")
    private val linearLayoutManager = LinearLayoutManager(context)

    init {
        adapter = loadingAdapter
        layoutManager = linearLayoutManager
        addItemDecoration(ListItemDivider(context, DividerItemDecoration.VERTICAL))
    }

    fun submitList(list: List<IPersona>?) {
        adapter = when {
            list == null -> loadingAdapter
            list.isEmpty() -> emptyAdapter
            else -> {
                contactsAdapter.submitList(list)
                contactsAdapter
            }
        }
    }

    fun setOnGroupClickListener(action: ((iPersona: IPersona) -> Unit)) {
        contactsAdapter.onContactClickListener = object : OnContactClickListener {
            override fun onClick(iPersona: IPersona) {
                action(iPersona)
            }
        }
    }

    inner class ContactsAdapter : ListAdapter<IPersona, ListItemViewHolder>(iPersonaDiffCallback) {

        var onContactClickListener: OnContactClickListener? = null

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
                holder.listItemView.title = name
                holder.listItemView.subtitle = email
                holder.listItemView.setOnClickListener {
                    onContactClickListener?.onClick(this)
                }
            }
        }
    }

    interface OnContactClickListener {
        fun onClick(iPersona: IPersona)
    }

    companion object {

        val iPersonaDiffCallback = object : DiffUtil.ItemCallback<IPersona>() {

            override fun areItemsTheSame(oldItem: IPersona, newItem: IPersona): Boolean {
                return oldItem.email == newItem.email
            }

            override fun areContentsTheSame(oldItem: IPersona, newItem: IPersona): Boolean {
                return oldItem.email == newItem.email && oldItem.name == newItem.name
            }
        }
    }

}
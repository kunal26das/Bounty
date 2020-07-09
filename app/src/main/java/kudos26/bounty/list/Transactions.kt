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
import kudos26.bounty.firebase.Extensions.name
import kudos26.bounty.firebase.Extensions.observeValue
import kudos26.bounty.firebase.Extensions.user
import kudos26.bounty.source.model.Transaction
import kudos26.bounty.source.model.Transaction.Companion.transactionDiffCallback
import kudos26.bounty.utils.CalendarUtils.date
import kudos26.bounty.utils.Extensions.Try
import kudos26.bounty.utils.Extensions.amount
import kudos26.bounty.utils.Extensions.main
import org.koin.core.KoinComponent
import org.koin.core.inject

class Transactions(
        context: Context,
        attributes: AttributeSet? = null
) : RecyclerView(context, attributes), KoinComponent {

    private val loadingAdapter = LoadingAdapter()
    private val transactionAdapter = TransactionsAdapter()
    private val emptyAdapter = EmptyAdapter("No Transactions")
    private val firebaseDatabase: DatabaseReference by inject()
    private val linearLayoutManager = LinearLayoutManager(context)

    init {
        adapter = loadingAdapter
        layoutManager = linearLayoutManager
        addItemDecoration(ListItemDivider(context, DividerItemDecoration.VERTICAL))
    }

    fun submitList(list: List<Transaction>?) {
        adapter = when {
            list == null -> loadingAdapter
            list.isEmpty() -> emptyAdapter
            else -> {
                transactionAdapter.submitList(list)
                transactionAdapter
            }
        }
    }

    fun setOnTransactionClickListener(action: ((transaction: Transaction) -> Unit)) {
        transactionAdapter.onTransactionClickListener = object : OnTransactionClickListener {
            override fun onClick(transaction: Transaction) {
                action(transaction)
            }
        }
    }

    inner class TransactionsAdapter : ListAdapter<Transaction, ListItemViewHolder>(transactionDiffCallback) {

        lateinit var onTransactionClickListener: OnTransactionClickListener

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
                holder.listItemView.title = comment
                holder.listItemView.footer = date.date
                MaterialTextView(context).let {
                    it.textSize = 20f
                    it.text = "₹${amount.amount}"
                    holder.listItemView.customAccessoryView = it
                    it.setTextColor(context.getColor(R.color.colorAccent))
                }
                holder.listItemView.setOnClickListener {
                    onTransactionClickListener.onClick(this)
                }
                firebaseDatabase.user(payer.uid).name.observeValue({
                    Try {
                        payer.name = it.value as String
                        main { holder.listItemView.subtitle = "Paid by: ${payer.name}" }
                    }
                })
            }
        }
    }

    interface OnTransactionClickListener {
        fun onClick(transaction: Transaction)
    }

}
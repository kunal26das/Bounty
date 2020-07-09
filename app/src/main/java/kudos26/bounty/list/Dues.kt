package kudos26.bounty.list

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.microsoft.officeuifabric.listitem.ListItemDivider
import kudos26.bounty.R
import kudos26.bounty.adapter.EmptyAdapter
import kudos26.bounty.adapter.LoadingAdapter
import kudos26.bounty.databinding.ItemDueBinding
import kudos26.bounty.firebase.Extensions.name
import kudos26.bounty.firebase.Extensions.observeValue
import kudos26.bounty.firebase.Extensions.upi
import kudos26.bounty.firebase.Extensions.user
import kudos26.bounty.source.model.Due
import kudos26.bounty.source.model.Due.Companion.duesDiffCallback
import kudos26.bounty.utils.Extensions.Try
import kudos26.bounty.utils.Extensions.amount
import kudos26.bounty.utils.Extensions.default
import org.koin.core.KoinComponent
import org.koin.core.inject

class Dues(
        context: Context,
        attributes: AttributeSet? = null
) : RecyclerView(context, attributes), KoinComponent {

    private val duesAdapter = DuesAdapter()
    private val loadingAdapter = LoadingAdapter()
    private val firebaseAuth: FirebaseAuth by inject()
    private val database: DatabaseReference by inject()
    private val emptyAdapter = EmptyAdapter("No Pending Dues")
    private val linearLayoutManager = LinearLayoutManager(context)

    init {
        adapter = loadingAdapter
        layoutManager = linearLayoutManager
        addItemDecoration(ListItemDivider(context, DividerItemDecoration.VERTICAL))
    }

    fun submitList(list: List<Due>?) {
        adapter = when {
            list == null -> loadingAdapter
            list.isEmpty() -> emptyAdapter
            else -> {
                duesAdapter.submitList(list.toMutableList())
                duesAdapter
            }
        }
    }

    fun setOnDueClickListener(action: ((due: Due) -> Unit)) {
        duesAdapter.onDueClickListener = object : OnDueClickListener {
            override fun onClick(due: Due) {
                action(due)
            }
        }
    }

    inner class DuesAdapter : ListAdapter<Due, DuesAdapter.DueHolder>(duesDiffCallback) {

        override fun submitList(list: MutableList<Due>?) {
            default {
                super.submitList(list?.apply {
                    forEach {
                        if (it.amount < 0) {
                            it.amount *= -1
                            val temp = it.debtor
                            it.debtor = it.creditor
                            it.creditor = temp
                        }
                    }
                })
            }
        }

        var onDueClickListener: OnDueClickListener? = null

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DueHolder {
            return DueHolder(DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.item_due, parent, false))
        }

        override fun onBindViewHolder(holder: DueHolder, position: Int) {
            getItem(position).apply {
                if (firebaseAuth.currentUser?.uid == debtor.uid) {
                    holder.view.root.isClickable = true
                    holder.itemView.setOnClickListener {
                        onDueClickListener?.onClick(this)
                    }
                }
                holder.view.amountTextView.text = "₹${amount.amount}"
                database.user(debtor.uid).name.observeValue({
                    Try { debtor.name = it.value as String }
                    holder.view.debtorTextView.text = debtor.name
                })
                database.user(creditor.uid).name.observeValue({
                    Try { creditor.name = it.value as String }
                    holder.view.creditorTextView.text = creditor.name
                })
                database.user(creditor.uid).upi.observeValue({
                    Try { creditor.upi = it.value as String }
                })
            }
        }

        inner class DueHolder(val view: ItemDueBinding) : RecyclerView.ViewHolder(view.root)
    }

    interface OnDueClickListener {
        fun onClick(due: Due)
    }

}
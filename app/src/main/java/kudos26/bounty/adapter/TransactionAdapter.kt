package kudos26.bounty.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import kudos26.bounty.R
import kudos26.bounty.source.model.Transaction

/**
 * Created by kunal on 19-01-2020.
 */

interface OnTransactionClickListener {
    fun onClick(transaction: Transaction, position: Int)
}

interface OnTransactionLongClickListener {
    fun onLongClick(transaction: Transaction, position: Int)
}

class TransactionAdapter : RecyclerView.Adapter<TransactionAdapter.TransactionHolder>() {

    lateinit var onTransactionClickListener: OnTransactionClickListener
    lateinit var onTransactionLongClickListener: OnTransactionLongClickListener

    var transactions = listOf<Transaction>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    fun getTotalAmount(): Int {
        var sum = 0
        transactions.forEach {
            sum += it.amount!!
        }
        return sum
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionHolder {
        return TransactionHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_transaction, parent, false))
    }

    override fun getItemCount() = transactions.size

    override fun onBindViewHolder(holder: TransactionHolder, position: Int) {
        holder.transactionAmount.text = "₹${transactions[position].amount}"
        holder.transactionComment.text = transactions[position].comment
        holder.itemView.setOnClickListener {
            onTransactionClickListener.onClick(transactions[position], position)
        }
        holder.itemView.setOnLongClickListener {
            onTransactionLongClickListener.onLongClick(transactions[position], position)
            true
        }
    }

    inner class TransactionHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val transactionAmount: AppCompatTextView = itemView.findViewById(R.id.transactionAmount)
        val transactionComment: AppCompatTextView = itemView.findViewById(R.id.transactionComment)
    }
}
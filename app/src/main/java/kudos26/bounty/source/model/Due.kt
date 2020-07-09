package kudos26.bounty.source.model

import androidx.recyclerview.widget.DiffUtil

data class Due(
        var debtor: Member = Member(),
        var creditor: Member = Member(),
        var amount: Long = 0
) {

    companion object {

        val duesDiffCallback = object : DiffUtil.ItemCallback<Due>() {

            override fun areItemsTheSame(oldItem: Due, newItem: Due): Boolean {
                return oldItem.debtor.uid == newItem.debtor.uid
            }

            override fun areContentsTheSame(oldItem: Due, newItem: Due): Boolean {
                return oldItem == newItem
            }
        }
    }
}
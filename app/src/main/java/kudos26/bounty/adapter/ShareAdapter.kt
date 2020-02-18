package kudos26.bounty.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import kudos26.bounty.R
import kudos26.bounty.source.model.Share

/**
 * Created by kunal on 19-01-2020.
 */

class ShareAdapter : RecyclerView.Adapter<ShareAdapter.ShareHolder>() {

    var shares = listOf<Share>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShareHolder {
        return ShareHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_share, parent, false))
    }

    override fun getItemCount() = shares.size

    override fun onBindViewHolder(holder: ShareHolder, position: Int) {
        holder.memberName.text = shares[position].name
        holder.memberShare.text = "₹${shares[position].amount}"
    }

    inner class ShareHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val memberName: AppCompatTextView = itemView.findViewById(R.id.memberName)
        val memberShare: AppCompatTextView = itemView.findViewById(R.id.memberShare)
    }
}
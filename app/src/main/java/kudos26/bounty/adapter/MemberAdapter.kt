package kudos26.bounty.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import kudos26.bounty.R
import kudos26.bounty.source.model.Member

/**
 * Created by kunal on 19-01-2020.
 */

interface OnUserClickListener {
    fun onClick(contact: Member, position: Int)
}

class MemberAdapter : RecyclerView.Adapter<MemberAdapter.MemberHolder>() {

    lateinit var onUserClickListener: OnUserClickListener

    var members = listOf<Member>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemberHolder {
        return MemberHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_member, parent, false))
    }

    override fun getItemCount() = members.size

    override fun onBindViewHolder(holder: MemberHolder, position: Int) {
        members[position].apply {
            holder.name.text = name
            holder.doj.text = doj
        }
        holder.itemView.setOnClickListener {
            //            onUserClickListener.onClick(users[position], position)
        }
    }

    inner class MemberHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: AppCompatTextView = itemView.findViewById(R.id.name)
        val doj: AppCompatTextView = itemView.findViewById(R.id.doj)
    }
}
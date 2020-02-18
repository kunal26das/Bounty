package kudos26.bounty.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import kudos26.bounty.R
import kudos26.bounty.source.model.Group

/**
 * Created by kunal on 19-01-2020.
 */

interface OnGroupClickListener {
    fun onClick(group: Group, position: Int)
}

class GroupAdapter : RecyclerView.Adapter<GroupAdapter.GroupHolder>() {

    lateinit var onGroupClickListener: OnGroupClickListener

    var groups = listOf<Group>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupHolder {
        return GroupHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_group, parent, false))
    }

    override fun getItemCount() = groups.size

    override fun onBindViewHolder(holder: GroupHolder, position: Int) {
        holder.groupName.text = groups[position].name
        holder.itemView.setOnClickListener {
            onGroupClickListener.onClick(groups[position], position)
        }
    }

    inner class GroupHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val groupName: AppCompatTextView = itemView.findViewById(R.id.groupName)
    }
}
package kudos26.bounty.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import kudos26.bounty.R

/**
 * Created by kunal on 19-01-2020.
 */

interface OnContactClickListener {
    fun onClick(contact: String, position: Int)
}

class ContactsAdapter : RecyclerView.Adapter<ContactsAdapter.ContactsHolder>() {

    lateinit var onContactClickListener: OnContactClickListener

    var contacts = listOf<String>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactsHolder {
        return ContactsHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_contact, parent, false))
    }

    override fun getItemCount() = contacts.size

    override fun onBindViewHolder(holder: ContactsHolder, position: Int) {
        holder.contact.text = contacts[position]
        holder.itemView.setOnClickListener {
            onContactClickListener.onClick(contacts[position], position)
        }
    }

    inner class ContactsHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val contact: AppCompatTextView = itemView.findViewById(R.id.contact)
    }
}
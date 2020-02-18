package kudos26.bounty.ui.contacts

import android.os.Bundle
import android.provider.ContactsContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_groups.*
import kudos26.bounty.R
import kudos26.bounty.adapter.ContactsAdapter
import kudos26.bounty.adapter.OnContactClickListener
import kudos26.bounty.core.Fragment
import kudos26.bounty.source.model.Group
import kudos26.bounty.ui.MainViewModel
import org.koin.android.viewmodel.ext.android.sharedViewModel

/**
 * Created by kunal on 20-01-2020.
 */

class ContactsFragment : Fragment() {

    private lateinit var group: Group
    private val contactsAdapter = ContactsAdapter()
    override val viewModel by sharedViewModel<MainViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_contacts, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        group = (arguments?.get("Group") as Group)

        LinearLayoutManager(context).apply {
            list.layoutManager = this
            list.adapter = contactsAdapter
            list.addItemDecoration(DividerItemDecoration(context, orientation))
        }

        contactsAdapter.onContactClickListener = object : OnContactClickListener {
            override fun onClick(email: String, position: Int) {
                viewModel.addMember(group, email)
                findNavController().navigateUp()
            }
        }

        contactsAdapter.contacts = getEmails()
    }

    override fun initObservers() {}

    private fun getEmails(): ArrayList<String> {
        val emails = ArrayList<String>()
        context?.contentResolver!!.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null)?.apply {
            if (count > 0) {
                while (moveToNext()) {
                    val id: String = getString(getColumnIndex(ContactsContract.Contacts._ID))!!
                    context?.contentResolver!!.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, null, ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?", arrayOf(id), null)?.apply {
                        while (moveToNext()) {
                            val email: String = getString(getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA))!!
                            emails.add(email)
                        }
                        close()
                    }
                }
            }
            close()
        }
        emails.sort()
        return emails
    }
}
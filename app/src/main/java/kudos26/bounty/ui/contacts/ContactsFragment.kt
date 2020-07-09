package kudos26.bounty.ui.contacts

import android.os.Bundle
import android.provider.ContactsContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.microsoft.officeuifabric.persona.IPersona
import com.microsoft.officeuifabric.persona.Persona
import kotlinx.android.synthetic.main.fragment_contacts.*
import kudos26.bounty.R
import kudos26.bounty.core.Fragment
import kudos26.bounty.source.model.Group
import kudos26.bounty.source.model.Member
import kudos26.bounty.ui.MainViewModel
import kudos26.bounty.utils.Events
import kudos26.bounty.utils.Extensions.default
import kudos26.bounty.utils.Extensions.main
import org.koin.android.viewmodel.ext.android.sharedViewModel

/**
 * Created by kunal on 20-01-2020.
 */

class ContactsFragment : Fragment() {

    private lateinit var group: Group
    override val viewModel by sharedViewModel<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        group = arguments?.getParcelable(getString(R.string.group))!!
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_contacts, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        personaList.setOnGroupClickListener {
            viewModel.invite(group, Member(
                    name = it.name,
                    email = it.email
            ))
            findNavController().navigateUp()
        }
        refreshContacts.setOnRefreshListener {
            getContacts()
        }
    }

    override fun onResume() {
        super.onResume()
        getContacts()
        viewModel.title.value = group.name
        viewModel.subtitle.value = getString(R.string.invite_member)
    }

    override fun initObservers() {
        super.initObservers()
        Events.subscribe(Group::class.java) {
            if (it.id == group.id) {
                findNavController().navigate(R.id.action_contacts_to_groups)
            }
        }
    }

    private fun getContacts() {
        default {
            val contacts = ArrayList<IPersona>()
            context?.contentResolver?.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null)?.apply {
                if (count > 0) {
                    while (moveToNext()) {
                        val id: String = getString(getColumnIndex(ContactsContract.Contacts._ID))!!
                        context?.contentResolver!!.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, null, ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?", arrayOf(id), null)?.apply {
                            while (moveToNext()) {
                                contacts.add(Persona(
                                        name = getString(getColumnIndex(ContactsContract.CommonDataKinds.Identity.DISPLAY_NAME)),
                                        email = getString(getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA))
                                ).apply { subtitle = email })
                            }
                            close()
                        }
                    }
                }
                close()
            }
            main {
                personaList.submitList(contacts)
                refreshContacts.isRefreshing = false
                contactsCount.text = when (contacts.size) {
                    0 -> getString(R.string.no_contacts)
                    1 -> getString(R.string.one_contact)
                    else -> "${contacts.size} Contacts"
                }
            }
        }
    }
}
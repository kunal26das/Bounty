package kudos26.bounty.ui.groups

import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_group_settings.*
import kotlinx.android.synthetic.main.toolbar.*
import kudos26.bounty.R
import kudos26.bounty.adapter.LoadingAdapter
import kudos26.bounty.adapter.MemberAdapter
import kudos26.bounty.core.Fragment
import kudos26.bounty.databinding.FragmentGroupSettingsBinding
import kudos26.bounty.source.model.Group
import kudos26.bounty.ui.MainViewModel
import org.koin.android.viewmodel.ext.android.sharedViewModel

/**
 * Created by kunal on 22-01-2020.
 */

class GroupSettingsFragment : Fragment() {

    private lateinit var group: Group
    private val membersAdapter = MemberAdapter()
    private val loadingAdapter = LoadingAdapter()
    override val viewModel by sharedViewModel<MainViewModel>()
    private lateinit var dataBinding: FragmentGroupSettingsBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(true)
        dataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_group_settings, container, false)
        dataBinding.lifecycleOwner = viewLifecycleOwner
        dataBinding.viewModel = viewModel
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        group = (arguments?.get("Group") as Group)
        activity?.toolbar?.title = group.name

        LinearLayoutManager(context).apply {
            list.layoutManager = this
            list.adapter = membersAdapter
            list.addItemDecoration(DividerItemDecoration(context, orientation))
        }
    }

    override fun initObservers() {
        viewModel.members.observe(this, Observer {
            list.adapter = if (it.isNotEmpty()) {
                membersAdapter.members = it
                membersAdapter
            } else {
                loadingAdapter
            }
        })
    }

    private fun updateCreatedBy(uid: String) {
        firebaseDatabase.child("users").child(uid).child("name").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    private fun navigateToContactsFragment() {
        Bundle().apply {
            putParcelable("Group", group)
            findNavController().navigate(R.id.action_group_settings_to_contacts, this)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        return inflater.inflate(R.menu.menu_add, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.add -> {
                navigateToContactsFragment()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}
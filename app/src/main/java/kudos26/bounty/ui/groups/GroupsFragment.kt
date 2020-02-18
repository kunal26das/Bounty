package kudos26.bounty.ui.groups

import android.os.Bundle
import android.view.*
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_groups.*
import kudos26.bounty.R
import kudos26.bounty.adapter.GroupAdapter
import kudos26.bounty.adapter.LoadingAdapter
import kudos26.bounty.adapter.OnGroupClickListener
import kudos26.bounty.core.Fragment
import kudos26.bounty.source.model.Group
import kudos26.bounty.ui.MainViewModel
import org.koin.android.viewmodel.ext.android.sharedViewModel

class GroupsFragment : Fragment() {

    private val groupsAdapter = GroupAdapter()
    private val loadingAdapter = LoadingAdapter()
    override val viewModel by sharedViewModel<MainViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_groups, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        LinearLayoutManager(context).apply {
            viewModel.getGroups()
            list.layoutManager = this
            list.addItemDecoration(DividerItemDecoration(context, orientation))
            groupsAdapter.onGroupClickListener = object : OnGroupClickListener {
                override fun onClick(group: Group, position: Int) {
                    Bundle().apply {
                        putParcelable("Group", group)
                        findNavController().navigate(R.id.action_home_to_group, this)
                    }
                }
            }
        }
    }

    override fun initObservers() {
        viewModel.groups.observe(this, Observer {
            list.adapter = if (it.isNotEmpty()) {
                groupsAdapter.groups = it
                groupsAdapter
            } else {
                loadingAdapter
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        return inflater.inflate(R.menu.menu_groups, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.add -> {
                findNavController().navigate(R.id.action_home_to_addGroup)
//                displayCreateGroupDialog()
                true
            }
            R.id.settings -> {
                findNavController().navigate(R.id.action_home_to_settings)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    /*private fun displayCreateGroupDialog() {
        val groupNameEditText = TextInputEditText(context!!)
        MaterialAlertDialogBuilder(context).apply {
            setTitle("Enter Group name")
            setView(groupNameEditText)
            setPositiveButton("Create") { dialog, _ ->
                val groupName = groupNameEditText.text.toString()
                dialog.dismiss()
            }
            setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            show()
        }
    }*/

}
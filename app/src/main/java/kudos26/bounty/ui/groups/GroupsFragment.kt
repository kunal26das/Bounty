package kudos26.bounty.ui.groups

import android.os.Bundle
import android.view.*
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.input.input
import kotlinx.android.synthetic.main.fragment_groups.*
import kudos26.bounty.R
import kudos26.bounty.core.Fragment
import kudos26.bounty.ui.MainViewModel
import kudos26.bounty.utils.Extensions.Try
import org.koin.android.viewmodel.ext.android.sharedViewModel

class GroupsFragment : Fragment() {

    override val viewModel by sharedViewModel<MainViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_groups, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        addGroupButton.setOnClickListener {
            MaterialDialog(requireContext()).show {
                input(
                        maxLength = 25,
                        allowEmpty = false,
                        hintRes = R.string.group_name
                ) { dialog, name ->
                    Try {
                        if (viewModel.groups.value?.size!! < 10) {
                            viewModel.createGroup(name.toString())
                        }
                    }
                    dialog.dismiss()
                }
                negativeButton { dismiss() }
                positiveButton(R.string.create)
            }

        }
        groups.setOnGroupClickListener {
            Bundle().apply {
                putParcelable(getString(R.string.group), it)
                putBoolean(getString(R.string.archive), false)
                findNavController().navigate(R.id.action_groups_to_group, this)
            }
        }
        refreshGroups.setOnRefreshListener {
            viewModel.getGroups()
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.getGroups()
        viewModel.title.value = getString(R.string.beta)
    }

    override fun initObservers() {
        super.initObservers()
        viewModel.groups.observe(this, Observer {
            groupsCount.text = when (it.size) {
                0 -> getString(R.string.no_groups)
                1 -> getString(R.string.one_group)
                else -> "${it.size} Groups"
            }
            refreshGroups.isRefreshing = false
            groups.submitList(it)
        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_groups, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.invitations -> {
                findNavController().navigate(R.id.action_groups_to_invitations)
                true
            }
            R.id.archive -> {
                findNavController().navigate(R.id.action_groups_to_archive)
                true
            }
            R.id.settings -> {
                findNavController().navigate(R.id.action_groups_to_settings)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}
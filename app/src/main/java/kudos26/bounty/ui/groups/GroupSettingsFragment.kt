package kudos26.bounty.ui.groups

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.*
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.input.input
import com.microsoft.officeuifabric.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_group_settings.*
import kudos26.bounty.R
import kudos26.bounty.core.Fragment
import kudos26.bounty.databinding.FragmentGroupSettingsBinding
import kudos26.bounty.source.model.Group
import kudos26.bounty.source.model.Member
import kudos26.bounty.ui.MainViewModel
import kudos26.bounty.utils.Events
import kudos26.bounty.utils.Extensions.default
import kudos26.bounty.utils.Extensions.main
import org.koin.android.viewmodel.ext.android.sharedViewModel

/**
 * Created by kunal on 22-01-2020.
 */

class GroupSettingsFragment : Fragment() {

    private lateinit var group: Group
    override val viewModel by sharedViewModel<MainViewModel>()
    private lateinit var dataBinding: FragmentGroupSettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        group = arguments?.getParcelable(getString(R.string.group))!!
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(true)
        dataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_group_settings, container, false)
        dataBinding.lifecycleOwner = viewLifecycleOwner
        dataBinding.viewModel = viewModel
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        addMemberButton.setOnClickListener {
            when (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_CONTACTS)) {
                PackageManager.PERMISSION_GRANTED -> {
                    Bundle().apply {
                        putParcelable(getString(R.string.group), group)
                        findNavController().navigate(R.id.action_group_settings_to_contacts, this)
                    }
                }
                PackageManager.PERMISSION_DENIED -> {
                    // TODO Request Permission
                }
            }
        }
        refreshMembers.setOnRefreshListener {
            viewModel.getMembers(group)
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.getMembers(group)
        viewModel.group.value = group
        viewModel.title.value = group.name
        viewModel.subtitle.value = "Settings"
    }

    override fun initObservers() {
        super.initObservers()
        viewModel.members.observe(this, Observer {
            members.submitList(it)
            groupName.text = when (it.size) {
                0 -> "No Members"
                1 -> "1 Member"
                else -> "${it.size} Members"
            }
            refreshMembers.isRefreshing = false
            if (it.fold(false) { isAdmin, member ->
                        isAdmin || member.uid == viewModel.uid.value && member.isAdmin
                    }) {
                members.adminAccess = true
                editGroupName.visibility = View.VISIBLE
                editGroupName.setOnClickListener {
                    MaterialDialog(requireContext()).show {
                        input(
                                maxLength = 25,
                                allowEmpty = false,
                                prefill = group.name,
                                hintRes = R.string.group_name
                        ) { dialog, name ->
                            viewModel.renameGroup(group, name.toString())
                            dialog.dismiss()
                        }
                        negativeButton { dismiss() }
                        positiveButton(R.string.change)
                    }
                }
                members.setOnMemberClickListener { action, member ->
                    when (action.id) {
                        R.id.remove -> viewModel.remove(group, member)
                        R.id.admin -> viewModel.makeAdmin(group, member)
                    }
                }
            } else {
                members.adminAccess = false
                editGroupName.setOnClickListener {}
                editGroupName.visibility = View.GONE
                members.setOnMemberClickListener { _, _ -> }
            }
        })
        Events.subscribe(Group::class.java) {
            if (it.id == group.id) {
                findNavController().navigate(GroupSettingsFragmentDirections.actionGroupSettingsToGroups())
            }
        }
        Events.subscribe(Member::class.java) {
            Snackbar.make(root, getString(R.string.not_signed_up_yet), Snackbar.LENGTH_LONG).show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_group_settings, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.leave -> {
                default {
                    viewModel.members.value?.toMutableList()?.let { list ->
                        list.find { it.uid == viewModel.uid.value }?.let {
                            list.removeAt(list.indexOf(it))
                        }
                        val members = list.size
                        val admins = list.fold(0) { admins, member ->
                            if (member.isAdmin) admins + 1
                            else admins
                        }
                        if (admins > 0 || members == 0) main {
                            Snackbar.make(root, getString(R.string.are_you_sure), Snackbar.LENGTH_SHORT).apply {
                                setAction(getString(R.string.leave), View.OnClickListener {
                                    findNavController().navigate(GroupSettingsFragmentDirections.actionGroupSettingsToGroups())
                                    viewModel.leave(group)
                                })
                            }.show()
                        } else main {
                            Snackbar.make(root, getString(R.string.transfer_your_admin_access_first), Snackbar.LENGTH_LONG).show()
                        }
                    }
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.group.value = Group()
        viewModel.members.value = emptyList()
    }

}
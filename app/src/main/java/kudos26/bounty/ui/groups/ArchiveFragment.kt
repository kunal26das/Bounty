package kudos26.bounty.ui.groups

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.fragment_archive.*
import kudos26.bounty.R
import kudos26.bounty.core.Fragment
import kudos26.bounty.ui.MainViewModel
import org.koin.android.viewmodel.ext.android.sharedViewModel

class ArchiveFragment : Fragment() {

    override val viewModel by sharedViewModel<MainViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_archive, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        archive.setOnGroupClickListener {
            Bundle().apply {
                putParcelable(getString(R.string.group), it)
                putBoolean(getString(R.string.archive), true)
                findNavController().navigate(R.id.action_archive_to_group, this)
            }
        }
        refreshArchive.setOnRefreshListener {
            viewModel.getArchivedGroups()
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.getArchivedGroups()
        viewModel.title.value = findNavController().currentDestination?.label?.toString()
    }

    override fun initObservers() {
        super.initObservers()
        viewModel.archive.observe(this, Observer {
            archiveSize.text = when (it.size) {
                0 -> getString(R.string.archive_is_empty)
                1 -> "1 Archived Group"
                else -> "${it.size} Archived Groups"
            }
            refreshArchive.isRefreshing = false
            archive.submitList(it)
        })
    }

}
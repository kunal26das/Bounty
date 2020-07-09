package kudos26.bounty.ui.groups

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.BaseTransientBottomBar.BaseCallback.DISMISS_EVENT_ACTION
import com.microsoft.officeuifabric.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_invitations.*
import kudos26.bounty.R
import kudos26.bounty.core.Fragment
import kudos26.bounty.ui.MainViewModel
import kudos26.bounty.utils.Extensions.setOnDismissListener
import org.koin.android.viewmodel.ext.android.sharedViewModel

class InvitationsFragment : Fragment() {

    override val viewModel by sharedViewModel<MainViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_invitations, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        invitations.setOnSwipeToDeleteListener {
            Snackbar.make(root, getString(R.string.invitation_deleted), Snackbar.LENGTH_LONG).apply {
                setOnDismissListener { _, event ->
                    if (event != DISMISS_EVENT_ACTION) {
                        viewModel.declineInvitation(it)
                    }
                }
                setAction(getString(R.string.undo), View.OnClickListener {
                    viewModel.getInvitations()
                })
            }.show()
        }
        invitations.setOnSwipeToAcceptListener {
            viewModel.joinGroup(it)
        }
        refreshInvitations.setOnRefreshListener {
            viewModel.getInvitations()
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.getInvitations()
        viewModel.title.value = findNavController().currentDestination?.label?.toString()
    }

    override fun initObservers() {
        super.initObservers()
        viewModel.invitations.observe(this, Observer {
            invitationsCount.text = when (it.size) {
                0 -> getString(R.string.no_invitations)
                1 -> getString(R.string.one_invitation)
                else -> "${it.size} Invitations"
            }
            refreshInvitations.isRefreshing = false
            invitations.submitList(it)
        })
    }

}
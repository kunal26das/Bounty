package kudos26.bounty.ui.transactions

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.android.material.textview.MaterialTextView
import com.microsoft.officeuifabric.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_transaction.*
import kudos26.bounty.R
import kudos26.bounty.core.Fragment
import kudos26.bounty.databinding.FragmentTransactionBinding
import kudos26.bounty.source.model.Group
import kudos26.bounty.source.model.Transaction
import kudos26.bounty.ui.MainViewModel
import kudos26.bounty.utils.CalendarUtils.date
import kudos26.bounty.utils.Events
import kudos26.bounty.utils.Extensions.amount
import org.koin.android.viewmodel.ext.android.sharedViewModel

/**
 * Created by kunal on 23-01-2020.
 */

class TransactionFragment : Fragment() {

    private var archive = false
    private lateinit var group: Group
    private lateinit var transaction: Transaction
    override val viewModel by sharedViewModel<MainViewModel>()
    private lateinit var dataBinding: FragmentTransactionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        group = arguments?.getParcelable(getString(R.string.group))!!
        archive = arguments?.getBoolean(getString(R.string.archive))!!
        transaction = arguments?.getParcelable(getString(R.string.transaction))!!
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_transaction, container, false)
        dataBinding.lifecycleOwner = viewLifecycleOwner
        dataBinding.viewModel = viewModel
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        MaterialTextView(requireContext()).let {
            it.textSize = 20f
            it.text = "₹${transaction.amount.amount}"
            total.customAccessoryView = it
        }
        editTransactionButton.setOnClickListener {
            Bundle().apply {
                putParcelable(getString(R.string.group), group)
                putParcelable(getString(R.string.transaction), transaction)
                findNavController().navigate(R.id.action_transaction_to_add_transaction, this)
            }
        }
        refreshShares.setOnRefreshListener {
            viewModel.getShares(group, transaction)
        }
        deleteTransactionButton.setOnClickListener {
            Snackbar.make(root, getString(R.string.are_you_sure), Snackbar.LENGTH_LONG).apply {
                setAction(getString(R.string.delete), View.OnClickListener {
                    findNavController().navigateUp()
                    viewModel.deleteTransaction(group, transaction)
                })
            }.show()
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.getShares(group, transaction)
        viewModel.title.value = transaction.comment
        viewModel.subtitle.value = transaction.date.date
    }

    override fun initObservers() {
        super.initObservers()
        viewModel.uid.observe(viewLifecycleOwner, Observer {
            (it == transaction.payer.uid).let { visible ->
                deleteTransactionButton.visibility = when (visible and !archive) {
                    true -> {
                        editTransactionButton.show()
                        View.VISIBLE
                    }
                    else -> {
                        editTransactionButton.hide()
                        View.GONE
                    }
                }
            }
        })
        viewModel.transaction.observe(viewLifecycleOwner, Observer {
            sharesCount.text = when (it.impact.size) {
                0 -> "No Shares"
                1 -> "1 Share"
                else -> "${it.impact.size} Shares"
            }
            refreshShares.isRefreshing = false
            shares.submitList(it.impact)
        })
        Events.subscribe(Group::class.java) {
            if (it.id == group.id) {
                findNavController().navigate(R.id.action_transaction_to_groups)
            }
        }
    }

}
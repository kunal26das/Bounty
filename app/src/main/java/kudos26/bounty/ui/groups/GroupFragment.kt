package kudos26.bounty.ui.groups

import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_group.*
import kotlinx.android.synthetic.main.toolbar.*
import kudos26.bounty.R
import kudos26.bounty.adapter.LoadingAdapter
import kudos26.bounty.adapter.OnTransactionClickListener
import kudos26.bounty.adapter.OnTransactionLongClickListener
import kudos26.bounty.adapter.TransactionAdapter
import kudos26.bounty.core.Fragment
import kudos26.bounty.databinding.FragmentGroupBinding
import kudos26.bounty.source.model.Group
import kudos26.bounty.source.model.Transaction
import kudos26.bounty.ui.MainViewModel
import org.koin.android.viewmodel.ext.android.sharedViewModel

/**
 * Created by kunal on 20-01-2020.
 */

class GroupFragment : Fragment() {

    private lateinit var group: Group
    private val loadingAdapter = LoadingAdapter()
    private val transactionAdapter = TransactionAdapter()
    private lateinit var dataBinding: FragmentGroupBinding
    override val viewModel by sharedViewModel<MainViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(true)
        dataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_group, container, false)
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
            viewModel.getShares(group)
            viewModel.getMembers(group)
            viewModel.getTransactions(group)
            list.addItemDecoration(DividerItemDecoration(context, orientation))
            transactionAdapter.onTransactionClickListener = object : OnTransactionClickListener {
                override fun onClick(transaction: Transaction, position: Int) {
                    navigateToTransactionFragment(transaction)
                }
            }
            transactionAdapter.onTransactionLongClickListener = object : OnTransactionLongClickListener {
                override fun onLongClick(transaction: Transaction, position: Int) {

                }
            }
        }
    }

    override fun initObservers() {
        viewModel.transactions.observe(this, Observer {
            list?.adapter = if (it.isNotEmpty()) {
                totalAmount?.text = "₹${transactionAdapter.getTotalAmount()}"
                transactionAdapter.transactions = it
                transactionAdapter
            } else {
                loadingAdapter
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        return inflater.inflate(R.menu.menu_group, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.add -> {
                navigateToAddTransactionFragment()
                true
            }
            R.id.settings -> {
                navigateToParticipantsFragment()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun navigateToParticipantsFragment() {
        Bundle().apply {
            putParcelable("Group", group)
            findNavController().navigate(R.id.action_group_to_group_settings, this)
        }
    }

    private fun navigateToAddTransactionFragment() {
        Bundle().apply {
            putParcelable("Group", group)
            findNavController().navigate(R.id.action_group_to_addTransaction, this)
        }
    }

    private fun navigateToTransactionFragment(transaction: Transaction) {
        Bundle().apply {
            putParcelable("Group", group)
            putParcelable("Transaction", transaction)
            findNavController().navigate(R.id.action_group_to_transaction, this)
        }
    }
}
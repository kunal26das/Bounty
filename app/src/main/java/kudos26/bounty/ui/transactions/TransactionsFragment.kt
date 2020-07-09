package kudos26.bounty.ui.transactions

import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.microsoft.officeuifabric.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_transactions.*
import kudos26.bounty.R
import kudos26.bounty.core.Fragment
import kudos26.bounty.databinding.FragmentTransactionsBinding
import kudos26.bounty.source.model.Group
import kudos26.bounty.ui.MainViewModel
import kudos26.bounty.utils.Events
import org.koin.android.viewmodel.ext.android.sharedViewModel

/**
 * Created by kunal on 20-01-2020.
 */

class TransactionsFragment : Fragment() {

    private lateinit var group: Group
    private var archive: Boolean = true
    private lateinit var dataBinding: FragmentTransactionsBinding
    override val viewModel by sharedViewModel<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        group = arguments?.getParcelable(getString(R.string.group))!!
        archive = arguments?.getBoolean(getString(R.string.archive))!!
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(true)
        dataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_transactions, container, false)
        dataBinding.lifecycleOwner = viewLifecycleOwner
        dataBinding.viewModel = viewModel
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        when (archive) {
            true -> {
                deleteGroupButton.show()
                addTransactionButton.hide()
            }
            false -> {
                deleteGroupButton.hide()
                addTransactionButton.show()
            }
        }
        transactions.setOnTransactionClickListener {
            if (!archive) {
                Bundle().apply {
                    putParcelable(getString(R.string.group), group)
                    putBoolean(getString(R.string.archive), archive)
                    putParcelable(getString(R.string.transaction), it)
                    findNavController().navigate(R.id.action_group_to_transaction, this)
                }
            }
        }
        addTransactionButton.setOnClickListener {
            if (!archive) {
                Bundle().apply {
                    putParcelable(getString(R.string.group), group)
                    findNavController().navigate(R.id.action_group_to_add_transaction, this)
                }
            }
        }
        refreshTransactions.setOnRefreshListener {
            viewModel.getTransactions(group)
        }
        deleteGroupButton.setOnClickListener {
            if (archive) {
                Snackbar.make(root, getString(R.string.are_you_sure), Snackbar.LENGTH_SHORT).apply {
                    setAction(getString(R.string.delete), View.OnClickListener {
                        findNavController().navigateUp()
                        viewModel.deleteGroup(group)
                    })
                }.show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.getTransactions(group)
        viewModel.title.value = group.name
    }

    override fun initObservers() {
        super.initObservers()
        viewModel.transactions.observe(viewLifecycleOwner, Observer {
            transactionsCount.text = when (it.size) {
                0 -> getString(R.string.no_transactions)
                1 -> getString(R.string.one_transaction)
                else -> "${it.size} Transactions"
            }
            refreshTransactions.isRefreshing = false
            transactions.submitList(it.reversed())
        })
        Events.subscribe(Group::class.java) {
            if (it.id == group.id) {
                findNavController().navigateUp()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        if (!archive) {
            inflater.inflate(R.menu.menu_settings, menu)
            super.onCreateOptionsMenu(menu, inflater)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.settings -> {
                Bundle().apply {
                    putParcelable(getString(R.string.group), group)
                    findNavController().navigate(R.id.action_group_to_group_settings, this)
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.transactions.value = emptyList()
    }

}
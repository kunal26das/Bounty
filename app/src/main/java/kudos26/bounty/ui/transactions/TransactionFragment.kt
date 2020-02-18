package kudos26.bounty.ui.transactions

import android.os.Bundle
import android.view.*
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_transaction.*
import kotlinx.android.synthetic.main.toolbar.*
import kudos26.bounty.R
import kudos26.bounty.adapter.ShareAdapter
import kudos26.bounty.core.Fragment
import kudos26.bounty.source.model.Group
import kudos26.bounty.source.model.Share
import kudos26.bounty.source.model.Transaction
import kudos26.bounty.ui.MainViewModel
import kudos26.bounty.utils.toDisplayDate
import org.koin.android.viewmodel.ext.android.sharedViewModel

/**
 * Created by kunal on 23-01-2020.
 */

class TransactionFragment : Fragment() {

    private lateinit var group: Group
    private val ratioAdapter = ShareAdapter()
    private lateinit var transaction: Transaction
    override val viewModel by sharedViewModel<MainViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_transaction, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        transaction = arguments?.getParcelable("Transaction")!!
        activity?.toolbar?.title = transaction.comment
        group = arguments?.getParcelable("Group")!!

        transactionAmount.text = "₹${transaction.amount}"
        transactionPaidBy.text = "Paid by ${transaction.from} on ${transaction.date.toDisplayDate()}"

        getMembers()

        LinearLayoutManager(context).apply {
            list.adapter = ratioAdapter
            list.layoutManager = this
            list.addItemDecoration(DividerItemDecoration(context, orientation))
        }
    }

    override fun initObservers() {}

    private fun getMembers() {
        firebaseDatabase.child("groups").child(group.id).child("members").apply {
            addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val ratios = mutableListOf<Share>()
                    transaction.to.forEach {
                        Share().apply {
                            name = dataSnapshot.child(it.uid).child("name").value as String
                            amount = (transaction.amount!! * it.percentage).toInt()
                            ratios += this
                        }
                    }
                    ratioAdapter.shares = ratios
                }

                override fun onCancelled(databaseError: DatabaseError) {

                }
            })
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        return inflater.inflate(R.menu.menu_edit, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.edit -> {
                Bundle().apply {
                    putParcelable("Group", group)
                    putParcelable("Transaction", transaction)
                    findNavController().navigate(R.id.action_transaction_to_addTransaction, this)
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}
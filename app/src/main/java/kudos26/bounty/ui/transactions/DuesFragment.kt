package kudos26.bounty.ui.transactions

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.fragment_dues.*
import kudos26.bounty.R
import kudos26.bounty.core.Fragment
import kudos26.bounty.databinding.FragmentDuesBinding
import kudos26.bounty.source.model.*
import kudos26.bounty.ui.MainViewModel
import kudos26.bounty.utils.CalendarUtils.currentDate
import kudos26.bounty.utils.Events
import kudos26.bounty.utils.Extensions.Try
import kudos26.bounty.utils.Extensions.default
import kudos26.bounty.utils.Extensions.main
import org.koin.android.viewmodel.ext.android.sharedViewModel

class DuesFragment : Fragment() {

    private lateinit var group: Group
    private var archive: Boolean = true
    private lateinit var dataBinding: FragmentDuesBinding
    override val viewModel by sharedViewModel<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        group = arguments?.getParcelable(getString(R.string.group))!!
        archive = arguments?.getBoolean(getString(R.string.archive))!!
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(true)
        dataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_dues, container, false)
        dataBinding.lifecycleOwner = viewLifecycleOwner
        dataBinding.viewModel = viewModel
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        refreshDues.setOnRefreshListener {
            viewModel.getDues(group)
        }
        dues.setOnDueClickListener {
            reimburse(it)
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.getDues(group)
        viewModel.title.value = group.name
    }

    override fun initObservers() {
        super.initObservers()
        viewModel.dues.observe(viewLifecycleOwner, Observer {
            default {
                it.filter { it.amount != 0L }.main {
                    duesCount.text = when (size) {
                        0 -> getString(R.string.no_dues)
                        else -> "${it.size} Dues"
                    }
                    refreshDues.isRefreshing = false
                    dues.submitList(this)
                }
            }
        })
        Events.subscribe(Group::class.java) {
            if (it.id == group.id) {
                findNavController().navigateUp()
            }
        }
    }

    private fun reimburse(due: Due) {
        Intent(Intent.ACTION_VIEW).apply {
            data = Uri.Builder().apply {
                scheme("upi")
                authority("pay")
                appendQueryParameter("cu", "INR")
                appendQueryParameter("am", "${due.amount}")
                appendQueryParameter("pa", due.creditor.upi)
                appendQueryParameter("pn", due.creditor.name)
                appendQueryParameter("uid", due.creditor.uid)
                appendQueryParameter("tn", getString(R.string.reimbursement))
            }.build()
            startActivityForResult(Intent.createChooser(this, "Pay with"), UPI_PAYMENT)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == UPI_PAYMENT && (resultCode == RESULT_OK || resultCode == 11) && data != null) Try {
            val amount = data.data?.getQueryParameter("am")?.toLong()!!
            val creditor = data.data?.getQueryParameter("uid")!!
            viewModel.addTransaction(group, Transaction(
                    date = currentDate,
                    amount = amount,
                    comment = getString(R.string.reimbursement),
                    impact = listOf(Share(
                            member = Member(creditor),
                            percentage = 100
                    ))
            ))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.dues.value = emptyList()
    }

    companion object {
        const val UPI_PAYMENT = 101
    }

}
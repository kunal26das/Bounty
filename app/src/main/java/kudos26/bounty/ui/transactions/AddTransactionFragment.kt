package kudos26.bounty.ui.transactions

import android.os.Bundle
import android.view.*
import androidx.core.widget.doAfterTextChanged
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.InterstitialAd
import kotlinx.android.synthetic.main.fragment_add_transaction.*
import kudos26.bounty.R
import kudos26.bounty.adapter.AddShareAdapter
import kudos26.bounty.core.Fragment
import kudos26.bounty.databinding.FragmentAddTransactionBinding
import kudos26.bounty.source.model.Group
import kudos26.bounty.source.model.Transaction
import kudos26.bounty.ui.MainViewModel
import org.koin.android.viewmodel.ext.android.sharedViewModel

/**
 * Created by kunal on 22-01-2020.
 */

class AddTransactionFragment : Fragment() {

    private lateinit var group: Group
    private val addShareAdapter = AddShareAdapter()
    override val viewModel by sharedViewModel<MainViewModel>()
    private lateinit var dataBinding: FragmentAddTransactionBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(true)
        dataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_add_transaction, container, false)
        dataBinding.lifecycleOwner = viewLifecycleOwner
        dataBinding.viewModel = viewModel
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        group = arguments?.getParcelable("Group")!!

        list.adapter = addShareAdapter
        list.layoutManager = LinearLayoutManager(context)

        transactionAmount.doAfterTextChanged {
            addShareAdapter.amount = try {
                it.toString().toInt()
            } catch (numberFormatException: NumberFormatException) {
                0
            }
        }

        transactionAmountLayout.setEndIconOnClickListener {
            addShareAdapter.divideEqually()
        }

        InterstitialAd(context).apply {
            adUnitId = "ca-app-pub-2292299294214510/4420687397"
            loadAd(AdRequest.Builder().build())
            show()
        }
    }

    override fun onStart() {
        super.onStart()
        viewModel.transaction.value = arguments?.getParcelable("Transaction")
        if (viewModel.transaction.value == null) {
            viewModel.transaction.value = Transaction()
        }
    }

    override fun initObservers() {
        viewModel.shares.observe(this, Observer {
            list.adapter = if (it.isNotEmpty()) {
                addShareAdapter.shares = it
                addShareAdapter
            } else {
                null
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        return inflater.inflate(R.menu.menu_done, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.done -> {
                if (viewModel.transaction.value?.amount != 0) {
                    viewModel.addTransaction(group)
                    findNavController().navigateUp()
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}
package kudos26.bounty.ui.transactions

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.databinding.DataBindingUtil
import androidx.essentials.events.Events
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.InterstitialAd
import com.microsoft.officeuifabric.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_add_transaction.*
import kudos26.bounty.BuildConfig
import kudos26.bounty.R
import kudos26.bounty.core.Fragment
import kudos26.bounty.databinding.FragmentAddTransactionBinding
import kudos26.bounty.source.model.Group
import kudos26.bounty.source.model.Transaction
import kudos26.bounty.ui.MainViewModel
import kudos26.bounty.utils.CalendarUtils.date
import kudos26.bounty.utils.CalendarUtils.simpleDateFormat
import org.koin.android.viewmodel.ext.android.sharedViewModel
import java.util.*

/**
 * Created by kunal on 22-01-2020.
 */

class AddTransactionFragment : Fragment() {

    private lateinit var group: Group
    private var transaction: Transaction? = null
    private val calendar = Calendar.getInstance()
    private lateinit var interstitialAd: InterstitialAd
    private lateinit var datePickerDialog: DatePickerDialog
    override val viewModel by sharedViewModel<MainViewModel>()
    private lateinit var dataBinding: FragmentAddTransactionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        group = arguments?.getParcelable(getString(R.string.group))!!
        transaction = arguments?.getParcelable(getString(R.string.transaction))
        viewModel.transaction.value = transaction ?: Transaction()
        interstitialAd = InterstitialAd(context).apply {
            adUnitId = when (BuildConfig.DEBUG) {
                true -> getString(R.string.admob_test_interstitial_video_ad_unit_id)
                else -> getString(R.string.admob_interstitial_ad_unit_id)
            }
            loadAd(AdRequest.Builder().build())
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        initDatePickerDialog()
        dataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_add_transaction, container, false)
        dataBinding.lifecycleOwner = viewLifecycleOwner
        dataBinding.viewModel = viewModel
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        transactionAmountLayout.setEndIconOnClickListener {
            mutableShares.divideEqually()
        }
        // TODO Format to comma separated amount
        transactionAmount.doAfterTextChanged {
            transactionAmountLayout.error = ""
            mutableShares.sum = try {
                it.toString().toLong()
            } catch (e: NumberFormatException) {
                0
            }
        }
        transactionDateLayout.editText?.apply {
            keyListener = null
            setOnFocusChangeListener { view, hasFocus ->
                if (hasFocus) {
                    hideKeyboard(view)
                    datePickerDialog.show()
                }
            }
            setOnClickListener {
                datePickerDialog.show()
            }
        }
        addTransactionButton.setOnClickListener {
            viewModel.transaction.value?.apply {
                val flag1 = amount > 0
                val flag2 = comment.isNotEmpty()
                val flag3 = mutableShares.validate()
                if (flag1 and flag2 and flag3) {
                    viewModel.addTransaction(group, this)
                    findNavController().navigateUp()
                    interstitialAd.show()
                } else when {
                    !flag1 -> transactionAmountLayout.requestFocus()
                    !flag2 -> transactionCommentLayout.requestFocus()
                    !flag3 -> Snackbar.make(root, "Share Aggregate & Total Amount are Unequal", Snackbar.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun initDatePickerDialog() {
        datePickerDialog = DatePickerDialog(
                requireContext(),
                DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                    calendar.set(year, month, dayOfMonth, 0, 0)
                    transactionDate.setText(calendar.time.time.date)
                    viewModel.transaction.value?.date = simpleDateFormat.format(calendar.time).toString()
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.datePicker.maxDate = calendar.timeInMillis
    }

    override fun onResume() {
        super.onResume()
        viewModel.subtitle.value = when (transaction == null) {
            true -> "Add Transaction"
            else -> "Edit Transaction"
        }
        viewModel.title.value = group.name
        viewModel.getShares(group, transaction ?: Transaction())
    }

    override fun initObservers() {
        super.initObservers()
        viewModel.transaction.observe(viewLifecycleOwner, Observer {
            transactionDate.setText(it.date.date)
            mutableShares.submitList(it.impact)
        })
        Events.subscribe(Group::class.java) {
            if (it.id == group.id) {
                findNavController().navigate(R.id.action_add_transaction_to_groups)
            }
        }
    }

}
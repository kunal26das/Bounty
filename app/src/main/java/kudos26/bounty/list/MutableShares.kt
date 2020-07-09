package kudos26.bounty.list

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.SeekBar
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DatabaseReference
import kudos26.bounty.R
import kudos26.bounty.adapter.LoadingAdapter
import kudos26.bounty.databinding.ItemMutableShareBinding
import kudos26.bounty.firebase.Extensions.name
import kudos26.bounty.firebase.Extensions.observeValue
import kudos26.bounty.firebase.Extensions.user
import kudos26.bounty.source.model.Share
import kudos26.bounty.source.model.Share.Companion.shareDiffCallback
import kudos26.bounty.utils.Extensions.Try
import kudos26.bounty.utils.Extensions.amount
import kudos26.bounty.utils.Extensions.default
import kudos26.bounty.utils.Extensions.main
import org.koin.core.KoinComponent
import org.koin.core.inject

class MutableShares(
        context: Context,
        attributes: AttributeSet? = null
) : RecyclerView(context, attributes), KoinComponent {

    private val loadingAdapter = LoadingAdapter()
    private val database: DatabaseReference by inject()
    private val mutableSharesAdapter = MutableSharesAdapter()
    private val linearLayoutManager = LinearLayoutManager(context)
    private val inputMethodManager: InputMethodManager by inject()

    var sum: Long = 0
        set(value) {
            field = value
            mutableSharesAdapter.sum = value
        }

    init {
        adapter = loadingAdapter
        layoutManager = linearLayoutManager
    }

    fun submitList(list: List<Share>?) {
        adapter = when (list) {
            null -> loadingAdapter
            else -> {
                mutableSharesAdapter.submitList(list)
                mutableSharesAdapter
            }
        }
    }

    fun validate() = mutableSharesAdapter.validate()
    fun divideEqually() = mutableSharesAdapter.divideEqually()

    inner class MutableSharesAdapter : ListAdapter<Share, MutableShareHolder>(shareDiffCallback) {

        var sum: Long = 0
            set(value) {
                field = value
                default {
                    currentList.forEach {
                        it.amount = (value * it.percentage / 100)
                    }
                    main { notifyDataSetChanged() }
                }
            }

        fun divideEqually() = Try {
            val share = sum / itemCount
            default {
                currentList.forEach {
                    it.amount = share
                    it.percentage = 100 / itemCount
                }
                main { notifyDataSetChanged() }
            }
        }

        fun validate(): Boolean {
            return currentList.fold(0) { sum, share ->
                sum + share.percentage
            } == 100
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MutableShareHolder {
            return MutableShareHolder(
                    DataBindingUtil.inflate(
                            LayoutInflater.from(parent.context),
                            R.layout.item_mutable_share,
                            parent, false
                    )
            )
        }

        override fun onBindViewHolder(holder: MutableShareHolder, position: Int) {
            holder.setIsRecyclable(false)
            holder.mutableShare.apply {
                share = getItem(position)
                executePendingBindings()
                memberAmount.setText(getItem(position).amount.amount)
                memberShare.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                        getItem(position).apply {
                            amount = (sum * progress / 100)
                            memberAmount.setText(amount.amount)
                            if (fromUser) percentage = progress
                            memberName.suffixText = "$progress%"
                        }
                    }

                    override fun onStartTrackingTouch(seekBar: SeekBar?) {}
                    override fun onStopTrackingTouch(seekBar: SeekBar?) {}
                })
                memberName.editText?.apply {
                    keyListener = null
                    setOnFocusChangeListener { view, hasFocus ->
                        if (hasFocus) hideKeyboard(view)
                    }
                }
                database.user(getItem(position).member.uid).name.observeValue({
                    Try {
                        (it.value as String).let {
                            getItem(position).member.name = it
                            memberName.hint = it
                        }
                    }
                })
            }
        }
    }

    inner class MutableShareHolder(val mutableShare: ItemMutableShareBinding) : RecyclerView.ViewHolder(mutableShare.root)

    private fun hideKeyboard(view: View) = inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
}
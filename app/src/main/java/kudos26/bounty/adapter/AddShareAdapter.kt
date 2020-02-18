package kudos26.bounty.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.appcompat.widget.AppCompatSeekBar
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kudos26.bounty.R
import kudos26.bounty.source.model.Ratio
import kudos26.bounty.source.model.Share

/**
 * Created by kunal on 19-01-2020.
 */

class AddShareAdapter : RecyclerView.Adapter<AddShareAdapter.AddShareHolder>() {

    var amount = 0
        set(value) {
            field = value
            updateAmount()
        }

    var shares = mapOf<Share, Ratio>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    var holders = mutableListOf<AddShareHolder>()

    fun divideEqually() {
        val a = "${amount / itemCount}"
        holders.forEach {
            it.memberAmount.setText(a)
        }
    }

    private fun updateAmount() {
        holders.forEach {
            it.memberAmount.setText("${amount * it.memberShare.progress / 100}")
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddShareHolder {
        val holder = AddShareHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_add_share, parent, false))
        holders.plusAssign(holder)
        return holder
    }

    override fun getItemCount() = shares.keys.size

    override fun onBindViewHolder(holder: AddShareHolder, position: Int) {
        holder.apply {
            setIsRecyclable(false)
            memberName.hint = shares.keys.elementAt(position).name
            memberAmount.setText("${shares.keys.elementAt(position).amount}")
            memberAmount.doAfterTextChanged {
                memberShare.progress = try {
                    val temp = memberAmount.text.toString().toInt()
                    shares.values.elementAt(position).percentage = temp / amount.toDouble()
                    temp * 100 / amount
                } catch (numberFormatException: NumberFormatException) {
                    shares.values.elementAt(position).percentage = 0.0
                    0
                } catch (arithmeticException: ArithmeticException) {
                    shares.values.elementAt(position).percentage = 0.0
                    0
                }
            }
            memberShare.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                var flag = false
                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                    if (flag) {
                        shares.values.elementAt(position).percentage = progress / 100.0
                        shares.keys.elementAt(position).amount = amount * progress / 100
                        memberAmount.setText("${shares.keys.elementAt(position).amount}")
                    }
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {
                    flag = true
                }

                override fun onStopTrackingTouch(seekBar: SeekBar?) {
                    flag = false
                }

            })
        }
    }

    inner class AddShareHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val memberName: TextInputLayout = itemView.findViewById(R.id.memberName)
        val memberShare: AppCompatSeekBar = itemView.findViewById(R.id.memberShare)
        val memberAmount: TextInputEditText = itemView.findViewById(R.id.memberAmount)
    }
}
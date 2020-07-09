package kudos26.bounty.source.model

import android.os.Parcelable
import androidx.recyclerview.widget.DiffUtil
import com.google.gson.JsonDeserializer
import com.google.gson.JsonObject
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import kudos26.bounty.utils.CalendarUtils.currentDate

/**
 * Created by kunal on 21-01-2020.
 */

@Parcelize
data class Transaction(
        @SerializedName("id")
        var id: String = "",

        @SerializedName("amount")
        var amount: Long = 0,

        @SerializedName("comment")
        var comment: String = "",

        @SerializedName("payer")
        var payer: Member = Member(),

        @SerializedName("date")
        var date: String = currentDate,

        @SerializedName("impact")
        var impact: List<Share> = emptyList()
) : Parcelable {

    companion object {

        val transactionDiffCallback = object : DiffUtil.ItemCallback<Transaction>() {
            override fun areItemsTheSame(oldItem: Transaction, newItem: Transaction): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Transaction, newItem: Transaction): Boolean {
                return oldItem == newItem
            }
        }

        var transactionDeserializer: JsonDeserializer<Transaction?> =
                JsonDeserializer { json, _, context ->
                    val jsonObject: JsonObject = json.asJsonObject
                    Transaction(
                            jsonObject.get("id").asString,
                            jsonObject.get("amount").asLong,
                            jsonObject.get("comment").asString,
                            Member.memberDeserializer.deserialize(
                                    jsonObject.get("payer"),
                                    Member::class.java,
                                    context
                            ),
                            jsonObject.get("date").asString
                    )
                }
    }
}
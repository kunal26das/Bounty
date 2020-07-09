package kudos26.bounty.source.model

import android.os.Parcelable
import androidx.recyclerview.widget.DiffUtil
import com.google.gson.JsonDeserializer
import com.google.gson.JsonObject
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import kudos26.bounty.source.model.Member.Companion.memberDeserializer

/**
 * Created by kunal on 24-01-2020.
 */

@Parcelize
data class Share(
        @SerializedName("amount")
        var amount: Long = 0,

        @SerializedName("percentage")
        var percentage: Int = 0,

        @SerializedName("member")
        var member: Member = Member()
) : Parcelable {

    companion object {

        val shareDiffCallback = object : DiffUtil.ItemCallback<Share>() {
            override fun areItemsTheSame(oldItem: Share, newItem: Share): Boolean {
                return oldItem.member.uid == newItem.member.uid
            }

            override fun areContentsTheSame(oldItem: Share, newItem: Share): Boolean {
                return oldItem == newItem
            }
        }

        var shareDeserializer: JsonDeserializer<Share?> =
                JsonDeserializer { json, _, context ->
                    val jsonObject: JsonObject = json.asJsonObject
                    Share(
                            jsonObject.get("amount").asLong,
                            jsonObject.get("percentage").asInt,
                            memberDeserializer.deserialize(
                                    jsonObject.get("member"),
                                    Member::class.java,
                                    context
                            )
                    )
                }
    }
}
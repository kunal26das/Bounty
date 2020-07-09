package kudos26.bounty.source.model

import android.os.Parcelable
import androidx.recyclerview.widget.DiffUtil
import com.google.gson.JsonDeserializer
import com.google.gson.JsonObject
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

/**
 * Created by kunal on 28-01-2020.
 */

@Parcelize
data class Member(
        @SerializedName("uid")
        var uid: String = "",

        @SerializedName("name")
        var name: String = "",

        var upi: String = "",
        var date: String = "",
        var email: String = "",
        var isAdmin: Boolean = false
) : Parcelable {

    companion object {

        val memberDiffCallback = object : DiffUtil.ItemCallback<Member>() {
            override fun areItemsTheSame(oldItem: Member, newItem: Member): Boolean {
                return oldItem.uid == newItem.uid
            }

            override fun areContentsTheSame(oldItem: Member, newItem: Member): Boolean {
                return oldItem == newItem
            }
        }

        var memberDeserializer: JsonDeserializer<Member> =
                JsonDeserializer { json, _, _ ->
                    val jsonObject: JsonObject = json.asJsonObject
                    Member(
                            jsonObject.get("uid").asString,
                            jsonObject.get("name").asString
                    )
                }
    }
}
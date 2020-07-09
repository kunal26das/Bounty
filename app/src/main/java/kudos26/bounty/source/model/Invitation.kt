package kudos26.bounty.source.model

import android.os.Parcelable
import androidx.recyclerview.widget.DiffUtil
import com.google.gson.JsonDeserializer
import com.google.gson.JsonObject
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Invitation(
        @SerializedName("id")
        var id: String = "",

        @SerializedName("uid")
        var uid: String = "",

        @SerializedName("date")
        var date: String = ""
) : Parcelable {

    companion object {

        val invitationDiffCallback = object : DiffUtil.ItemCallback<Invitation>() {
            override fun areItemsTheSame(oldItem: Invitation, newItem: Invitation): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Invitation, newItem: Invitation): Boolean {
                return oldItem == newItem
            }
        }

        var invitationDeserializer: JsonDeserializer<Invitation> =
                JsonDeserializer { json, _, _ ->
                    val jsonObject: JsonObject = json.asJsonObject
                    Invitation(
                            jsonObject.get("id").asString,
                            jsonObject.get("uid").asString,
                            jsonObject.get("date").asString
                    )
                }
    }
}
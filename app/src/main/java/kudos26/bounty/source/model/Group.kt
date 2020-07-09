package kudos26.bounty.source.model

import android.os.Parcelable
import androidx.recyclerview.widget.DiffUtil
import com.google.gson.JsonDeserializer
import com.google.gson.JsonObject
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

/**
 * Created by kunal on 21-01-2020.
 */

@Parcelize
data class Group(
        @SerializedName("id")
        var id: String = "",

        @SerializedName("name")
        var name: String = "",

        var total: Long = 0
) : Parcelable {

    companion object {

        val groupDiffCallback = object : DiffUtil.ItemCallback<Group>() {
            override fun areItemsTheSame(oldItem: Group, newItem: Group): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Group, newItem: Group): Boolean {
                return oldItem == newItem
            }
        }

        var groupDeserializer: JsonDeserializer<Group> =
                JsonDeserializer { json, _, _ ->
                    val jsonObject: JsonObject = json.asJsonObject
                    Group(
                            jsonObject.get("id").asString,
                            jsonObject.get("name").asString
                    )
                }
    }
}
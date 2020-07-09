package kudos26.bounty.source.model

import com.google.gson.annotations.SerializedName

data class Payload(
        @SerializedName("to")
        var to: String = "",

        @SerializedName("data")
        var data: Any? = null
)
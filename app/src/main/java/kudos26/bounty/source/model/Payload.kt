package kudos26.bounty.source.model

import com.google.gson.annotations.SerializedName

data class Payload(
        @SerializedName("notification")
        var notification: Notification = Notification(),

        @SerializedName("to")
        var to: String = ""
)

data class Notification(
        @SerializedName("title")
        var title: String = "",

        @SerializedName("body")
        var body: String = ""
)
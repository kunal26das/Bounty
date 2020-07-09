package kudos26.bounty.source.model

data class Notification(
        var title: String = "",
        var body: String = "",
        var channel: Channel = Channel()
)
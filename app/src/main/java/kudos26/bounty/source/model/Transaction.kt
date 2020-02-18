package kudos26.bounty.source.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import kudos26.bounty.utils.getCurrentDate

/**
 * Created by kunal on 21-01-2020.
 */

@Parcelize
data class Transaction(
        var amount: Int? = null,
        var comment: String = "",
        var date: String = getCurrentDate(),
        var id: String = "",
        var from: String = "",
        var to: List<Ratio> = emptyList()
) : Parcelable
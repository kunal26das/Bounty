package kudos26.bounty.source.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Created by kunal on 24-01-2020.
 */

@Parcelize
data class Share(
        var name: String = "",
        var amount: Int = 0
) : Parcelable
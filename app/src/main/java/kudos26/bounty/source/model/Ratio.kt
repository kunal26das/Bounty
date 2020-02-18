package kudos26.bounty.source.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Created by kunal on 27-01-2020.
 */

@Parcelize
data class Ratio(
        var uid: String = "",
        var percentage: Double = 0.0
) : Parcelable
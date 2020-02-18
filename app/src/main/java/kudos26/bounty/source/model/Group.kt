package kudos26.bounty.source.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Created by kunal on 21-01-2020.
 */

@Parcelize
data class Group(
        var id: String = "",
        var name: String = ""
) : Parcelable
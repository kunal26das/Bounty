package kudos26.bounty.source.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Created by kunal on 28-01-2020.
 */

@Parcelize
data class Member(
        var uid: String = "",
        var doj: String = "",
        var name: String = "",
        var admin: Boolean = false
) : Parcelable
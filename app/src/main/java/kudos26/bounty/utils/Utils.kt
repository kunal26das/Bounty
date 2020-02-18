package kudos26.bounty.utils

import android.content.res.Resources
import android.util.TypedValue
import kotlin.math.roundToInt

/**
 * Created by kunal on 28-01-2020.
 */

private val resources = Resources.getSystem()

// Density Independent Pixels
fun Float.toDipsInt(): Int {
    return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            this, resources.displayMetrics
    ).roundToInt()
}
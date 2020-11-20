package kudos26.bounty.firebase

import android.app.Activity
import android.content.SharedPreferences
import com.google.android.gms.common.GoogleApiAvailability
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.iid.FirebaseInstanceId
import kudos26.bounty.firebase.Extensions.device
import kudos26.bounty.firebase.Extensions.user
import org.koin.core.KoinComponent
import org.koin.core.inject
import java.util.*

class Firebase private constructor() {

    fun onNewToken(token: String) {
        updateInstanceId(token)
    }

    companion object : KoinComponent {

        private const val KEY_UUID = "UUID"
        private val firebaseAuth: FirebaseAuth by inject()
        private val database: DatabaseReference by inject()
        private val sharedPreferences: SharedPreferences by inject()
        private val firebaseInstanceId: FirebaseInstanceId by inject()
        private val googleApiAvailability: GoogleApiAvailability by inject()
        private val uuid get() = sharedPreferences.getString(KEY_UUID, "")

        private fun initializeUUID() {
            if (uuid.isNullOrBlank()) {
                with(sharedPreferences.edit()) {
                    putString(KEY_UUID, UUID.randomUUID().toString())
                    commit()
                }
            }
        }

        private fun updateInstanceId(token: String) {
            database.user(firebaseAuth.currentUser?.uid!!).device(uuid!!).setValue(token)
        }

        fun initialize(activity: Activity) {
            googleApiAvailability.makeGooglePlayServicesAvailable(activity).addOnSuccessListener {
                initializeUUID()
                firebaseInstanceId.instanceId.addOnSuccessListener {
                    updateInstanceId(it.token)
                }
            }
        }
    }
}
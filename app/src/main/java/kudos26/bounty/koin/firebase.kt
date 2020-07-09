package kudos26.bounty.koin

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessaging
import kudos26.bounty.firebase.Firebase
import org.koin.dsl.module

/**
 * Created by kunal on 06-02-2020.
 */

val firebase = module {
    single { Firebase }
    single { FirebaseAuth.getInstance() }
    single { FirebaseInstanceId.getInstance() }
    single {
        FirebaseDatabase.getInstance().apply {
            setPersistenceEnabled(true)
        }.reference
    }
    single {
        FirebaseMessaging.getInstance().apply {
            isAutoInitEnabled = true
        }
    }
}
package kudos26.bounty.koin

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import org.koin.dsl.module

/**
 * Created by kunal on 06-02-2020.
 */

val firebase = module {
    single { FirebaseAuth.getInstance() }
    single { FirebaseDatabase.getInstance() }
}
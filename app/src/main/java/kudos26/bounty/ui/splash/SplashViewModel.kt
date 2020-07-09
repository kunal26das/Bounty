package kudos26.bounty.ui.splash

import android.content.Intent
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.DatabaseReference
import kudos26.bounty.core.ViewModel
import kudos26.bounty.firebase.Extensions.email
import kudos26.bounty.firebase.Extensions.getValue
import kudos26.bounty.firebase.Extensions.name
import kudos26.bounty.firebase.Extensions.user
import kudos26.bounty.utils.Extensions.Try

/**
 * Created by kunal on 06-02-2020.
 */

class SplashViewModel(
        private val database: DatabaseReference
) : ViewModel() {

    fun googleSignIn(data: Intent?) = Try {
        firebaseAuth.signInWithCredential(
                GoogleAuthProvider.getCredential(GoogleSignIn
                        .getSignedInAccountFromIntent(data)
                        .getResult(ApiException::class.java)!!.idToken, null
                )
        ).addOnSuccessListener {
            currentUser.value = it.user
        }
    }

    fun updateCurrentUser() {
        currentUser.value?.let { user ->
            database.user(user.uid).apply {
                email.setValue(user.email)
                name.getValue({
                    if (!it.exists()) {
                        // TODO Not updating at all
                        name.setValue(user.displayName)
                    }
                })
            }
        }
    }

}
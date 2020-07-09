package kudos26.bounty.ui.splash

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.lifecycle.Observer
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import kudos26.bounty.R
import kudos26.bounty.core.Activity
import kudos26.bounty.firebase.Firebase
import kudos26.bounty.source.model.Channel
import kudos26.bounty.ui.MainActivity
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel

class SplashScreen : Activity() {

    override val viewModel by viewModel<SplashViewModel>()
    private val googleSignInClient: GoogleSignInClient by inject()
    private val notificationManager: NotificationManager by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!resumeApplication()) {
            createNotificationChannel(Channel(R.string.group, getString(R.string.group)))
            createNotificationChannel(Channel(R.string.invitations, getString(R.string.invitations)))
            viewModel.currentUser.observe(this, Observer {
                when (it) {
                    null -> startActivityForResult(googleSignInClient.signInIntent, GOOGLE_SIGN_IN)
                    else -> {
                        Firebase.initialize(this)
                        Intent(this, MainActivity::class.java).apply {
                            viewModel.updateCurrentUser()
                            startActivity(this)
                            finish()
                        }
                    }
                }
            })
        }
    }

    private fun resumeApplication(): Boolean {
        if (!isTaskRoot
                && intent.hasCategory(Intent.CATEGORY_LAUNCHER)
                && intent.action != null
                && intent.action.equals(Intent.ACTION_MAIN)
        ) {
            finish()
            return true
        }
        return false
    }

    private fun createNotificationChannel(channel: Channel) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannel(NotificationChannel(
                    channel.name, channel.name,
                    NotificationManager.IMPORTANCE_DEFAULT
            ))
        }
    }

    // Activity Result
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            GOOGLE_SIGN_IN -> when (resultCode) {
                RESULT_OK -> viewModel.googleSignIn(data)
                else -> finish()
            }
        }

    }

    companion object {
        const val GOOGLE_SIGN_IN = 101
    }

}
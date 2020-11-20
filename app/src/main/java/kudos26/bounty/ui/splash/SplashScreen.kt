package kudos26.bounty.ui.splash

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.Observer
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.FirebaseUser
import kudos26.bounty.R
import kudos26.bounty.core.Activity
import kudos26.bounty.firebase.Firebase
import kudos26.bounty.source.model.Channel
import kudos26.bounty.ui.MainActivity
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel

class SplashScreen : Activity() {

    private val googleSignInClient: GoogleSignInClient by inject()
    private val notificationManager: NotificationManager by inject()
    override val viewModel by viewModel<SplashViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!resumeApplication()) {
            createNotificationChannel(Channel(R.string.group, getString(R.string.group)))
            createNotificationChannel(Channel(R.string.invitations, getString(R.string.invitations)))
            viewModel.currentUser.observe(this, object : Observer<FirebaseUser?> {
                override fun onChanged(it: FirebaseUser?) {
                    when (it) {
                        null -> {
                            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                                if (result.resultCode == RESULT_OK) {
                                    viewModel.googleSignIn(result.data)
                                }
                            }.launch(googleSignInClient.signInIntent)
                        }
                        else -> {
                            Firebase.initialize(this@SplashScreen)
                            viewModel.currentUser.removeObserver(this)
                            Intent(baseContext, MainActivity::class.java).apply {
                                viewModel.updateCurrentUser()
                                startActivity(this)
                                finish()
                            }
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

}
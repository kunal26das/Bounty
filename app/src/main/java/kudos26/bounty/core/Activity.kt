package kudos26.bounty.core

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.common.api.ApiException
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessaging
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import kudos26.bounty.R
import kudos26.bounty.ui.MainActivity

abstract class Activity : AppCompatActivity() {

    abstract val viewModel: ViewModel
    private val firebaseAuth = FirebaseAuth.getInstance()
    private lateinit var googleSignInClient: GoogleSignInClient
    private val firebaseDatabase = FirebaseDatabase.getInstance().reference

    // Activity Life Cycle
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_OVERSCAN)
        googleSignInClient = GoogleSignIn.getClient(baseContext, GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build())
        MobileAds.initialize(this, getString(R.string.admob_app_id))
    }

    override fun onDestroy() {
        CompositeDisposable().clear()
        super.onDestroy()
    }

    // Firebase
    fun switchAccount() {
        googleSignInClient.signOut()
        startActivityForResult(googleSignInClient.signInIntent, GOOGLE_SIGN_IN)
    }

    // Activity Result
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                GOOGLE_SIGN_IN -> {
                    try {
                        val currentUserId = firebaseAuth.uid
                        firebaseAuth.signInWithCredential(
                                GoogleAuthProvider.getCredential(GoogleSignIn
                                        .getSignedInAccountFromIntent(data)
                                        .getResult(ApiException::class.java)!!.idToken,
                                        null
                                )
                        ).addOnCompleteListener(this) { task ->
                            if (!currentUserId.equals(firebaseAuth.uid)) {
                                when {
                                    task.isSuccessful -> startApplication()
                                    else -> LOGIN_FAILURE.snackBarForLongDuration()
                                }
                            }
                        }
                    } catch (e: ApiException) {
                        e.message?.snackBarForLongDuration()
                    }
                }
            }
        }
    }

    // Start Application
    protected fun startApplication() {
        when {
            firebaseAuth.currentUser != null -> {
                updateUserInfo()
                Intent(this, MainActivity::class.java).apply {
                    initFirebaseCloudMessaging()
                    startActivity(this)
                    finish()
                }
            }
            else -> startActivityForResult(googleSignInClient.signInIntent, GOOGLE_SIGN_IN)
        }
    }

    private fun initFirebaseCloudMessaging() {
        GoogleApiAvailability.getInstance().makeGooglePlayServicesAvailable(this)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        FirebaseMessaging.getInstance().isAutoInitEnabled = true
                        FirebaseInstanceId.getInstance().instanceId
                                .addOnSuccessListener {
                                    firebaseDatabase.child("users").child(firebaseAuth.uid!!).child("token").setValue(it.token)
                                }
                    }
                }
    }

    private fun updateUserInfo() {
        firebaseAuth.currentUser?.let {
            firebaseDatabase.child("users").child(it.uid).apply {
                child("email").setValue(it.email)
                child("name").setValue(it.displayName)
            }
        }
    }

    // Disposables
    fun dispose(disposable: Disposable) {
        CompositeDisposable().add(disposable)
    }

    // Theme
    fun switchTheme() {
        isDarkTheme().apply {
            when (this) {
                true -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                false -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }
    }

    private fun isDarkTheme(): Boolean {
        return when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_NO -> true
            Configuration.UI_MODE_NIGHT_YES -> false
            else -> false
        }
    }

    // Status Bar
    val HEIGHT_STATUS_BAR: Int
        get() {
            resources.getIdentifier(
                    getString(R.string.status_bar_height),
                    getString(R.string.dimension),
                    getString(R.string.android)).apply {
                return resources.getDimensionPixelSize(this)
            }
        }

    // Toasts
    fun String.toastForShortDuration(context: Context = this@Activity, duration: Int = Toast.LENGTH_SHORT): Toast {
        return Toast.makeText(context, this, duration).apply { show() }
    }
    fun String.toastForLongDuration(context: Context = this@Activity, duration: Int = Toast.LENGTH_LONG): Toast {
        return Toast.makeText(context, this, duration).apply { show() }
    }

    // SnackBars
    fun String.snackBarForShortDuration(view: View = findViewById(android.R.id.content), duration: Int = Snackbar.LENGTH_SHORT): Snackbar {
        return Snackbar.make(view, this, duration).apply { show() }
    }

    fun String.snackBarForLongDuration(view: View = findViewById(android.R.id.content), duration: Int = Snackbar.LENGTH_LONG): Snackbar {
        return Snackbar.make(view, this, duration).apply { show() }
    }

    fun String.snackBarForIndefiniteDuration(view: View = findViewById(android.R.id.content), duration: Int = Snackbar.LENGTH_INDEFINITE): Snackbar {
        return Snackbar.make(view, this, duration).apply { show() }
    }

    companion object {
        const val GOOGLE_SIGN_IN = 101
        const val LOGIN_FAILURE = "Login Failure"
    }
}


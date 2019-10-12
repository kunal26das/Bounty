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
import androidx.appcompat.widget.SwitchCompat
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import kudos26.bounty.R
import kudos26.bounty.ui.main.MainActivity

abstract class Activity : AppCompatActivity(), View.OnClickListener {

    private lateinit var googleSignInClient: GoogleSignInClient

    // Activity Life Cycle
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )
        googleSignInClient = GoogleSignIn.getClient(baseContext, GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build())
        firebaseAuth = FirebaseAuth.getInstance()
    }

    override fun onDestroy() {
        CompositeDisposable().clear()
        super.onDestroy()
    }

    // Activity Result
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                GOOGLE_SIGN_IN -> {
                    try {
                        firebaseAuth.signInWithCredential(
                                GoogleAuthProvider.getCredential(GoogleSignIn
                                        .getSignedInAccountFromIntent(data)
                                        .getResult(ApiException::class.java)!!.idToken,
                                        null
                                )
                        ).addOnCompleteListener(this) { task ->
                            when {
                                task.isSuccessful -> startApplication()
                                else -> LOGIN_FAILURE.snackBarForLongDuration()
                            }
                        }
                    } catch (e: ApiException) {
                        e.message?.snackBarForLongDuration()
                    }
                }
            }
        }
    }

    // Firebase
    fun switchAccount() {
        googleSignInClient.signOut()
        startActivityForResult(googleSignInClient.signInIntent, GOOGLE_SIGN_IN)
    }

    // View Click Handler
    override fun onClick(view: View?) {}

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
    val statusBarHeight: Int
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

    private fun String.snackBarForLongDuration(view: View = findViewById(android.R.id.content), duration: Int = Snackbar.LENGTH_LONG): Snackbar {
        return Snackbar.make(view, this, duration).apply { show() }
    }

    private fun String.snackBarForIndefiniteDuration(view: View = findViewById(android.R.id.content), duration: Int = Snackbar.LENGTH_INDEFINITE): Snackbar {
        return Snackbar.make(view, this, duration).apply { show() }
    }

    // Switch
    fun SwitchCompat.onCheckedChangeListener(boolean: (Boolean) -> Unit) {
        this.setOnCheckedChangeListener { _, isChecked ->
            boolean(isChecked)
        }
    }

    // Start Application
    fun startApplication() {
        firebaseAuth.currentUser.let {
            when {
                it != null -> Intent(this, MainActivity::class.java).apply {
                    startActivity(this)
                    finish()
                }
                else -> startActivityForResult(googleSignInClient.signInIntent, GOOGLE_SIGN_IN)
            }
        }
    }

    companion object {
        lateinit var firebaseAuth: FirebaseAuth
        private const val GOOGLE_SIGN_IN = 101
        private const val LOGIN_FAILURE = "Login Failure"
    }

}


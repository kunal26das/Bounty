package kudos26.bounty

import android.app.NotificationManager
import android.content.Context
import android.view.inputmethod.InputMethodManager
import androidx.multidex.MultiDexApplication
import com.facebook.stetho.Stetho
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.GoogleApiAvailability
import com.google.firebase.analytics.FirebaseAnalytics
import com.jakewharton.threetenabp.AndroidThreeTen
import kudos26.bounty.koin.firebase
import kudos26.bounty.koin.networking
import kudos26.bounty.koin.repositories
import kudos26.bounty.koin.viewModels
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.dsl.module

class Application : MultiDexApplication() {

    private val systemServices = module {
        single { getSystemService(InputMethodManager::class.java) } // Input Method Manager
        single { getSystemService(NotificationManager::class.java) } // Notification Manager
    }

    private val dependencies = module {
        single { resources } // Resources
        single { GoogleApiAvailability.getInstance() } // Google API Availability
        single { getSharedPreferences(packageName, Context.MODE_PRIVATE) } // Shared Preferences
        single { // Sign In with Google
            GoogleSignIn.getClient(applicationContext, GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.default_web_client_id))
                    .requestEmail()
                    .build())
        }
    }

    override fun onCreate() {
        super.onCreate()
        AndroidThreeTen.init(applicationContext)
        MobileAds.initialize(applicationContext)
        FirebaseAnalytics.getInstance(applicationContext)
        Stetho.initializeWithDefaults(applicationContext)
        startKoin {
            androidContext(applicationContext)
            modules(listOf(systemServices))
            modules(listOf(dependencies))
            modules(listOf(repositories))
            modules(listOf(viewModels))
            modules(listOf(networking))
            modules(listOf(firebase))
        }
    }

}
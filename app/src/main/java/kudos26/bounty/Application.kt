package kudos26.bounty

import android.app.Application
import com.facebook.stetho.Stetho
import kudos26.bounty.koin.firebase
import kudos26.bounty.koin.networking
import kudos26.bounty.koin.repositories
import kudos26.bounty.koin.viewModels
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class Application : Application() {

    override fun onCreate() {
        super.onCreate()
        Stetho.initializeWithDefaults(this)
        startKoin {
            androidContext(this@Application)
            modules(listOf(repositories))
            modules(listOf(viewModels))
            modules(listOf(networking))
            modules(listOf(firebase))
        }
    }

}
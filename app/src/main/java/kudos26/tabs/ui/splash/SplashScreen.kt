package kudos26.tabs.ui.splash

import android.content.Intent
import android.os.Bundle
import io.reactivex.Completable
import kudos26.tabs.R
import kudos26.tabs.core.Activity
import kudos26.tabs.ui.main.MainActivity
import java.util.concurrent.TimeUnit

class SplashScreen : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash_screen)

        dispose(Completable.complete().delay(1, TimeUnit.SECONDS).subscribe {
            Intent(this, MainActivity::class.java).apply {
                startActivity(this)
            }
            finish()
        })
    }

}
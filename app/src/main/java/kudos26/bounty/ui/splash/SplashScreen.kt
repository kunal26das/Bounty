package kudos26.bounty.ui.splash

import android.os.Bundle
import kudos26.bounty.core.Activity
import org.koin.android.viewmodel.ext.android.viewModel

class SplashScreen : Activity() {

    override val viewModel by viewModel<SplashViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startApplication()
    }

}
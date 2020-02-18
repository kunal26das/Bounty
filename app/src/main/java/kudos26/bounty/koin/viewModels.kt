package kudos26.bounty.koin

import kudos26.bounty.ui.MainViewModel
import kudos26.bounty.ui.splash.SplashViewModel
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

/**
 * Created by kunal on 06-02-2020.
 */

val viewModels = module {
    viewModel { SplashViewModel(get(), get()) }
    viewModel { MainViewModel(get(), get(), get()) }
}
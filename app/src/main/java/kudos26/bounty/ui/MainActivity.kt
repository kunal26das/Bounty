package kudos26.bounty.ui

import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.navigateUp
import com.microsoft.officeuifabric.util.ThemeUtil
import kotlinx.android.synthetic.main.activity_main.*
import kudos26.bounty.R
import kudos26.bounty.core.Activity
import org.koin.android.viewmodel.ext.android.viewModel

class MainActivity : Activity(), NavController.OnDestinationChangedListener {

    private lateinit var navController: NavController
    private val navigationSet = setOf(R.id.groups)
    private lateinit var appBarConfiguration: AppBarConfiguration
    override val viewModel by viewModel<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initToolbar()
        initNavigation()
    }

    private fun initToolbar() {
        setSupportActionBar(appBar.toolbar)
    }

    private fun initNavigation() {
        (supportFragmentManager.findFragmentById(R.id.appNavigationFragment) as NavHostFragment).navController.let {
            navController = it
            appBarConfiguration = AppBarConfiguration(it.graph)
            appBarConfiguration = AppBarConfiguration(navigationSet)
            NavigationUI.setupActionBarWithNavController(this, it)
            it.addOnDestinationChangedListener(this)
        }
    }

    override fun initObservers() {
        super.initObservers()
        viewModel.title.observe(this, Observer {
            appBar.toolbar.title = it
        })
        viewModel.subtitle.observe(this, Observer {
            appBar.toolbar.subtitle = it
        })
    }

    override fun onSupportNavigateUp(): Boolean {
        return findNavController(R.id.appNavigationFragment).navigateUp(appBarConfiguration) and super.onSupportNavigateUp()
    }

    override fun onDestinationChanged(controller: NavController, destination: NavDestination, arguments: Bundle?) {
        appBar.toolbar.navigationIcon = if (destination.id !in navigationSet) {
            val backArrow = ContextCompat.getDrawable(this, R.drawable.ms_ic_arrow_left_24_filled)
            backArrow?.setTint(ThemeUtil.getThemeAttrColor(this, R.attr.uifabricToolbarIconColor))
            backArrow
        } else null
        hideKeyboard()
        viewModel.subtitle.value = ""
    }

}

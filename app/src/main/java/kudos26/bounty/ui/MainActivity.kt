package kudos26.bounty.ui

import android.os.Bundle
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import kotlinx.android.synthetic.main.toolbar.*
import kudos26.bounty.R
import kudos26.bounty.core.Activity
import org.koin.android.viewmodel.ext.android.viewModel

class MainActivity : Activity() {

    override val viewModel by viewModel<MainViewModel>()
    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initToolbar()
        initNavigation()
    }

    override fun onStart() {
        super.onStart()
        viewModel.getNumberOfGroups()
    }

    private fun initToolbar() {
        (appBarLayout?.layoutParams as LinearLayoutCompat.LayoutParams).apply {
            setMargins(0, HEIGHT_STATUS_BAR, 0, 0)
        }
        setSupportActionBar(toolbar)
    }

    private fun initNavigation() {
        val navController = (supportFragmentManager.findFragmentById(R.id.app_navigation_fragment) as NavHostFragment).navController
        appBarConfiguration = AppBarConfiguration(navController.graph)
        appBarConfiguration = AppBarConfiguration(setOf(R.id.groups))
        setupActionBarWithNavController(navController, appBarConfiguration)
    }

    override fun onSupportNavigateUp(): Boolean {
        return findNavController(R.id.app_navigation_fragment).navigateUp(appBarConfiguration)
    }

}
